package com.ecotrack.controller;

import com.ecotrack.entity.LaporanPohon;
import com.ecotrack.repository.LaporanRepository;
import com.ecotrack.util.FileManager;

import java.io.File;
import java.util.List;

public class LaporanPohonController {

    private final LaporanRepository repository;
    private final PohonController pohonController;

    public LaporanPohonController(LaporanRepository repository, PohonController pohonController) {
        this.repository = repository;
        this.pohonController = pohonController;
    }

    public String prosesLaporan(LaporanPohon data) {
        // Algo-015
        if (data.getKondisi() == null || data.getLokasi() == null) {
            return "Data belum lengkap";
        }
        data.setEstimasiKarbon(hitungKapasitasBaru(data));
        boolean success = repository.save(data);
        return success ? "Berhasil" : "Gagal";
    }

    public float hitungKapasitasBaru(LaporanPohon data) {
        // Hitung ulang estimasi karbon setelah pohon rusak/mati
        return 0;
    }

    public List<LaporanPohon> getRiwayatLaporan() {
        return repository.getHistoryLaporan();
    }
}
