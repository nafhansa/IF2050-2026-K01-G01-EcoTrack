package com.ecotrack.entity;

public class LaporanPohon {
    private String idLaporan;
    private String kondisi;
    private String lokasi;
    private String fileFoto;
    private float estimasiKarbon;

    public String getIdLaporan() { return idLaporan; }
    public void setIdLaporan(String idLaporan) { this.idLaporan = idLaporan; }

    public String getKondisi() { return kondisi; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    public String getFileFoto() { return fileFoto; }
    public void setFileFoto(String fileFoto) { this.fileFoto = fileFoto; }

    public float getEstimasiKarbon() { return estimasiKarbon; }
    public void setEstimasiKarbon(float estimasiKarbon) { this.estimasiKarbon = estimasiKarbon; }

    public void simpanLaporan(LaporanPohon data) {
        // Q-006: INSERT INTO laporan_pohon
    }
}
