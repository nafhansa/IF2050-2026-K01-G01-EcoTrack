package com.ecotrack.entity;

import java.util.Date;
import java.util.List;

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
    }

    public void ubahData(DataPenanaman data) {
        // Q-004: UPDATE data_penanaman
    }

    public void hapusData(String idPenanaman) {
        // Q-005: DELETE FROM data_penanaman
    }

    public void getDataPenanaman() {
        // Q-001: SELECT * FROM data_penanaman ORDER BY tanggal DESC
    }
}
