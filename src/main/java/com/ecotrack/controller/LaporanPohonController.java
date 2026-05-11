package com.ecotrack.controller;

import com.ecotrack.entity.LaporanPohon;

public class LaporanPohonController {

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
        // Algo-060: result <- LaporanPohon.simpanLaporan(dataLaporan)
        return "Berhasil";
    }
}
