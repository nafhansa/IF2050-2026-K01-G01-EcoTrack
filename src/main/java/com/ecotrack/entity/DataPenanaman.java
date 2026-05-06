package com.ecotrack.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DataPenanaman {
    private String idPenanaman;
    private String idUser;
    private String idPohon;
    private String lokasi;
    private String jenisPohon;
    private int jumlahPohon;
    private LocalDate tanggalPenanaman;
    private Float estimasiKarbon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DataPenanaman() {
    }

    public DataPenanaman(String idPenanaman, String lokasi, String jenisPohon,
                         int jumlahPohon, LocalDate tanggalPenanaman) {
        this.idPenanaman = idPenanaman;
        this.lokasi = lokasi;
        this.jenisPohon = jenisPohon;
        this.jumlahPohon = jumlahPohon;
        this.tanggalPenanaman = tanggalPenanaman;
    }

    public String getIdPenanaman() {
        return idPenanaman;
    }

    public void setIdPenanaman(String idPenanaman) {
        this.idPenanaman = idPenanaman;
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

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getJenisPohon() {
        return jenisPohon;
    }

    public void setJenisPohon(String jenisPohon) {
        this.jenisPohon = jenisPohon;
    }

    public int getJumlahPohon() {
        return jumlahPohon;
    }

    public void setJumlahPohon(int jumlahPohon) {
        this.jumlahPohon = jumlahPohon;
    }

    public LocalDate getTanggalPenanaman() {
        return tanggalPenanaman;
    }

    public void setTanggalPenanaman(LocalDate tanggalPenanaman) {
        this.tanggalPenanaman = tanggalPenanaman;
    }

    public Float getEstimasiKarbon() {
        return estimasiKarbon;
    }

    public void setEstimasiKarbon(Float estimasiKarbon) {
        this.estimasiKarbon = estimasiKarbon;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
