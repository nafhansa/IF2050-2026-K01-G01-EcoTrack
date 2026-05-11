package com.ecotrack.controller;

import com.ecotrack.entity.DataPohon;
import com.ecotrack.util.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataPohonController {

    public void cariDataPohon(String kriteria) {
        // Algo-061: result <- DataPohon.cariDataPohon(kriteria)
    }

    public void ambilDetailPohon(String idPohon) {
        // Algo-062: result <- DataPohon.cariDataPohon(idPohon)
    }

    public List<DataPohon> ambilDataPohon() {
        // Algo-063: result <- DataPohon.getDataPohon()
        return new ArrayList<>();
    }

    public String prosesInputPohon(Object data) {
        // Algo-064
        if (validasiData(data)) {
            return simpanDataPohon(data);
        } else {
            return "Data pohon tidak valid";
        }
    }

    public String simpanDataPohon(Object data) {
        // Algo-065
        if (data instanceof DataPohon) {
            DataPohon d = (DataPohon) data;
            if (d.getFileFoto() != null) {
                File fotoFile = FileManager.getFile(d.getFileFoto());
                if (fotoFile != null) {
                    String path = simpanFoto(fotoFile);
                    d.setFileFoto(path);
                }
            }
        }
        return "Berhasil";
    }

    public void ubahDataPohon(Object data) {
        // Algo-066
        if (validasiData(data)) {
            // DataPohon.ubahData(data)
        }
    }

    public void hapusDataPohon(String idPohon) {
        // Algo-067: result <- DataPohon.hapusData(idPohon)
    }

    public String simpanFoto(File file) {
        // Algo-068: result <- DataPohon.simpanFoto(file)
        return FileManager.saveFoto(file, FileManager.getFotoDir());
    }

    public boolean validasiData(Object data) {
        // Algo-069
        if (data instanceof DataPohon) {
            DataPohon d = (DataPohon) data;
            if (d.getNamaPohon() == null || d.getNamaPohon().isEmpty()) return false;
            if (d.getUsia() < 0) return false;
            if (d.getSerapanKarbon() < 0) return false;
        }
        return true;
    }

    public String tampilkanStatus(String status) {
        // Algo-070
        return status;
    }
}
