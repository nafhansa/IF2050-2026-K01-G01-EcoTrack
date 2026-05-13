package com.ecotrack.controller;

import java.util.List;

import com.ecotrack.entity.DataPenanaman;
import com.ecotrack.entity.DataPohon;

public class PenanamanController {
    private DataPenanaman modelPenanaman = new DataPenanaman();
    private DataPohon modelPohon = new DataPohon();

    public void cariData(String idPenanaman) {
        // Algo-051: result <- DataPenanaman.cariData(idPenanaman)
        modelPenanaman.cariData(idPenanaman);
    }

    public List<DataPenanaman> ambilDataPenanaman() {
        // Algo-052: result <- DataPenanaman.getDataPenanaman()
        List<DataPenanaman> result = modelPenanaman.getDataPenanaman();
        teruskanKeView(result);
        return result;
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
        dataPenanaman.setEstimasiKarbon(hitungEstimasiKarbon(dataPenanaman));
        modelPenanaman.ubahData(dataPenanaman);
        teruskanKeView("Berhasil");
        return "Berhasil";
    }

    public String hapusDataPenanaman(String idPenanaman) {
        // Algo-055: result <- DataPenanaman.hapusData(idPenanaman)
        modelPenanaman.hapusData(idPenanaman);
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

    public Object teruskanKeView(Object result) {
        // Algo-057: ViewLaporanPenanaman.tampilkanData(result)
        return result;            
    }
}
