# ECOTRACK — PROGRESS CHECKLIST

> Dibuat berdasarkan: FULL-SPEC.md  
> Status terakhir: ✅ = Selesai | ⚠️ Sebagian | ❌ Belum Dikerjakan

---

## PHASE 1 — FONDASI

| # | Task | Status | Keterangan |
|---|---|---|---|
| 1 | Setup project Maven + dependency JDBC PostgreSQL + JavaFX | ✅ | `pom.xml` sudah lengkap dengan semua dependency |
| 2 | Buat `DBConnection.java` (singleton JDBC) | ✅ | Sudah ada + load dari `.env` file |
| 3 | Buat semua schema SQL di PostgreSQL (4 tabel) | ✅ | `db/init.sql` sudah lengkap (user, data_pohon, data_penanaman, laporan_pohon) |
| 4 | Buat semua Entity class (User, DataPohon, DataPenanaman, LaporanPohon) | ✅ | Keempat entity sudah ada dengan semua field + getter/setter |
| - | Docker PostgreSQL setup (`docker-compose.yml`, `.env`, `.gitignore`) | ✅ | Semua file konfigurasi sudah ada |

---

## PHASE 2 — DATA LAYER

### 2.1 Repository Interfaces

| # | Task | Status | Keterangan |
|---|---|---|---|
| 5 | `PohonRepository.java` (Q-005 s/d Q-010) | ⚠️ | Interface sudah ada (6 method), **tapi belum ada implementasi JDBC** |
| 6 | `PenanamanRepository.java` (Q-001 s/d Q-004) | ⚠️ | Interface sudah ada (4 method), **tapi belum ada implementasi JDBC** |
| 7 | `LaporanRepository.java` (Q-011, Q-012) | ⚠️ | Interface sudah ada (2 method), **tapi belum ada implementasi JDBC** |
| 8 | `FileManager.java` (simpan/ambil foto lokal) | ✅ | Sudah lengkap: `saveFoto`, `deleteFile`, `getFile` |

### 2.2 SQL Queries Implementation (Q-001 s/d Q-015)

| Kode | Query | Status | Keterangan |
|---|---|---|---|
| Q-001 | `SELECT * FROM data_penanaman ORDER BY ...` | ❌ | Belum diimplementasi di repository |
| Q-002 | `INSERT INTO data_penanaman ...` | ❌ | Belum diimplementasi di repository |
| Q-003 | `UPDATE data_penanaman SET ...` | ❌ | Belum diimplementasi di repository |
| Q-004 | `DELETE FROM data_penanaman WHERE ...` | ❌ | Belum diimplementasi di repository |
| Q-005 | `SELECT * FROM data_pohon ORDER BY nama_pohon ASC` | ❌ | Belum diimplementasi di repository |
| Q-006 | `INSERT INTO data_pohon ...` | ❌ | Belum diimplementasi di repository |
| Q-007 | `SELECT * FROM data_pohon WHERE id_pohon=?` | ❌ | Belum diimplementasi di repository |
| Q-008 | `UPDATE data_pohon SET ...` | ❌ | Belum diimplementasi di repository |
| Q-009 | `DELETE FROM data_pohon WHERE id_pohon=?` | ❌ | Belum diimplementasi di repository |
| Q-010 | `SELECT * FROM data_pohon WHERE nama_pohon=?` | ❌ | Belum diimplementasi di repository |
| Q-011 | `INSERT INTO laporan_pohon ...` | ❌ | Belum diimplementasi di repository |
| Q-012 | `SELECT * FROM laporan_pohon ORDER BY ...` | ❌ | Belum diimplementasi di repository |
| Q-013 | `SELECT SUM(jumlah_pohon), SUM(estimasi_karbon) FROM data_penanaman` | ❌ | Belum diimplementasi (StatistikController masih hardcoded) |
| Q-014 | `SELECT SUM(...) WHERE tanggal_penanaman BETWEEN ? AND ?` | ❌ | Belum diimplementasi (StatistikController masih hardcoded) |
| Q-015 | `SELECT lokasi, SUM(jumlah_pohon) GROUP BY lokasi` | ❌ | Belum diimplementasi (tidak ada method di repository manapun) |

