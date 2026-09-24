package com.carsharing.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnectionManager {

    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream input = DbConnectionManager.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    throw new RuntimeException("Unable to find db.properties in the classpath.");
                }
                props.load(input);
            }

            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));

            // Pool settings
            int maxSize = Integer.parseInt(props.getProperty("db.pool.max-size", "10"));
            long idleTimeout = Long.parseLong(props.getProperty("db.pool.idle-timeout", "600000"));
            long connTimeout = Long.parseLong(props.getProperty("db.pool.connection-timeout", "30000"));

            config.setMaximumPoolSize(maxSize);
            config.setIdleTimeout(idleTimeout);
            config.setConnectionTimeout(connTimeout);

            // Optimized settings for MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Database connection pool initialization failed: " + e.getMessage());
        }
    }

    /**
     * Retrieves a database connection from the connection pool.
     * @return Connection object.
     * @throws SQLException if a connection cannot be established.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the data source connection pool.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
