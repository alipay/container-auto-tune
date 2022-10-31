/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.h2;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.RunScript;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author huangkaifei
 * @version : H2DBManagerImpl.java, v 0.1 2022年10月26日 11:35 AM huangkaifei Exp $
 */
@Slf4j
@Service
public class H2DBManagerImpl implements H2DBManager {

    private static final String     url        = "jdbc:h2:/tmp/h2db/tmaestro";
    private static final String     userName   = "sa";
    private static final String     password   = "";
    private static       Connection connection = null;

    static {
        try {
            connection = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            log.error("create h2 connection occurs an error.", e);
            throw new RuntimeException();
        }
        try {
            initRDBTables();
            initNoSqlTables();
        }catch (Exception e){
            log.error("int sql occurs an error.", e);

        }

    }

    @Override
    public Connection getConnection() throws Exception {
        return Objects.requireNonNull(connection, "H2DBManager creates h2 connection failed.");
    }

    @Override
    public boolean closeConnection() throws Exception {
        Objects.requireNonNull(connection, "connection is empty.").close();
        return connection.isClosed();
    }

    public static boolean closeConn() throws SQLException {
        log.info("****      Start to close H2 Conn ****");
        Objects.requireNonNull(connection, "connection is empty.").close();
        return connection.isClosed();
    }

    private static void initRDBTables() throws SQLException, IOException {
        log.info("*****  start to init RDB tables.  *****");
        Resource initData = new ClassPathResource("sql/data.sql");
        RunScript.execute(url, userName, password, initData.getFile().getAbsolutePath(), null, false);
        log.info("*****  end to init RDB tables.  *****");
    }

    private static void initNoSqlTables() {
        log.info("*****  start to init NoSql tables.  *****");
    }
}