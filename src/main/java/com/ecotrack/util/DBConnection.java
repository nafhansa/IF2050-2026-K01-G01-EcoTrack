package com.ecotrack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Konfigurasi koneksi DB.
    // Saat ini nilainya hard-coded agar mudah dijalankan saat development.
    // (Jika mau, ini bisa diarahkan ke env var/.env agar lebih fleksibel.)
    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String DB   = "ecotrack_db";
    private static final String USER = "ecotrack_user";
    private static final String PASS = "ecotrack_pass";

    private static final String URL =
        "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;

    private static Connection instance;

    public static Connection getConnection() throws SQLException {
        // Pola singleton ringan: 1 koneksi dipakai ulang.
        // Tujuannya mengurangi overhead pembuatan koneksi berulang kali.
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASS);
        }
        return instance;
    }
}
