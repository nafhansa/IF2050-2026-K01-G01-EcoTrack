package com.ecotrack.controller;

import java.util.List;

import com.ecotrack.entity.LaporanPohon;

public class LaporanPohonController {

    private LaporanPohon modelLaporan = new LaporanPohon();

    public String prosesLaporan(LaporanPohon dataLaporan) {
        // Algo-058
        dataLaporan.setEstimasiKarbon(hitungEstimasiKarbon(dataLaporan));
        return simpanLaporan(dataLaporan);
    }

    public float hitungEstimasiKarbon(LaporanPohon dataLaporan) {
        // Algo-059
        String kondisi = dataLaporan.getKondisi();
        if (kondisi != null && kondisi.equalsIgnoreCase("ditebang")) {
            return 0;
        } else if (kondisi != null && kondisi.equalsIgnoreCase("rusak")) {
            return dataLaporan.getEstimasiKarbon() * 0.5f;
        } else {
            return dataLaporan.getEstimasiKarbon();
        }
    }

    public String simpanLaporan(LaporanPohon dataLaporan) {
        dataLaporan.simpanLaporan(dataLaporan);
        return "Berhasil menyimpan laporan pohon ke database!";
    }

    public List<LaporanPohon> ambilDataLaporan() {
        return modelLaporan.getLaporanPohon();
    }
}
