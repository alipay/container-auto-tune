/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.base.h2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.h2.tools.RunScript;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static final String     DATA_PATH = "sql/data.sql";
    private static final String     url       = "jdbc:h2:/tmp/h2db/tmaestro";
    private static final String     userName  = "sa";
    private static final String     password  = "";
    private static       Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            log.error("create h2 connection occurs an error.", e);
            throw new RuntimeException();
        }
        try {
            initRDBTables();
        } catch (Exception e) {
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

    public static boolean closeConn() {
        try {
            log.info("****  Start to close H2 Conn  ****");
            Objects.requireNonNull(connection, "connection is empty.").close();
            return connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initRDBTables() throws Exception {
        log.info("*****  start to init RDB tables.  *****");
        InputStream resourceAsStream = H2DBManagerImpl.class.getClassLoader().getResourceAsStream(DATA_PATH);
        log.info("start to copy file");
        File file = new File("/tmp/data.sql");
        FileUtils.copyInputStreamToFile(resourceAsStream, file);
        RunScript.execute(url, userName, password, file.getAbsolutePath(), StandardCharsets.UTF_8, false);
        log.info("*****  end to init RDB tables.  *****");
    }
}