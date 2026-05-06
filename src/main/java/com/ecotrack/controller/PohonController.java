package com.ecotrack.controller;

import com.ecotrack.entity.DataPohon;
import com.ecotrack.repository.PohonRepository;
import com.ecotrack.util.FileManager;

import java.io.File;
import java.util.List;

public class PohonController {

    private final PohonRepository repository;

    public PohonController(PohonRepository repository) {
        this.repository = repository;
    }

    public List<DataPohon> getDataPohon() {
        return repository.findAll();
    }

    public boolean validasiData(DataPohon data) {
        // Algo-010
        if (data.getNamaPohon() == null || data.getNamaPohon().isEmpty()) return false;
        if (data.getKapasitasSerapanKarbon() != null && data.getKapasitasSerapanKarbon() < 0) return false;
        return true;
    }

    public String prosesSimpanFoto(File file) {
        return FileManager.saveFoto(file, FileManager.getFotoDir());
    }

    public String simpanDataPohon(DataPohon data) {
        if (!validasiData(data)) {
            return "Data tidak valid";
        }
        boolean success = repository.save(data);
        return success ? "Berhasil" : "Gagal";
    }

    public DataPohon getDetailPohon(String idPohon) {
        return repository.findById(idPohon);
    }

    public String updateDataPohon(DataPohon data) {
        if (!validasiData(data)) {
            return "Data tidak valid";
        }
        boolean success = repository.update(data);
        return success ? "Berhasil" : "Gagal";
    }

    public String hapusDataPohon(String idPohon) {
        boolean success = repository.delete(idPohon);
        return success ? "Berhasil" : "Gagal";
    }
}
