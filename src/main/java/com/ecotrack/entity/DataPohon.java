package com.ecotrack.entity;

import java.io.File;
import java.util.Date;
import java.util.List;

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

    public void getDataPohon() {
        // Q-007: SELECT * FROM data_pohon ORDER BY nama_pohon ASC
    }

    public void simpanData(DataPohon data) {
        // Q-009: INSERT INTO data_pohon
    }

    public void ubahData(DataPohon data) {
        // Q-010: UPDATE data_pohon
    }

    public void hapusData(String idPohon) {
        // Q-011: DELETE FROM data_pohon WHERE id_pohon=?
    }

    public void simpanDataPohon(Object data) {
        // Wrapper untuk simpan data pohon
    }

    public void simpanFoto(File file) {
        // Simpan foto ke local folder
    }
}
