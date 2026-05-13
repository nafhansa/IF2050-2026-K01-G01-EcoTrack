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
        return new DataPenanaman().getDataPenanaman();
    }

    public String simpanDataPenanaman(DataPenanaman dataPenanaman) {
        // Algo-053
        // IF FormLaporanPenanaman.validasiData(dataPenanaman) = TRUE THEN
        if (dataPenanaman instanceof DataPenanaman) {
            DataPenanaman d = (DataPenanaman) dataPenanaman;

            d.setEstimasiKarbon(hitungEstimasiKarbon(d));
        // result <- DataPenanaman.simpanData(dataPenanaman)
            d.simpanData(d);
            
            return "Berhasil menyimpan data penanaman ke database!";
        }
        return "Gagal: Data tidak valid";
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
        float serapanPerPohon = 0.0f;

        List<DataPohon> daftarPohon = new DataPohon().getDataPohon(); 

        if (dataPenanaman.getJenisPohon() != null) {
            for (DataPohon p : daftarPohon) {
                if (p.getNamaPohon().equalsIgnoreCase(dataPenanaman.getJenisPohon())) {
                    serapanPerPohon = p.getSerapanKarbon();
                    break;
                }
            }
        }
        
        // Rumus: Jumlah * Kapasitas Serapan per Pohon
        return dataPenanaman.getJumlahPohon() * serapanPerPohon;
    }

    public void teruskanKeView(Object result) {
        // Algo-057
    }
}