---

## PHASE 3 — BUSINESS LOGIC (Controllers)

### 3.1 PohonController

| Method | Status | Keterangan |
|---|---|---|
| `getDataPohon()` | ✅ | Sudah memanggil `repository.findAll()` |
| `validasiData()` (Algo-010) | ✅ | Sudah implementasi validasi namaPohon & serapanKarbon |
| `prosesSimpanFoto()` | ✅ | Sudah memanggil `FileManager.saveFoto()` |
| `simpanDataPohon()` (Q-006) | ✅ | Sudah ada validasi + panggil repository |
| `getDetailPohon()` (Q-007) | ✅ | Sudah memanggil `repository.findById()` |
| `updateDataPohon()` (Q-008) | ✅ | Sudah ada validasi + panggil repository |
| `hapusDataPohon()` (Q-009) | ✅ | Sudah memanggil `repository.delete()` |

### 3.2 PenanamanController

| Method | Status | Keterangan |
|---|---|---|
| `getDataPenanaman()` | ✅ | Sudah memanggil `repository.findAll()` |
| `validasiInput()` | ✅ | Sudah validasi lokasi, jenisPohon, jumlahPohon, tanggal |
| `hitungEstimasi()` (Algo-003) | ❌ | **Masih return 0** — belum panggil Q-010 dan hitung estimasi |
| `simpanDataPenanaman()` (Algo-004) | ⚠️ | Validasi sudah, tapi **belum panggil hitungEstimasi() dan repository.save()** |

### 3.3 LaporanPohonController

| Method | Status | Keterangan |
|---|---|---|
| `prosesLaporan()` (Algo-015) | ⚠️ | Validasi kondisi/lokasi sudah, **tapi belum prosesSimpanFoto() dan belum simpan path foto** |
| `hitungKapasitasBaru()` | ❌ | **Masih return 0** — belum ada logika perhitungan |
| `getRiwayatLaporan()` | ✅ | Sudah memanggil `repository.getHistoryLaporan()` |

### 3.4 StatistikController

| Method | Status | Keterangan |
|---|---|---|
| `hitungStatistikTotal()` (Algo-020) | ❌ | **Masih hardcoded return 0** — belum panggil Q-013/Q-014 |
| `ambilDataVisualisasi()` (Algo-021) | ❌ | **Belum format data untuk grafik JavaFX** — masih return Map mentah |

---

## PHASE 4 — UI (JavaFX)

### 4.1 Main.java + Navigasi Sidebar

