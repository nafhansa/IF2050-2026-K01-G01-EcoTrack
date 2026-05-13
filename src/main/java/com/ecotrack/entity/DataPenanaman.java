package com.ecotrack.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecotrack.util.DBConnection;

public class DataPenanaman {
    private String idPenanaman;
    private String lokasi;
    private String jenisPohon;
    private int jumlahPohon;
    private Date tanggal;
    private float estimasiKarbon;

    public String getIdPenanaman() { return idPenanaman; }
    public void setIdPenanaman(String idPenanaman) { this.idPenanaman = idPenanaman; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getJenisPohon() { return jenisPohon; }
    public void setJenisPohon(String jenisPohon) { this.jenisPohon = jenisPohon; }

    public int getJumlahPohon() { return jumlahPohon; }
    public void setJumlahPohon(int jumlahPohon) { this.jumlahPohon = jumlahPohon; }

    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }

    public float getEstimasiKarbon() { return estimasiKarbon; }
    public void setEstimasiKarbon(float estimasiKarbon) { this.estimasiKarbon = estimasiKarbon; }

    public void cariData(String idPenanaman) {
        // Q-001 or Q-002
    }

    public void simpanData(DataPenanaman data) {
        // Q-003: INSERT INTO data_penanaman
        String sql = "INSERT INTO data_penanaman (lokasi, jenis_pohon, jumlah_pohon, tanggal, estimasi_karbon) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            
            pstmt.setString(1, this.lokasi);
            pstmt.setString(2, this.jenisPohon);
            pstmt.setInt(3, this.jumlahPohon);
            pstmt.setDate(4, new java.sql.Date(this.tanggal.getTime()));
            pstmt.setFloat(5, this.estimasiKarbon);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal simpan penanaman: " + e.getMessage());
        }
    }

    public void ubahData(DataPenanaman data) {
        // Q-004: UPDATE data_penanaman
    }

    public void hapusData(String idPenanaman) {
        // Q-005: DELETE FROM data_penanaman
    }

    public List<DataPenanaman> getDataPenanaman() {
        // Q-001: SELECT * FROM data_penanaman ORDER BY tanggal DESC
        List<DataPenanaman> list = new ArrayList<>();
        String sql = "SELECT * FROM data_penanaman ORDER BY tanggal DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DataPenanaman d = new DataPenanaman();
                d.setLokasi(rs.getString("lokasi"));
                d.setJenisPohon(rs.getString("jenis_pohon"));
                d.setJumlahPohon(rs.getInt("jumlah_pohon"));
                d.setTanggal(rs.getDate("tanggal"));
                d.setEstimasiKarbon(rs.getFloat("estimasi_karbon"));
                list.add(d);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }
}
