/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.base.h2;

import java.sql.Connection;

/**
 * @author huangkaifei
 * @version : H2DBManager.java, v 0.1 2022年10月26日 11:40 AM huangkaifei Exp $
 */
public interface H2DBManager {

    /**
     * Get connection to h2 database.
     *
     * @return
     * @throws Exception
     */
    Connection getConnection() throws Exception;

    /**
     * Close connection to h2 database.
     *
     * @return
     * @throws Exception
     */
    boolean closeConnection() throws Exception;
}