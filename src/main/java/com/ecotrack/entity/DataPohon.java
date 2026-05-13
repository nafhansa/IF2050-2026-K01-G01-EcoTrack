package com.ecotrack.entity;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecotrack.util.DBConnection;

public class DataPohon {
    private String idPohon;
    private String idUser;
    private String namaPohon;
    private int usia;
    private String lokasi;
    private float serapanKarbon;
    private String status;
    private String fileFoto;

    public String getIdPohon() { return idPohon; }
    public void setIdPohon(String idPohon) { this.idPohon = idPohon; }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getNamaPohon() { return namaPohon; }
    public void setNamaPohon(String namaPohon) { this.namaPohon = namaPohon; }

    public int getUsia() { return usia; }
    public void setUsia(int usia) { this.usia = usia; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public float getSerapanKarbon() { return serapanKarbon; }
    public void setSerapanKarbon(float serapanKarbon) { this.serapanKarbon = serapanKarbon; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFileFoto() { return fileFoto; }
    public void setFileFoto(String fileFoto) { this.fileFoto = fileFoto; }

    public void cariDataPohon(String kriteria) {
        // Q-008: SELECT * FROM data_pohon WHERE id_pohon=? OR nama_pohon ILIKE ?
    }

    public List<DataPohon> getDataPohon() {
        // Q-007: SELECT * FROM data_pohon ORDER BY nama_pohon ASC
        List<DataPohon> list = new ArrayList<>();
        String sql = "SELECT * FROM data_pohon ORDER BY nama_pohon ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                DataPohon p = new DataPohon();
                p.setIdPohon(rs.getString("id_pohon"));
                p.setIdUser(rs.getString("id_user"));
                p.setNamaPohon(rs.getString("nama_pohon"));
                p.setUsia(rs.getInt("usia"));
                p.setLokasi(rs.getString("lokasi"));
                p.setSerapanKarbon(rs.getFloat("kapasitas_serapan_karbon"));
                p.setStatus(rs.getString("status"));
                p.setFileFoto(rs.getString("file_foto_path"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Gagal ambil data pohon: " + e.getMessage());
        }
        return list;
    
    }

    public void simpanData(DataPohon data) {
        // Q-009: INSERT INTO data_pohon
        String sql = "INSERT INTO data_pohon (id_pohon, id_user, nama_pohon, usia, lokasi, kapasitas_serapan_karbon, status, file_foto_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, data.getIdPohon() != null ? data.getIdPohon() : java.util.UUID.randomUUID().toString());
            pstmt.setString(2, data.getIdUser());
            pstmt.setString(3, data.getNamaPohon());
            pstmt.setInt(4, data.getUsia());
            pstmt.setString(5, data.getLokasi());
            pstmt.setFloat(6, data.getSerapanKarbon());
            pstmt.setString(7, data.getStatus());
            pstmt.setString(8, data.getFileFoto());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal simpan data pohon: " + e.getMessage());
        }
    }

    public void ubahData(DataPohon data) {
        // Q-010: UPDATE data_pohon
        String sql = "UPDATE data_pohon SET id_user = ?, nama_pohon = ?, usia = ?, lokasi = ?, kapasitas_serapan_karbon = ?, status = ?, file_foto_path = ? WHERE id_pohon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, data.getIdUser());
            pstmt.setString(2, data.getNamaPohon());
            pstmt.setInt(3, data.getUsia());
            pstmt.setString(4, data.getLokasi());
            pstmt.setFloat(5, data.getSerapanKarbon());
            pstmt.setString(6, data.getStatus());
            pstmt.setString(7, data.getFileFoto());
            pstmt.setString(8, data.getIdPohon());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void hapusData(String idPohon) {
        // Q-011: DELETE FROM data_pohon WHERE id_pohon=?
        String sql = "DELETE FROM data_pohon WHERE id_pohon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idPohon);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void simpanDataPohon(Object data) {
        // Wrapper untuk simpan data pohon
    }

    public void simpanFoto(File file) {
        // Simpan foto ke local folder
    }
}
