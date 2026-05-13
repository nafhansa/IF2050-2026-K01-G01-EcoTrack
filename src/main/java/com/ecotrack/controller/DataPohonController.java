package com.ecotrack.controller;

import java.io.File;
import java.util.List;

import com.ecotrack.entity.DataPohon;

public class DataPohonController {
    // Controller untuk modul Data Pohon.
    // Berperan sebagai penghubung boundary (UI) dengan entity (akses DB).
    // Saat ini entity juga memuat query DB, sehingga controller lebih banyak
    // meneruskan request dan melakukan validasi ringan.
private DataPohon modelPohon = new DataPohon(); // Menginstansiasi Entity langsung

    public List<DataPohon> cariDataPohon(String kriteria) {
        // Cari data berdasarkan kriteria (id/nama) via entity.
        // Algo-061: result <- DataPohon.cariDataPohon(kriteria)
        modelPohon.cariDataPohon(kriteria);
        return modelPohon.getDataPohon();
    }

    public void ambilDetailPohon(String idPohon) {
        // Algo-062: DataPohon.cariDataPohon(idPohon)
        modelPohon.cariDataPohon(idPohon);
    }

    public List<DataPohon> ambilDataPohon() {
        // Mengambil seluruh data pohon untuk ditampilkan di tabel.
        // Algo-063: result <- DataPohon.getDataPohon()
        return modelPohon.getDataPohon();
    }

    public String prosesInputPohon(Object data) {
        // Entry point dari form: validasi -> simpan -> kembalikan pesan status.
        // Algo-064
        String result;
        if (validasiData(data)) {
            result = simpanDataPohon(data);
        } else {
            result = "Data pohon tidak valid";
        }
        return tampilkanStatus(result);    }

    public String simpanDataPohon(Object data) {
        // Menyimpan data pohon baru (termasuk handle foto jika ada).
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
        // Update data pohon (saat ini memanggil simpanData di entity).
        // Algo-066
        DataPohon d = (DataPohon) data;
            if (d.getFileFoto() != null) {
                File file = new File(d.getFileFoto());
                d.setFileFoto(simpanFoto(file));
            }
            modelPohon.simpanData(d);
    }

    public void hapusDataPohon(String idPohon) {
        // Hapus data berdasarkan id.
        // Algo-067: result <- DataPohon.hapusData(idPohon)
        modelPohon.hapusData(idPohon);    }

    public String simpanFoto(File file) {
        // Delegasi penyimpanan foto (placeholder).
        // Di implementasi sekarang, entity belum benar-benar memindahkan file.
        // Algo-068: result <- DataPohon.simpanFoto(file)
        modelPohon.simpanFoto(file);
        return file.getName();    }

    public boolean validasiData(Object data) {
        // Validasi minimal untuk menghindari data kosong/negatif.
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
