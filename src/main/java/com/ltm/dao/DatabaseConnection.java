package com.ltm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/ltm_final_project?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "ltm_app_user";
    private static final String DEFAULT_PASSWORD = "ChangeMe123!";

    private static final String URL = resolve("LTM_DB_URL", "ltm.db.url", DEFAULT_URL);
    private static final String USER = resolve("LTM_DB_USER", "ltm.db.user", DEFAULT_USER);
    private static final String PASSWORD = resolve("LTM_DB_PASSWORD", "ltm.db.password", DEFAULT_PASSWORD);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String resolve(String envKey, String propertyKey, String fallback) {
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) {
            value = System.getProperty(propertyKey);
        }
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
