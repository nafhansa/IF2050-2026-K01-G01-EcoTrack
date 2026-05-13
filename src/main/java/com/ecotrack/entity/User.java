package com.ecotrack.entity;

import java.io.File;

public class User {
    // Entity user untuk autentikasi sederhana dan penyimpanan session.
    // Di aplikasi ini, password disimpan apa adanya (plain text) untuk kebutuhan demo.
    private String idUser;
    private String nama;
    private String role;
    private String password;

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public void pilihMenuPenanaman() {
        // Algo-001: HalamanDataPenanaman.ambilDataPenanaman()
    }

    public void lihatDataPohon() {
        // Algo-002: HalamanDataPohon.ambilDataPohon()
    }

    public void lihatStatistik() {
        // Algo-003: HalamanStatistik.ambilData()
    }

    public void kirimLaporan(Object dataInput, File fileFoto) {
        // Algo-004: FormLaporanPohon.prosesLaporan(dataInput, fileFoto)
    }

    public String pilihOpsiKelolaData(String opsi) {
        // Validasi opsi sederhana untuk fitur CRUD.
        // Algo-005
        if (opsi.equals("Tambah") || opsi.equals("Ubah") || opsi.equals("Hapus")) {
            return opsi;
        } else {
            return "Opsi tidak valid";
        }
    }

    public void isiDataPenanaman(DataPenanaman dataPenanaman) {
        // Algo-006: FormLaporanPenanaman.kumpulkanDataInput(dataPenanaman)
    }

    public Object unggahFoto(File fileFoto) {
        // Helper sederhana: memastikan file tidak null.
        // Algo-007
        if (fileFoto != null) {
            return fileFoto;
        } else {
            return "Foto belum dipilih";
        }
    }

    public void isiDataPohon(Object dataPohon) {
        // Algo-008: FormDataPohon.prosesInputPohon(dataPohon)
    }

    public void simpanData(Object data, String jenisData) {
        // Algo-009
        if (jenisData.equals("penanaman")) {
            // PenanamanController.simpanDataPenanaman(data)
        } else if (jenisData.equals("pohon")) {
            // DataPohonController.simpanDataPohon(data)
        }
    }

    public String konfirmasiHapus(String idData) {
        // Placeholder konfirmasi hapus.
        // Saat ini selalu menyetujui hapus; implementasi nyata seharusnya
        // mengecek keberadaan data dulu.
        // Algo-010
        // IF data dengan idData DITEMUKAN
        //   RETURN "Hapus disetujui"
        // ELSE
        //   RETURN "Data tidak ditemukan"
        return "Hapus disetujui";
    }
}
