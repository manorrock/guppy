/*
 *  Copyright (c) 2002-2021, Manorrock.com. All Rights Reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      1. Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package com.manorrock.guppy.environment;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;

/**
 * A JDBC driver that looks for environment variables to delegate to a another
 * JDBC driver.
 *
 * <p>
 * E.g. if you set the JDBC url in your application to be jdbc:environment:0 it
 * will look for environment variables of the format:
 * </p>
 * <pre>
 *    GUPPY_ENVIRONMENT.0.URL=
 *    GUPPY_ENVIRONMENT.0.PROPERTY.1=name=value
 *    GUPPY_ENVIRONMENT.0.PROPERTY.2=name2=value2
 * </pre>
 *
 * @author Manfred Riem (mriem@manorrock.com)
 */
public class EnvironmentDriver implements Driver {
    
    /**
     * Stores the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(EnvironmentDriver.class.getPackage().getName());

    /**
     * Constructor.
     */
    public EnvironmentDriver() {
        try {
            DriverManager.registerDriver(this);
        } catch (SQLException se) {
            LOGGER.log(SEVERE, "SQL error occured while registering the JDBC driver", se);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        String delegateUrl = getDelegateUrl(url);
        Properties delegateInfo = new Properties();
        Driver driver = DriverManager.getDriver(delegateUrl);
        return driver.connect(delegateUrl, delegateInfo);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.contains("jdbc:environment:");
    }
    
    /**
     * Get the delegate name.
     * 
     * @return the delegate name.
     */
    private String getDelegateName(String url) {
        String delegateName = url.substring("jdbc:environment:".length());
        LOGGER.log(INFO, "Delegate name: {0}", delegateName);
        return delegateName;
    }
    
    /**
     * Get the delegate URL.
     * 
     * @return the delegate URL.
     */
    private String getDelegateUrl(String url) {
        String delegateName = getDelegateName(url);
        String delegateUrl = System.getenv("GUPPY_ENVIRONMENT." + delegateName + ".URL");
        LOGGER.log(INFO, "Delegate URL: {0}", delegateUrl);
        return delegateUrl;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        String delegateUrl = getDelegateUrl(url);
        Properties delegateInfo = new Properties();
        Driver delegateDriver = DriverManager.getDriver(delegateUrl);
        return delegateDriver.getPropertyInfo(delegateUrl, delegateInfo);
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(EnvironmentDriver.class.getPackage().getName());
    }
}
