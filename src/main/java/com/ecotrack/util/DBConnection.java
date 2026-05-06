package com.ecotrack.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String ENV_FILE = ".env";

    private static String HOST;
    private static String PORT;
    private static String DB;
    private static String USER;
    private static String PASS;
    private static String URL;

    private static Connection instance;

    static {
        loadEnvConfig();
    }

    private static void loadEnvConfig() {
        Properties props = new Properties();

        Path envPath = Path.of(ENV_FILE);
        if (Files.exists(envPath)) {
            try (InputStream input = Files.newInputStream(envPath)) {
                props.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load .env file", e);
            }
        }

        HOST = props.getProperty("DB_HOST", "localhost");
        PORT = props.getProperty("DB_PORT", "5432");
        DB = props.getProperty("DB_NAME", "ecotrack_db");
        USER = props.getProperty("DB_USER", "ecotrack_user");
        PASS = props.getProperty("DB_PASS", "ecotrack_pass");

        URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASS);
        }
        return instance;
    }

    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
