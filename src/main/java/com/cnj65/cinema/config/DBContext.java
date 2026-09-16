package com.cnj65.cinema.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Quan ly ket noi CSDL bang HikariCP Connection Pool.
 * Doc cau hinh tu file db.properties (src/main/resources).
 *
 * KHONG dung DriverManager.getConnection() truc tiep trong DAO
 * vi se tao/dong ket noi lien tuc, rat ton tai nguyen.
 */
public class DBContext {

    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream is = DBContext.class.getClassLoader()
                    .getResourceAsStream("db.properties")) {
                if (is == null) {
                    throw new RuntimeException("Khong tim thay file db.properties trong classpath");
                }
                props.load(is);
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setDriverClassName(props.getProperty("db.driver"));

            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setPoolName("Cinema65Pool");

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Loi khoi tao Connection Pool: " + e.getMessage(), e);
        }
    }

    private DBContext() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
