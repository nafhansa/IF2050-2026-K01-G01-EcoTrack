package com.ecotrack.controller;

import java.util.Map;

public class StatistikController {

    public Map<String, Object> hitungStatistikTotal(String filterPeriode) {
        // Algo-020
        // IF filterPeriode == null -> Q-013
        // ELSE -> Q-014
        return Map.of(
            "totalPohon", 0,
            "totalSerapanKarbon", 0.0,
            "filterPeriode", filterPeriode
        );
    }

    public Object ambilDataVisualisasi(String filterPeriode) {
        // Algo-021
        Map<String, Object> statistik = hitungStatistikTotal(filterPeriode);
        // Format ke data grafik JavaFX
        return statistik;
    }
}
