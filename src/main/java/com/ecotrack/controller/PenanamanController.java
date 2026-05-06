package com.ecotrack.controller;

import com.ecotrack.entity.DataPohon;
import com.ecotrack.entity.DataPenanaman;
import com.ecotrack.repository.PenanamanRepository;

import java.io.File;
import java.util.List;

public class PenanamanController {

    private final PenanamanRepository repository;

    public PenanamanController(PenanamanRepository repository) {
        this.repository = repository;
    }

    public List<DataPenanaman> getDataPenanaman() {
        return repository.findAll();
    }

    public boolean validasiInput(DataPenanaman data) {
        if (data.getLokasi() == null || data.getLokasi().isEmpty()) return false;
        if (data.getJenisPohon() == null || data.getJenisPohon().isEmpty()) return false;
        if (data.getJumlahPohon() <= 0) return false;
        if (data.getTanggalPenanaman() == null) return false;
        return true;
    }

    public float hitungEstimasi(String jenisPohon, int jumlahPohon) {
        // Algo-003: estimasiKarbon = jumlahPohon * dataPohon.serapanKarbon
        return 0;
    }

    public String simpanDataPenanaman(DataPenanaman data) {
        // Algo-004: validasiInput -> hitungEstimasi -> repository.save
        if (!validasiInput(data)) {
            return "Data tidak valid";
        }
        return "Berhasil";
    }
}
