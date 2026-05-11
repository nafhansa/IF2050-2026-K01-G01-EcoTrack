package com.ecotrack.controller;

import com.ecotrack.entity.DataPohon;
import com.ecotrack.entity.DataPenanaman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatistikController {

    public Map<String, Object> hitungStatistik(List<DataPohon> dataPohon, List<DataPenanaman> dataPenanaman) {
        // Algo-071
        // totalPohon <- SUM(dataPenanaman.jumlahPohon)
        // totalKarbon <- SUM(dataPenanaman.estimasiKarbon)
        int totalPohon = 0;
        double totalKarbon = 0.0;
        if (dataPenanaman != null) {
            for (DataPenanaman dp : dataPenanaman) {
                totalPohon += dp.getJumlahPohon();
                totalKarbon += dp.getEstimasiKarbon();
            }
        }
        return Map.of(
            "totalPohon", totalPohon,
            "totalKarbon", totalKarbon,
            "dataPohon", dataPohon != null ? dataPohon : new ArrayList<>(),
            "dataPenanaman", dataPenanaman != null ? dataPenanaman : new ArrayList<>()
        );
    }

    public List<DataPohon> getDataPohon() {
        // Algo-072: result <- DataPohon.getDataPohon()
        return new ArrayList<>();
    }

    public List<DataPenanaman> getDataPenanaman() {
        // Algo-073: result <- DataPenanaman.getDataPenanaman()
        return new ArrayList<>();
    }

    public Map<String, Object> ambilData() {
        // Algo-074
        List<DataPohon> dataPohon = getDataPohon();
        List<DataPenanaman> dataPenanaman = getDataPenanaman();
        Map<String, Object> statistik = hitungStatistik(dataPohon, dataPenanaman);
        teruskanKeView(statistik);
        return statistik;
    }

    public Map<String, Object> terapkanFilter(String filterPeriode) {
        // Algo-075: dataPenanaman <- DO QUERY Q-012
        List<DataPohon> dataPohon = getDataPohon();
        List<DataPenanaman> dataPenanaman = getDataPenanaman();
        Map<String, Object> statistik = hitungStatistik(dataPohon, dataPenanaman);
        teruskanKeView(statistik);
        return statistik;
    }

    public void teruskanKeView(Object statistik) {
        // Algo-076
    }
}
