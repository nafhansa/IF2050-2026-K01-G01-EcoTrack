package com.ecotrack.controller;

import com.ecotrack.entity.User;
import com.ecotrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    public List<User> findAll() {
        // Ambil semua user untuk dropdown di halaman login.
        List<User> users = new ArrayList<>();
        String sql = "SELECT id_user, nama, role, password FROM \"user\" ORDER BY nama";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setIdUser(rs.getString("id_user"));
                u.setNama(rs.getString("nama"));
                u.setRole(rs.getString("role"));
                u.setPassword(rs.getString("password"));
                users.add(u);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return users;
    }

    public User findById(String id) {
        // Ambil detail user (termasuk password) untuk proses autentikasi.
        String sql = "SELECT id_user, nama, role, password FROM \"user\" WHERE id_user = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setIdUser(rs.getString("id_user"));
                    u.setNama(rs.getString("nama"));
                    u.setRole(rs.getString("role"));
                    u.setPassword(rs.getString("password"));
                    return u;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean authenticate(String id, String password) {
        // Autentikasi sederhana (plain text) sesuai kebutuhan tugas.
        // Jika suatu saat ingin ditingkatkan, idealnya password di-hash.
        User u = findById(id);
        if (u == null) return false;
        if (u.getPassword() == null) return false;
        return u.getPassword().equals(password);
    }
}
