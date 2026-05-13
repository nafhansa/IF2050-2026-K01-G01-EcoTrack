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
    private String namaPohon;
    private int usia;
    private String lokasi;
    private float serapanKarbon;
    private String fileFoto;

    public String getIdPohon() { return idPohon; }
    public void setIdPohon(String idPohon) { this.idPohon = idPohon; }

    public String getNamaPohon() { return namaPohon; }
    public void setNamaPohon(String namaPohon) { this.namaPohon = namaPohon; }

    public int getUsia() { return usia; }
    public void setUsia(int usia) { this.usia = usia; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public float getSerapanKarbon() { return serapanKarbon; }
    public void setSerapanKarbon(float serapanKarbon) { this.serapanKarbon = serapanKarbon; }

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
                p.setNamaPohon(rs.getString("nama_pohon"));
                p.setUsia(rs.getInt("usia"));
                p.setSerapanKarbon(rs.getFloat("serapan_karbon"));
                p.setFileFoto(rs.getString("file_foto"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Gagal ambil data pohon: " + e.getMessage());
        }
        return list;
    
    }

    public void simpanData(DataPohon data) {
        // Q-009: INSERT INTO data_pohon
        String sql = "INSERT INTO data_pohon (id_pohon, nama_pohon, usia, serapan_karbon, file_foto) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, java.util.UUID.randomUUID().toString());
            pstmt.setString(2, this.namaPohon);
            pstmt.setInt(3, this.usia);
            pstmt.setFloat(4, this.serapanKarbon);
            pstmt.setString(5, this.fileFoto);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal simpan data pohon: " + e.getMessage());
        }
    }

    public void ubahData(DataPohon data) {
        // Q-010: UPDATE data_pohon
        String sql = "UPDATE data_pohon SET nama_pohon = ?, usia = ?, serapan_karbon = ?, file_foto = ? WHERE id_pohon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.namaPohon);
            pstmt.setInt(2, this.usia);
            pstmt.setFloat(3, this.serapanKarbon);
            pstmt.setString(4, this.fileFoto);
            pstmt.setString(5, this.idPohon);
            
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
