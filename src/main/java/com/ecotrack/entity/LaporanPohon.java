package com.ecotrack.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ecotrack.util.DBConnection;

public class LaporanPohon {
    private String idLaporan;
    private String idUser;
    private String idPohon;
    private String kondisi;
    private String lokasi;
    private String fileFoto;
    private float estimasiKarbon;

    public String getIdLaporan() { return idLaporan; }
    public void setIdLaporan(String idLaporan) { this.idLaporan = idLaporan; }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getIdPohon() { return idPohon; }
    public void setIdPohon(String idPohon) { this.idPohon = idPohon; }

    public String getKondisi() { return kondisi; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getFileFoto() { return fileFoto; }
    public void setFileFoto(String fileFoto) { this.fileFoto = fileFoto; }

    public float getEstimasiKarbon() { return estimasiKarbon; }
    public void setEstimasiKarbon(float estimasiKarbon) { this.estimasiKarbon = estimasiKarbon; }

    public void simpanLaporan(LaporanPohon data) {
        String sql = "INSERT INTO laporan_pohon (id_laporan, id_user, id_pohon, kondisi, lokasi, file_foto, estimasi_karbon, tanggal_laporan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, data.getIdLaporan() != null ? data.getIdLaporan() : java.util.UUID.randomUUID().toString());
            pstmt.setString(2, data.getIdUser());
            pstmt.setString(3, data.getIdPohon());
            pstmt.setString(4, data.getKondisi());
            pstmt.setString(5, data.getLokasi());
            pstmt.setString(6, data.getFileFoto());
            pstmt.setFloat(7, data.getEstimasiKarbon());
            pstmt.setDate(8, java.sql.Date.valueOf(LocalDate.now()));
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal simpan laporan pohon: " + e.getMessage());
        }
    }

    public List<LaporanPohon> getLaporanPohon() {
        List<LaporanPohon> list = new ArrayList<>();
        String sql = "SELECT * FROM laporan_pohon ORDER BY tanggal_laporan DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LaporanPohon l = new LaporanPohon();
                l.setIdLaporan(rs.getString("id_laporan"));
                l.setIdUser(rs.getString("id_user"));
                l.setIdPohon(rs.getString("id_pohon"));
                l.setKondisi(rs.getString("kondisi"));
                l.setLokasi(rs.getString("lokasi"));
                l.setFileFoto(rs.getString("file_foto"));
                l.setEstimasiKarbon(rs.getFloat("estimasi_karbon"));
                list.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Gagal ambil data laporan pohon: " + e.getMessage());
        }
        return list;
    }
}
