package com.ecotrack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String DB   = "ecotrack_db";
    private static final String USER = "ecotrack_user";
    private static final String PASS = "ecotrack_pass";

    private static final String URL =
        "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;

    private static Connection instance;

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASS);
        }
        return instance;
    }
}
