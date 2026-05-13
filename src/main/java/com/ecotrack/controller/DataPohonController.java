package com.ecotrack.controller;

import java.io.File;
import java.util.List;

import com.ecotrack.entity.DataPohon;

public class DataPohonController {
private DataPohon modelPohon = new DataPohon(); // Menginstansiasi Entity langsung

    public List<DataPohon> cariDataPohon(String kriteria) {
        // Algo-061: result <- DataPohon.cariDataPohon(kriteria)
        modelPohon.cariDataPohon(kriteria);
        return modelPohon.getDataPohon();
    }

    public void ambilDetailPohon(String idPohon) {
        // Algo-062: DataPohon.cariDataPohon(idPohon)
        modelPohon.cariDataPohon(idPohon);
    }

    public List<DataPohon> ambilDataPohon() {
        // Algo-063: result <- DataPohon.getDataPohon()
        return modelPohon.getDataPohon();
    }

    public String prosesInputPohon(Object data) {
        // Algo-064
        String result;
        if (validasiData(data)) {
            result = simpanDataPohon(data);
        } else {
            result = "Data pohon tidak valid";
        }
        return tampilkanStatus(result);    }

    public String simpanDataPohon(Object data) {
        // Algo-065
        if (data instanceof DataPohon) {
            DataPohon d = (DataPohon) data;
            if (d.getFileFoto() != null) {
                File file = new File(d.getFileFoto());
                d.setFileFoto(simpanFoto(file));
            }
            modelPohon.simpanData(d); 
            
            return "Berhasil menyimpan data pohon ke database!";
        }
        return "Gagal: Data tidak valid";
    }

    public void ubahDataPohon(Object data) {
        // Algo-066
        DataPohon d = (DataPohon) data;
            if (d.getFileFoto() != null) {
                File file = new File(d.getFileFoto());
                d.setFileFoto(simpanFoto(file));
            }
            modelPohon.simpanData(d);
    }

    public void hapusDataPohon(String idPohon) {
        // Algo-067: result <- DataPohon.hapusData(idPohon)
        modelPohon.hapusData(idPohon);    }

    public String simpanFoto(File file) {
        // Algo-068: result <- DataPohon.simpanFoto(file)
        modelPohon.simpanFoto(file);
        return file.getName();    }

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
