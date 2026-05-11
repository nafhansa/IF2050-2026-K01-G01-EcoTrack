package com.ecotrack.controller;

import com.ecotrack.entity.DataPohon;
import com.ecotrack.entity.DataPenanaman;

import java.util.ArrayList;
import java.util.List;

public class PenanamanController {

    public void cariData(String idPenanaman) {
        // Algo-051: result <- DataPenanaman.cariData(idPenanaman)
    }

    public List<DataPenanaman> ambilDataPenanaman() {
        // Algo-052: result <- DataPenanaman.getDataPenanaman()
        return new ArrayList<>();
    }

    public String simpanDataPenanaman(DataPenanaman dataPenanaman) {
        // Algo-053
        // IF FormLaporanPenanaman.validasiData(dataPenanaman) = TRUE THEN
        dataPenanaman.setEstimasiKarbon(hitungEstimasiKarbon(dataPenanaman));
        // result <- DataPenanaman.simpanData(dataPenanaman)
        teruskanKeView("Berhasil");
        return "Berhasil";
    }

    public String ubahDataPenanaman(DataPenanaman dataPenanaman) {
        // Algo-054
        // IF FormLaporanPenanaman.validasiData(dataPenanaman) = TRUE THEN
        dataPenanaman.setEstimasiKarbon(hitungEstimasiKarbon(dataPenanaman));
        // result <- DataPenanaman.ubahData(dataPenanaman)
        teruskanKeView("Berhasil");
        return "Berhasil";
    }

    public String hapusDataPenanaman(String idPenanaman) {
        // Algo-055: result <- DataPenanaman.hapusData(idPenanaman)
        teruskanKeView("Berhasil");
        return "Berhasil";
    }

    public float hitungEstimasiKarbon(DataPenanaman dataPenanaman) {
        // Algo-056: dataPohon <- DataPohon.cariDataPohon(dataPenanaman.jenisPohon)
        // estimasiKarbon <- dataPenanaman.jumlahPohon * dataPohon.serapanKarbon
        return 0;
    }

    public void teruskanKeView(Object result) {
        // Algo-057
    }
}
