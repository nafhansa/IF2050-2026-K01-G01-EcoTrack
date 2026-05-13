package com.ecotrack.controller;

import com.ecotrack.entity.LaporanPohon;

public class LaporanPohonController {

    public String prosesLaporan(LaporanPohon dataLaporan) {
        // Entry point saat user menekan "Simpan" pada form laporan.
        // Estimasi karbon disesuaikan berdasarkan kondisi, lalu disimpan.
        // Algo-058
        dataLaporan.setEstimasiKarbon(hitungEstimasiKarbon(dataLaporan));
        return simpanLaporan(dataLaporan);
    }

    public float hitungEstimasiKarbon(LaporanPohon dataLaporan) {
        // Aturan sederhana:
        // - ditebang => 0
        // - rusak => 50% dari estimasi
        // - lainnya => tetap
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
        // Simpan laporan ke database via entity.
        dataLaporan.simpanLaporan(dataLaporan);
        return "Berhasil menyimpan laporan pohon ke database!";
    }
}