| Fitur | Status | Keterangan |
|---|---|---|
| BorderPane layout dengan sidebar + content area | ✅ | Sudah implementasi |
| Sidebar menu items (4 menu) | ✅ | Sudah ada dengan icon |
| Active/inactive menu styling | ✅ | Sudah sesuai spec (#A8E063 active, putih inactive) |
| Navigasi antar halaman (switch menu) | ✅ | Sudah load page sesuai menu |
| Logo "EcoTrack" + icon daun | ⚠️ | Ada text "🌿 EcoTrack", tapi emoji bukan icon daun SVG |

### 4.2 HalamanStatistik (UC04, UC08)

| Komponen | Status | Keterangan |
|---|---|---|
| Header (judul + subtitle) | ✅ | Sudah ada |
| Dropdown filter periode (Bulanan/Tahunan/Semua) | ✅ | ComboBox sudah ada |
| 3 Summary Cards (Total Pohon, Serapan Karbon, Rata-rata) | ❌ | **Belum dibuat** — method `updateCards()` masih kosong |
| Line Chart (Jumlah Pohon) | ❌ | **Belum dibuat** — tidak ada javafx.scene.chart.LineChart |
| Bar Chart (Serapan Karbon) | ❌ | **Belum dibuat** — tidak ada javafx.scene.chart.BarChart |
| Data feed dari StatistikController | ❌ | Controller masih return data dummy |
| Cards update otomatis saat filter berubah | ⚠️ | Event handler sudah ada, tapi updateCards() kosong |

### 4.3 HalamanDataPohon (UC03, UC07)

| Komponen | Status | Keterangan |
|---|---|---|
| Header (judul + subtitle) | ✅ | Sudah ada |
| Button "+ Tambah Pohon" | ✅ | Sudah ada dengan styling lime green |
| Badge info "Total Jenis Pohon" | ❌ | **Belum ada** |
| Tabel Daftar Pohon (4 kolom) | ✅ | TableView sudah ada dengan kolom Nama, Usia, Serapan, Aksi |
| Styling tabel (header uppercase, row hover, separator) | ❌ | **Belum di-styling** — masih default JavaFX |
| Icon lingkaran lime di kiri nama pohon | ❌ | **Belum ada** |
| Badge pill untuk usia (background hijau muda) | ❌ | **Belum ada** — masih plain text |
| Icon edit (teal) dan delete (merah) | ⚠️ | Button ada, tapi **bukan icon**, styling belum sesuai spec |
| Delete action | ✅ | Sudah memanggil `controller.hapusDataPohon()` |
| Edit action | ❌ | **Masih kosong** — belum buka modal edit |
| Modal Tambah Data Pohon | ❌ | **Belum terintegrasi** — `tampilkanModalTambah()` masih kosong |
| Empty state (ilustrasi + teks) | ❌ | **Belum ada** |

### 4.4 FormDataPohon (Modal — UC06, UC07)

| Komponen | Status | Keterangan |
|---|---|---|
| Modal card (background putih, radius 12px) | ✅ | Sudah ada |
| Header modal (judul + sub + [X] button) | ⚠️ | Judul + sub sudah, **[X] button belum ada** |
| Field Nama Pohon (TextField + placeholder) | ✅ | Sudah ada |
| Field Usia Pohon (TextField + placeholder) | ✅ | Sudah ada |
| Field Kapasitas Serapan Karbon (TextField + placeholder) | ✅ | Sudah ada |
| Footer modal (Batal + Tambah Pohon buttons) | ✅ | Sudah ada |
| Validasi input saat submit | ❌ | **Belum ada** — langsung simpan tanpa validasi |
| Upload foto | ❌ | `unggahFoto()` masih kosong |
| Edit mode (populate form dengan data existing) | ❌ | `isiDataPohon()` masih kosong |
| Overlay click to close | ❌ | **Belum ada** |
| Integrasi dengan HalamanDataPohon | ❌ | **Belum dipanggil** dari HalamanDataPohon |

### 4.5 HalamanDataPenanaman (UC01, UC05)

| Komponen | Status | Keterangan |
|---|---|---|
| Header (judul + subtitle) | ✅ | Sudah ada |
| Button "+ Catat Penanaman Baru" | ✅ | Sudah ada |
| 2 Summary Cards (Total Pohon Ditanam, Estimasi Total Serapan) | ❌ | **Belum dibuat** |
| Tabel Riwayat Penanaman (5 kolom) | ✅ | TableView sudah ada |
| Styling tabel (tanggal teal, lokasi icon pin, badge jenis pohon) | ❌ | **Belum di-styling** — masih plain text |
| Badge warna unik per jenis pohon | ❌ | **Belum ada** — map warna sudah di UIConstants tapi belum dipakai |
| Modal Catat Penanaman Baru | ❌ | **Belum dibuat** — `tampilkanFormInput()` masih kosong |
| ComboBox jenis pohon (load dari DB) | ❌ | `populateDropdownJenisPohon()` masih kosong |
| DatePicker untuk tanggal penanaman | ❌ | **Belum ada** di modal |
| Empty state | ❌ | **Belum ada** |

### 4.6 FormLaporanPohon (UC02)

| Komponen | Status | Keterangan |
|---|---|---|
| Header (judul + subtitle) | ✅ | Sudah ada |
| Split layout (form kiri 65%, riwayat kanan 35%) | ✅ | Sudah ada |
| Form Laporan Baru (panel kiri) | ✅ | Sudah ada |
| Field Lokasi (TextField) | ✅ | Sudah ada |
| Field Status Kondisi (ComboBox: Rusak/Mati/Ditebang) | ✅ | Sudah ada |
| Field Catatan Tambahan (TextArea) | ✅ | Sudah ada |
| Upload Area (dashed border, drag-drop, browse file) | ⚠️ | UI sudah ada, **tapi handler drag-drop dan file chooser belum implementasi** |
| Button Simpan Laporan (full width, lime green) | ✅ | Sudah ada |
| Panel Riwayat Laporan (kanan) | ✅ | Sudah ada dengan ListView |
| Empty state riwayat ("Belum ada laporan") | ✅ | Sudah ada placeholder |
| Badge kondisi (merah=mati, oranye=rusak, abu=ditebang) | ❌ | **Belum ada** — ListView masih plain text |
| FileChooser implementation | ❌ | `handleFileUpload()` masih kosong |
| Drag-over highlight | ❌ | `handleDragOver()` masih kosong |
| Drop handler | ❌ | `handleDrop()` masih kosong |
| Alert hasil submit | ❌ | **Belum ada** — result tidak ditampilkan ke user |

---

## UTILITIES & CONFIG

| File | Status | Keterangan |
|---|---|---|
| `UIConstants.java` | ✅ | Sudah lengkap (colors, fonts, radius, spacing, badge colors, menu items) |
| `FileManager.java` | ✅ | Sudah lengkap |
| `DBConnection.java` | ✅ | Sudah lengkap + .env support |
| `pom.xml` | ✅ | Sudah lengkap |
| `docker-compose.yml` | ✅ | Sudah sesuai spec |
| `db/init.sql` | ✅ | Sudah sesuai spec |
| `.env` | ✅ | Sudah ada |
| `.gitignore` | ✅ | Sudah ada |

---

## ALGORITMA (Pseudocode → Implementation)

| Kode | Algoritma | Status | Keterangan |
|---|---|---|---|
| Algo-001 | `User.pilihMenu()` | ⚠️ | Method ada tapi kosong — navigasi sudah di Main.java |
| Algo-003 | `PenanamanController.hitungEstimasi()` | ❌ | Masih return 0 |
| Algo-004 | `PenanamanController.simpanDataPenanaman()` | ⚠️ | Validasi sudah, tapi belum hitungEstimasi + save |
| Algo-010 | `PohonController.validasiData()` | ✅ | Sudah implementasi |
| Algo-015 | `LaporanPohonController.prosesLaporan()` | ⚠️ | Validasi sudah, tapi belum proses foto + hitung kapasitas |
| Algo-020 | `StatistikController.hitungStatistikTotal()` | ❌ | Masih hardcoded |
| Algo-021 | `StatistikController.ambilDataVisualisasi()` | ❌ | Belum format data untuk chart |

---

## USE CASES

| UC | Nama | Status | Keterangan |
|---|---|---|---|
| UC01 | Melihat Data Penanaman | ⚠️ | UI sudah, tapi data belum dari DB |
| UC02 | Melaporkan Pohon Rusak | ⚠️ | Form sudah, tapi upload foto + perhitungan belum |
| UC03 | Melihat Data Pohon | ⚠️ | UI sudah, tapi data belum dari DB |
| UC04 | Melihat Statistik Serapan Karbon | ❌ | Cards + chart belum dibuat, controller masih dummy |
| UC05 | Mengelola Data Penanaman (CRUD) | ⚠️ | Tabel sudah, tapi CRUD belum lengkap (modal + save belum) |
| UC06 | Menginput Foto dan Data Pohon | ⚠️ | Modal sudah, tapi upload foto belum |
| UC07 | Mengelola Data Pohon (CRUD) | ⚠️ | Tabel + delete sudah, tapi edit + add modal belum |
| UC08 | Memfilter Statistik | ❌ | Filter UI ada, tapi logic filter belum |

---

## TRACEABILITY MATRIX — KELAS YANG BELUM LENGKAP

| Kelas | Status | Yang Kurang |
|---|---|---|
| `Main.java` | ✅ Lengkap | - |
| `User.java` | ✅ Lengkap | - |
| `DataPohon.java` | ✅ Lengkap | - |
| `DataPenanaman.java` | ✅ Lengkap | - |
| `LaporanPohon.java` | ✅ Lengkap | - |
| `DBConnection.java` | ✅ Lengkap | - |
| `FileManager.java` | ✅ Lengkap | - |
| `UIConstants.java` | ✅ Lengkap | - |
| `PohonRepository` (interface) | ✅ | Sudah oke |
| `PenanamanRepository` (interface) | ✅ | Sudah oke |
| `LaporanRepository` (interface) | ✅ | Sudah oke |
| **PohonRepositoryImpl** | ❌ | **Belum dibuat sama sekali** |
| **PenanamanRepositoryImpl** | ❌ | **Belum dibuat sama sekali** |
| **LaporanRepositoryImpl** | ❌ | **Belum dibuat sama sekali** |
| `PohonController` | ✅ Lengkap | Tinggal tergantung repository impl |
| `PenanamanController` | ⚠️ | `hitungEstimasi()` dan `simpanDataPenanaman()` belum lengkap |
| `LaporanPohonController` | ⚠️ | `prosesLaporan()` belum handle foto, `hitungKapasitasBaru()` belum |
| `StatistikController` | ❌ | Kedua method masih hardcoded |
| `HalamanStatistik` | ❌ | Cards + charts belum dibuat |
| `HalamanDataPohon` | ⚠️ | Modal belum terintegrasi, styling tabel belum, badge belum |
| `HalamanDataPenanaman` | ⚠️ | Cards + modal belum dibuat, styling tabel belum |
| `FormLaporanPohon` | ⚠️ | File upload handlers belum, badge kondisi belum, alert belum |
| `FormDataPohon` | ⚠️ | Validasi, upload foto, edit mode, overlay close belum |

---

## RINGKASAN PROGRESS

| Phase | Total Task | Selesai | Sebagian | Belum | Progress % |
|---|---|---|---|---|---|
| Phase 1 — Fondasi | 5 | 5 | 0 | 0 | 100% |
| Phase 2 — Data Layer | 4 | 1 | 3 | 0 | 25% (interface ada, impl belum) |
| Phase 3 — Business Logic | 4 | 1 | 2 | 1 | 37.5% |
| Phase 4 — UI | 6 | 1 | 4 | 1 | 33% |
| **TOTAL** | **19** | **8** | **9** | **2** | **~42%** |

---

## PRIORITAS SELANJUTNYA (Recommended Order)

1. **Buat Repository Implementations** (JDBC PreparedStatement untuk Q-001 s/d Q-015)
   - `PohonRepositoryImpl.java`
   - `PenanamanRepositoryImpl.java`
   - `LaporanRepositoryImpl.java`

2. **Lengkapi Controller Logic**
   - `PenanamanController.hitungEstimasi()` — panggil Q-010 + hitung
   - `PenanamanController.simpanDataPenanaman()` — panggil hitungEstimasi + Q-002
   - `LaporanPohonController.hitungKapasitasBaru()` — implementasi algoritma
   - `LaporanPohonController.prosesLaporan()` — tambah prosesSimpanFoto
   - `StatistikController.hitungStatistikTotal()` — panggil Q-013/Q-014
   - `StatistikController.ambilDataVisualisasi()` — format data chart

3. **Lengkapi UI Components**
   - `HalamanStatistik` — buat 3 summary cards + LineChart + BarChart
   - `HalamanDataPohon` — integrasi modal FormDataPohon, styling tabel, badge
   - `HalamanDataPenanaman` — buat summary cards + modal input + DatePicker
   - `FormLaporanPohon` — implementasi FileChooser, drag-drop, badge kondisi
   - `FormDataPohon` — validasi, upload foto, edit mode

4. **Polishing**
   - Empty states untuk semua halaman
   - Loading states (ProgressIndicator)
   - Error states (Alert dialogs)
   - Row hover effects
   - Badge warna jenis pohon
