package com.ecotrack.repository;

import com.ecotrack.entity.LaporanPohon;

import java.util.List;

public interface LaporanRepository {
    boolean save(LaporanPohon l);           // Q-011
    List<LaporanPohon> getHistoryLaporan(); // Q-012
}
