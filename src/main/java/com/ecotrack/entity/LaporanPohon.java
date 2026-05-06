package com.ecotrack.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LaporanPohon {
    private String idLaporan;
    private String idUser;
    private String idPohon;
    private String kondisi;
    private String lokasi;
    private String fileFotoPath;
    private Float estimasiKarbon;
    private LocalDate tanggalLaporan;
    private LocalDateTime createdAt;

    public LaporanPohon() {
    }

    public LaporanPohon(String idLaporan, String kondisi, String lokasi,
                        LocalDate tanggalLaporan) {
        this.idLaporan = idLaporan;
        this.kondisi = kondisi;
        this.lokasi = lokasi;
        this.tanggalLaporan = tanggalLaporan;
    }

    public String getIdLaporan() {
        return idLaporan;
    }

    public void setIdLaporan(String idLaporan) {
        this.idLaporan = idLaporan;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPohon() {
        return idPohon;
    }

    public void setIdPohon(String idPohon) {
        this.idPohon = idPohon;
    }

    public String getKondisi() {
        return kondisi;
    }

    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getFileFotoPath() {
        return fileFotoPath;
    }

    public void setFileFotoPath(String fileFotoPath) {
        this.fileFotoPath = fileFotoPath;
    }

    public Float getEstimasiKarbon() {
        return estimasiKarbon;
    }

    public void setEstimasiKarbon(Float estimasiKarbon) {
        this.estimasiKarbon = estimasiKarbon;
    }

    public LocalDate getTanggalLaporan() {
        return tanggalLaporan;
    }

    public void setTanggalLaporan(LocalDate tanggalLaporan) {
        this.tanggalLaporan = tanggalLaporan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
