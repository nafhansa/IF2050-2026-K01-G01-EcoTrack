package com.ecotrack.controller;

import java.util.List;

import com.ecotrack.entity.DataPenanaman;
import com.ecotrack.entity.DataPohon;

public class PenanamanController {
    // Controller modul penanaman.
    // - Mengambil daftar penanaman
    // - Menghitung estimasi karbon
    // - Menyimpan/mengubah/menghapus data penanaman
    private DataPenanaman modelPenanaman = new DataPenanaman();
    private DataPohon modelPohon = new DataPohon();

    public void cariData(String idPenanaman) {
        // Algo-051: result <- DataPenanaman.cariData(idPenanaman)
        modelPenanaman.cariData(idPenanaman);
    }

    public List<DataPenanaman> ambilDataPenanaman() {
        // Ambil data penanaman untuk ditampilkan di tabel.
        // Algo-052: result <- DataPenanaman.getDataPenanaman()
        List<DataPenanaman> result = modelPenanaman.getDataPenanaman();
        teruskanKeView(result);
        return result;
    }

    public String simpanDataPenanaman(DataPenanaman dataPenanaman) {
        // Simpan data baru. Estimasi karbon dihitung sebelum insert.
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
        // Estimasi serapan dihitung dari: jumlah pohon x serapan per pohon.
        // Serapan per pohon diambil dari master DataPohon berdasarkan nama jenis.
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
