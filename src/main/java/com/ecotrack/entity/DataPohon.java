package com.ecotrack.entity;

import java.time.LocalDateTime;

public class DataPohon {
    private String idPohon;
    private String idUser;
    private String namaPohon;
    private Integer usia;
    private String lokasi;
    private Float kapasitasSerapanKarbon;
    private String status;
    private String fileFotoPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DataPohon() {
    }

    public DataPohon(String idPohon, String namaPohon, Float kapasitasSerapanKarbon) {
        this.idPohon = idPohon;
        this.namaPohon = namaPohon;
        this.kapasitasSerapanKarbon = kapasitasSerapanKarbon;
    }

    public String getIdPohon() {
        return idPohon;
    }

    public void setIdPohon(String idPohon) {
        this.idPohon = idPohon;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNamaPohon() {
        return namaPohon;
    }

    public void setNamaPohon(String namaPohon) {
        this.namaPohon = namaPohon;
    }

    public Integer getUsia() {
        return usia;
    }

    public void setUsia(Integer usia) {
        this.usia = usia;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public Float getKapasitasSerapanKarbon() {
        return kapasitasSerapanKarbon;
    }

    public void setKapasitasSerapanKarbon(Float kapasitasSerapanKarbon) {
        this.kapasitasSerapanKarbon = kapasitasSerapanKarbon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileFotoPath() {
        return fileFotoPath;
    }

    public void setFileFotoPath(String fileFotoPath) {
        this.fileFotoPath = fileFotoPath;
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
