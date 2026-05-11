package com.ecotrack.controller;

import com.ecotrack.entity.DataPohon;
import com.ecotrack.entity.DataPenanaman;

import java.util.List;
import java.util.Map;

public class StatistikController {

    public Map<String, Object> hitungStatistik(DataPohon dataPohon, DataPenanaman dataPenanaman) {
        // Algo-071
        // totalPohon <- SUM(dataPenanaman.jumlahPohon)
        // totalKarbon <- SUM(dataPenanaman.estimasiKarbon)
        return Map.of(
            "totalPohon", 0,
            "totalKarbon", 0.0,
            "dataPohon", dataPohon,
            "dataPenanaman", dataPenanaman
        );
    }

    public List<DataPohon> getDataPohon() {
        // Algo-072: result <- DataPohon.getDataPohon()
        return null;
    }

    public List<DataPenanaman> getDataPenanaman() {
        // Algo-073: result <- DataPenanaman.getDataPenanaman()
        return null;
    }

    public Map<String, Object> ambilData() {
        // Algo-074
        List<DataPohon> dataPohon = getDataPohon();
        List<DataPenanaman> dataPenanaman = getDataPenanaman();
        Map<String, Object> statistik = hitungStatistik(null, null);
        teruskanKeView(statistik);
        return statistik;
    }

    public Map<String, Object> terapkanFilter(String filterPeriode) {
        // Algo-075: dataPenanaman <- DO QUERY Q-012
        List<DataPohon> dataPohon = getDataPohon();
        Map<String, Object> statistik = hitungStatistik(null, null);
        teruskanKeView(statistik);
        return statistik;
    }

    public void teruskanKeView(Object statistik) {
        // Algo-076
    }
}
