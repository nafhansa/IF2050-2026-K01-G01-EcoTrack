# ECOTRACK — PROGRESS CHECKLIST

> Dibuat berdasarkan: FULL-SPEC.md  
> Status: ✅ = Selesai | ⚠️ Sebagian | ❌ Belum Dikerjakan  
> Current branch: `main` | Progress: ~44%

---

## 📋 BRANCH STRATEGY

| Branch | Status | Scope | Merge Target |
|---|---|---|---|
| `main` | ✅ STABLE | Phase 1 (Fondasi) | — |
| `phase-2/repository-implementations` | 🔄 NEXT | 3 RepositoryImpl + update Main.java | → main |
| `phase-3/controller-logic` | ⏳ PENDING | Lengkapin 4 controller | → main |
| `phase-4/ui-components` | ⏳ PENDING | 5 halaman UI lengkap | → main |

**Workflow:**
```bash
# 1. Buat branch dari main
git checkout main && git pull
git checkout -b phase-2/repository-implementations

# 2. Kerja + commit sering
git add . && git commit -m "feat: PohonRepositoryImpl Q-005 to Q-010"

# 3. Test compile & run
mvn clean compile && mvn javafx:run

# 4. Merge ke main (HANYA kalau stabil)
git checkout main
git merge phase-2/repository-implementations
git push origin main
```

---

## ✅ SUDAH DIKERJAIN (Merged to `main`)

### Phase 1 — Fondasi (100% ✅)

| # | Task | Branch | Keterangan |
|---|---|---|---|
| 1 | Setup project Maven + dependency JDBC PostgreSQL + JavaFX | `main` | `pom.xml` sudah lengkap dengan semua dependency |
| 2 | Buat `DBConnection.java` (singleton JDBC) | `main` | Sudah ada + load dari `.env` file |
| 3 | Buat semua schema SQL di PostgreSQL (4 tabel) | `main` | `db/init.sql` sudah lengkap (user, data_pohon, data_penanaman, laporan_pohon) |
| 4 | Buat semua Entity class (User, DataPohon, DataPenanaman, LaporanPohon) | `main` | Keempat entity sudah ada dengan semua field + getter/setter |
| 5 | Docker PostgreSQL setup (`docker-compose.yml`, `.env`, `.gitignore`) | `main` | Semua file konfigurasi sudah ada |

### Phase 2 — Data Layer (Interfaces Only)

| # | Task | Branch | Keterangan |
|---|---|---|---|
| 6 | `PohonRepository.java` (interface, Q-005 s/d Q-010) | `main` | 6 method defined |
| 7 | `PenanamanRepository.java` (interface, Q-001 s/d Q-004) | `main` | 4 method defined |
| 8 | `LaporanRepository.java` (interface, Q-011, Q-012) | `main` | 2 method defined |
| 9 | `FileManager.java` (simpan/ambil foto lokal) | `main` | Sudah lengkap: `saveFoto`, `deleteFile`, `getFile` |

### Phase 3 — Business Logic (Partial)

| Class | Method | Branch | Status |
|---|---|---|---|
| `PohonController` | `getDataPohon()` | `main` | ✅ |
| `PohonController` | `validasiData()` (Algo-010) | `main` | ✅ |
| `PohonController` | `prosesSimpanFoto()` | `main` | ✅ |
| `PohonController` | `simpanDataPohon()` (Q-006) | `main` | ✅ |
| `PohonController` | `getDetailPohon()` (Q-007) | `main` | ✅ |
| `PohonController` | `updateDataPohon()` (Q-008) | `main` | ✅ |
| `PohonController` | `hapusDataPohon()` (Q-009) | `main` | ✅ |
| `PenanamanController` | `getDataPenanaman()` | `main` | ✅ |
| `PenanamanController` | `validasiInput()` | `main` | ✅ |
| `LaporanPohonController` | `getRiwayatLaporan()` | `main` | ✅ |

### Phase 4 — UI (Partial)

| Komponen | Status | Branch | Keterangan |
|---|---|---|---|
| Main.java + Sidebar navigasi | ✅ | `main` | BorderPane, 4 menu, active/inactive styling |
| HalamanStatistik (header + dropdown) | ⚠️ | `main` | Cards + charts belum |
| HalamanDataPohon (header + tabel) | ⚠️ | `main` | Modal + styling belum |
| HalamanDataPenanaman (header + tabel) | ⚠️ | `main` | Cards + modal belum |
| FormLaporanPohon (form + riwayat) | ⚠️ | `main` | Upload handlers belum |
| FormDataPohon (modal structure) | ⚠️ | `main` | Belum terintegrasi |

### Utilities & Config

| File | Branch | Status |
|---|---|---|
| `UIConstants.java` | `main` | ✅ Lengkap (colors, fonts, radius, spacing, badge colors, menu items) |
| `DBConnection.java` | `main` | ✅ Lengkap + .env support |
| `FileManager.java` | `main` | ✅ Lengkap |
| `pom.xml` | `main` | ✅ Lengkap |
| `docker-compose.yml` | `main` | ✅ Sesuai spec |
| `db/init.sql` | `main` | ✅ Sesuai spec |
| `.env` | `main` | ✅ Ada |
| `.gitignore` | `main` | ✅ Ada |

---

## 🚧 YANG HARUS DIKERJAIN

### Priority 1: Repository Implementations

**Branch: `phase-2/repository-implementations`** (buat dari `main`)

| # | Task | File | Queries | Est. Effort |
|---|---|---|---|---|
| 1 | PohonRepositoryImpl | `repository/PohonRepositoryImpl.java` | Q-005 s/d Q-010 | Medium |
| 2 | PenanamanRepositoryImpl | `repository/PenanamanRepositoryImpl.java` | Q-001 s/d Q-004 | Medium |
| 3 | LaporanRepositoryImpl | `repository/LaporanRepositoryImpl.java` | Q-011, Q-012 | Small |
| 4 | Update Main.java | `Main.java` | Ganti `null` → repository instances | Small |

**SQL Queries Detail:**

| Kode | Query | Target File |
|---|---|---|
| Q-001 | `SELECT * FROM data_penanaman ORDER BY tanggal_penanaman DESC` | PenanamanRepositoryImpl |
| Q-002 | `INSERT INTO data_penanaman (id_penanaman, lokasi, jenis_pohon, jumlah_pohon, tanggal_penanaman, estimasi_karbon) VALUES (?, ?, ?, ?, ?, ?)` | PenanamanRepositoryImpl |
| Q-003 | `UPDATE data_penanaman SET lokasi=?, jenis_pohon=?, jumlah_pohon=?, tanggal_penanaman=?, estimasi_karbon=? WHERE id_penanaman=?` | PenanamanRepositoryImpl |
| Q-004 | `DELETE FROM data_penanaman WHERE id_penanaman=?` | PenanamanRepositoryImpl |
| Q-005 | `SELECT * FROM data_pohon ORDER BY nama_pohon ASC` | PohonRepositoryImpl |
| Q-006 | `INSERT INTO data_pohon (id_pohon, nama_pohon, usia, lokasi, kapasitas_serapan_karbon, status, file_foto_path, id_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)` | PohonRepositoryImpl |
| Q-007 | `SELECT * FROM data_pohon WHERE id_pohon=?` | PohonRepositoryImpl |
| Q-008 | `UPDATE data_pohon SET nama_pohon=?, usia=?, lokasi=?, kapasitas_serapan_karbon=?, status=?, file_foto_path=? WHERE id_pohon=?` | PohonRepositoryImpl |
| Q-009 | `DELETE FROM data_pohon WHERE id_pohon=?` | PohonRepositoryImpl |
| Q-010 | `SELECT * FROM data_pohon WHERE nama_pohon=?` | PohonRepositoryImpl |
| Q-011 | `INSERT INTO laporan_pohon (id_laporan, id_pohon, kondisi, lokasi, file_foto_path, estimasi_karbon, tanggal_laporan, id_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)` | LaporanRepositoryImpl |
| Q-012 | `SELECT * FROM laporan_pohon ORDER BY tanggal_laporan DESC` | LaporanRepositoryImpl |

**Acceptance Criteria:**
- [ ] Semua query pakai `PreparedStatement`
- [ ] `DBConnection.getConnection()` dipanggil dengan benar
- [ ] No NullPointerException saat controller dipanggil
- [ ] `mvn clean compile` success
- [ ] `mvn javafx:run` bisa jalan (walaupun data masih kosong)

---

### Priority 2: Controller Logic

**Branch: `phase-3/controller-logic`** (buat SETELAH phase-2 merge ke main)

| # | Task | File | Algorithm | Dependencies |
|---|---|---|---|---|
| 1 | Hitung estimasi karbon | `PenanamanController.java` | Algo-003 | Q-010 (PohonRepositoryImpl) |
| 2 | Simpan data penanaman | `PenanamanController.java` | Algo-004 | Q-002, hitungEstimasi() |
| 3 | Hitung kapasitas baru | `LaporanPohonController.java` | — | Q-011 |
| 4 | Proses laporan + foto | `LaporanPohonController.java` | Algo-015 | FileManager, Q-011 |
| 5 | Hitung statistik total | `StatistikController.java` | Algo-020 | Q-013/Q-014 (perlu method baru) |
| 6 | Format data visualisasi | `StatistikController.java` | Algo-021 | hitungStatistikTotal() |

**Note:** Q-013, Q-014, Q-015 belum ada di repository manapun → perlu tambah method di `PenanamanRepository`:
- `getTotalStatistik()` → Q-013
- `getTotalStatistikByPeriod(LocalDate, LocalDate)` → Q-014
- `getStatistikByLokasi()` → Q-015

**SQL Queries untuk Statistik:**

| Kode | Query | Keterangan |
|---|---|---|
| Q-013 | `SELECT SUM(jumlah_pohon) AS total_pohon, SUM(estimasi_karbon) AS total_karbon FROM data_penanaman` | Statistik total tanpa filter |
| Q-014 | `SELECT SUM(jumlah_pohon) AS total_pohon, SUM(estimasi_karbon) AS total_karbon FROM data_penanaman WHERE tanggal_penanaman BETWEEN ? AND ?` | Statistik dengan filter periode |
| Q-015 | `SELECT lokasi, SUM(jumlah_pohon) AS total_pohon FROM data_penanaman GROUP BY lokasi` | Statistik per lokasi |

**Acceptance Criteria:**
- [ ] `hitungEstimasi()` return `jumlahPohon * serapanKarbon`
- [ ] `simpanDataPenanaman()` panggil `repository.save()` + return "Berhasil"/"Gagal"
- [ ] `hitungStatistikTotal()` return Map dengan `totalPohon`, `totalSerapanKarbon`
- [ ] All controllers testable tanpa UI

---

### Priority 3: UI Components

**Branch: `phase-4/ui-components`** (buat SETELAH phase-3 merge ke main)

Opsional: Bisa dipecah jadi sub-branches per halaman kalau mau lebih aman:
- `phase-4/halaman-statistik`
- `phase-4/halaman-data-pohon`
- `phase-4/halaman-data-penanaman`
- `phase-4/form-laporan-pohon`

| # | Task | File | Components | Est. Effort |
|---|---|---|---|---|
| 1 | Summary cards + charts | `HalamanStatistik.java` | 3 cards, LineChart, BarChart | Large |
| 2 | Modal integration | `HalamanDataPohon.java` + `FormDataPohon.java` | Modal, validasi, upload foto | Large |
| 3 | Tabel styling + badges | `HalamanDataPohon.java` | Row hover, badge usia, icon aksi | Medium |
| 4 | Summary cards + modal | `HalamanDataPenanaman.java` | 2 cards, modal input, DatePicker | Large |
| 5 | Tabel styling + badges | `HalamanDataPenanaman.java` | Badge jenis pohon, tanggal teal | Medium |
| 6 | File upload handlers | `FormLaporanPohon.java` | FileChooser, drag-drop, preview | Medium |
| 7 | Badge kondisi | `FormLaporanPohon.java` | Merah/oranye/abu pills | Small |
| 8 | Empty states | Semua halaman | Ilustrasi + teks | Small |

**Detail per Halaman:**

#### HalamanStatistik (UC04, UC08)

| Komponen | Status | Keterangan |
|---|---|---|
| Header (judul + subtitle) | ✅ | Sudah ada |
| Dropdown filter periode (Bulanan/Tahunan/Semua) | ✅ | ComboBox sudah ada |
| 3 Summary Cards (Total Pohon, Serapan Karbon, Rata-rata) | ❌ | `updateCards()` masih kosong |
| Line Chart (Jumlah Pohon) | ❌ | Tidak ada `javafx.scene.chart.LineChart` |
| Bar Chart (Serapan Karbon) | ❌ | Tidak ada `javafx.scene.chart.BarChart` |
| Data feed dari StatistikController | ❌ | Controller masih return data dummy |
| Cards update otomatis saat filter berubah | ⚠️ | Event handler sudah ada, tapi `updateCards()` kosong |

#### HalamanDataPohon (UC03, UC07)

| Komponen | Status | Keterangan |
|---|---|---|
| Header (judul + subtitle) | ✅ | Sudah ada |
| Button "+ Tambah Pohon" | ✅ | Sudah ada dengan styling lime green |
| Badge info "Total Jenis Pohon" | ❌ | Belum ada |
| Tabel Daftar Pohon (4 kolom) | ✅ | TableView sudah ada |
| Styling tabel (header uppercase, row hover, separator) | ❌ | Masih default JavaFX |
| Icon lingkaran lime di kiri nama pohon | ❌ | Belum ada |
| Badge pill untuk usia (background hijau muda) | ❌ | Masih plain text |
| Icon edit (teal) dan delete (merah) | ⚠️ | Button ada, tapi bukan icon, styling belum sesuai spec |
| Delete action | ✅ | Sudah memanggil `controller.hapusDataPohon()` |
| Edit action | ❌ | Masih kosong — belum buka modal edit |
| Modal Tambah Data Pohon | ❌ | `tampilkanModalTambah()` masih kosong |
| Empty state (ilustrasi + teks) | ❌ | Belum ada |

#### FormDataPohon (Modal — UC06, UC07)

| Komponen | Status | Keterangan |
|---|---|---|
| Modal card (background putih, radius 12px) | ✅ | Sudah ada |
| Header modal (judul + sub + [X] button) | ⚠️ | Judul + sub sudah, [X] button belum ada |
| Field Nama Pohon (TextField + placeholder) | ✅ | Sudah ada |
| Field Usia Pohon (TextField + placeholder) | ✅ | Sudah ada |
| Field Kapasitas Serapan Karbon (TextField + placeholder) | ✅ | Sudah ada |
| Footer modal (Batal + Tambah Pohon buttons) | ✅ | Sudah ada |
| Validasi input saat submit | ❌ | Langsung simpan tanpa validasi |
| Upload foto | ❌ | `unggahFoto()` masih kosong |
| Edit mode (populate form dengan data existing) | ❌ | `isiDataPohon()` masih kosong |
| Overlay click to close | ❌ | Belum ada |
| Integrasi dengan HalamanDataPohon | ❌ | Belum dipanggil dari HalamanDataPohon |

#### HalamanDataPenanaman (UC01, UC05)

| Komponen | Status | Keterangan |
|---|---|---|
| Header (judul + subtitle) | ✅ | Sudah ada |
| Button "+ Catat Penanaman Baru" | ✅ | Sudah ada |
| 2 Summary Cards (Total Pohon Ditanam, Estimasi Total Serapan) | ❌ | Belum dibuat |
| Tabel Riwayat Penanaman (5 kolom) | ✅ | TableView sudah ada |
| Styling tabel (tanggal teal, lokasi icon pin, badge jenis pohon) | ❌ | Masih plain text |
| Badge warna unik per jenis pohon | ❌ | Map warna sudah di UIConstants tapi belum dipakai |
| Modal Catat Penanaman Baru | ❌ | `tampilkanFormInput()` masih kosong |
| ComboBox jenis pohon (load dari DB) | ❌ | `populateDropdownJenisPohon()` masih kosong |
| DatePicker untuk tanggal penanaman | ❌ | Belum ada di modal |
| Empty state | ❌ | Belum ada |

#### FormLaporanPohon (UC02)

| Komponen | Status | Keterangan |
|---|---|---|
| Header (judul + subtitle) | ✅ | Sudah ada |
| Split layout (form kiri 65%, riwayat kanan 35%) | ✅ | Sudah ada |
| Form Laporan Baru (panel kiri) | ✅ | Sudah ada |
| Field Lokasi (TextField) | ✅ | Sudah ada |
| Field Status Kondisi (ComboBox: Rusak/Mati/Ditebang) | ✅ | Sudah ada |
| Field Catatan Tambahan (TextArea) | ✅ | Sudah ada |
| Upload Area (dashed border, drag-drop, browse file) | ⚠️ | UI sudah ada, tapi handler drag-drop dan file chooser belum |
| Button Simpan Laporan (full width, lime green) | ✅ | Sudah ada |
| Panel Riwayat Laporan (kanan) | ✅ | Sudah ada dengan ListView |
| Empty state riwayat ("Belum ada laporan") | ✅ | Sudah ada placeholder |
| Badge kondisi (merah=mati, oranye=rusak, abu=ditebang) | ❌ | ListView masih plain text |
| FileChooser implementation | ❌ | `handleFileUpload()` masih kosong |
| Drag-over highlight | ❌ | `handleDragOver()` masih kosong |
| Drop handler | ❌ | `handleDrop()` masih kosong |
| Alert hasil submit | ❌ | Result tidak ditampilkan ke user |

**Acceptance Criteria:**
- [ ] Semua halaman sesuai spec FULL-SPEC.md (warna, layout, spacing)
- [ ] Modal bisa buka/tutup dengan benar
- [ ] File upload berfungsi (simpan ke folder, path ke DB)
- [ ] Charts render dengan data dari controller
- [ ] No runtime errors

---

### Priority 4: Polishing

**Branch: `phase-4/polishing`** (opsional, bisa gabung dengan phase-4)

| # | Task | Est. Effort |
|---|---|---|
| 1 | Loading states (ProgressIndicator) | Small |
| 2 | Error states (Alert dialogs) | Small |
| 3 | Row hover effects | Small |
| 4 | Badge warna jenis pohon (map UIConstants) | Small |
| 5 | Logo daun SVG (ganti emoji) | Small |

---

## 📊 ALGORITMA (Pseudocode → Implementation)

| Kode | Algoritma | Status | Target Branch | Keterangan |
|---|---|---|---|---|
| Algo-001 | `User.pilihMenu()` | ⚠️ | `main` | Method ada tapi kosong — navigasi sudah di Main.java |
| Algo-003 | `PenanamanController.hitungEstimasi()` | ❌ | `phase-3/controller-logic` | Masih return 0 |
| Algo-004 | `PenanamanController.simpanDataPenanaman()` | ⚠️ | `phase-3/controller-logic` | Validasi sudah, tapi belum hitungEstimasi + save |
| Algo-010 | `PohonController.validasiData()` | ✅ | `main` | Sudah implementasi |
| Algo-015 | `LaporanPohonController.prosesLaporan()` | ⚠️ | `phase-3/controller-logic` | Validasi sudah, tapi belum proses foto + hitung kapasitas |
| Algo-020 | `StatistikController.hitungStatistikTotal()` | ❌ | `phase-3/controller-logic` | Masih hardcoded |
| Algo-021 | `StatistikController.ambilDataVisualisasi()` | ❌ | `phase-3/controller-logic` | Belum format data untuk chart |

---

## 📊 USE CASES

| UC | Nama | Status | Target Branch | Keterangan |
|---|---|---|---|---|
| UC01 | Melihat Data Penanaman | ⚠️ | `phase-2` + `phase-3` | UI sudah, tapi data belum dari DB |
| UC02 | Melaporkan Pohon Rusak | ⚠️ | `phase-2` + `phase-3` + `phase-4` | Form sudah, tapi upload foto + perhitungan belum |
| UC03 | Melihat Data Pohon | ⚠️ | `phase-2` + `phase-3` | UI sudah, tapi data belum dari DB |
| UC04 | Melihat Statistik Serapan Karbon | ❌ | `phase-3` + `phase-4` | Cards + chart belum dibuat, controller masih dummy |
| UC05 | Mengelola Data Penanaman (CRUD) | ⚠️ | `phase-2` + `phase-3` + `phase-4` | Tabel sudah, tapi CRUD belum lengkap |
| UC06 | Menginput Foto dan Data Pohon | ⚠️ | `phase-2` + `phase-4` | Modal sudah, tapi upload foto belum |
| UC07 | Mengelola Data Pohon (CRUD) | ⚠️ | `phase-2` + `phase-3` + `phase-4` | Tabel + delete sudah, tapi edit + add modal belum |
| UC08 | Memfilter Statistik | ❌ | `phase-3` + `phase-4` | Filter UI ada, tapi logic filter belum |

---

## 📊 TRACEABILITY MATRIX — KELAS YANG BELUM LENGKAP

| Kelas | Status | Target Branch | Yang Kurang |
|---|---|---|---|
| `Main.java` | ✅ Lengkap | `main` | — |
| `User.java` | ✅ Lengkap | `main` | — |
| `DataPohon.java` | ✅ Lengkap | `main` | — |
| `DataPenanaman.java` | ✅ Lengkap | `main` | — |
| `LaporanPohon.java` | ✅ Lengkap | `main` | — |
| `DBConnection.java` | ✅ Lengkap | `main` | — |
| `FileManager.java` | ✅ Lengkap | `main` | — |
| `UIConstants.java` | ✅ Lengkap | `main` | — |
| `PohonRepository` (interface) | ✅ | `main` | Sudah oke |
| `PenanamanRepository` (interface) | ✅ | `main` | Sudah oke |
| `LaporanRepository` (interface) | ✅ | `main` | Sudah oke |
| **PohonRepositoryImpl** | ❌ | `phase-2/repository-implementations` | **Belum dibuat sama sekali** |
| **PenanamanRepositoryImpl** | ❌ | `phase-2/repository-implementations` | **Belum dibuat sama sekali** |
| **LaporanRepositoryImpl** | ❌ | `phase-2/repository-implementations` | **Belum dibuat sama sekali** |
| `PohonController` | ✅ Lengkap | `main` | Tinggal tergantung repository impl |
| `PenanamanController` | ⚠️ | `phase-3/controller-logic` | `hitungEstimasi()` dan `simpanDataPenanaman()` belum lengkap |
| `LaporanPohonController` | ⚠️ | `phase-3/controller-logic` | `prosesLaporan()` belum handle foto, `hitungKapasitasBaru()` belum |
| `StatistikController` | ❌ | `phase-3/controller-logic` | Kedua method masih hardcoded |
| `HalamanStatistik` | ❌ | `phase-4/ui-components` | Cards + charts belum dibuat |
| `HalamanDataPohon` | ⚠️ | `phase-4/ui-components` | Modal belum terintegrasi, styling tabel belum, badge belum |
| `HalamanDataPenanaman` | ⚠️ | `phase-4/ui-components` | Cards + modal belum dibuat, styling tabel belum |
| `FormLaporanPohon` | ⚠️ | `phase-4/ui-components` | File upload handlers belum, badge kondisi belum, alert belum |
| `FormDataPohon` | ⚠️ | `phase-4/ui-components` | Validasi, upload foto, edit mode, overlay close belum |

---

## 📊 RINGKASAN PROGRESS

| Phase | Status | Branch | Tasks Done | Total Tasks | Progress |
|---|---|---|---|---|---|
| Phase 1 — Fondasi | ✅ DONE | `main` | 5/5 | 5 | 100% |
| Phase 2 — Data Layer | 🔄 NEXT | `phase-2/repository-implementations` | 4/7 | 7 | 57% (interface only) |
| Phase 3 — Business Logic | ⏳ PENDING | `phase-3/controller-logic` | 3/9 | 9 | 33% |
| Phase 4 — UI | ⏳ PENDING | `phase-4/ui-components` | 6/20 | 20 | 30% |
| **TOTAL** | | | **18/41** | **41** | **~44%** |

---

## 🌿 BRANCH WORKFLOW (Step-by-Step)

### Step 1: Mulai Phase 2 (Repository Implementations)

```bash
# Pastikan main terbaru
git checkout main
git pull origin main

# Buat branch baru
git checkout -b phase-2/repository-implementations

# Kerja + commit per file
git add src/main/java/com/ecotrack/repository/PohonRepositoryImpl.java
git commit -m "feat: implement PohonRepositoryImpl (Q-005 to Q-010)"

git add src/main/java/com/ecotrack/repository/PenanamanRepositoryImpl.java
git commit -m "feat: implement PenanamanRepositoryImpl (Q-001 to Q-004)"

git add src/main/java/com/ecotrack/repository/LaporanRepositoryImpl.java
git commit -m "feat: implement LaporanRepositoryImpl (Q-011, Q-012)"

git add src/main/java/com/ecotrack/Main.java
git commit -m "fix: wire repository implementations to controllers"

# Test compile
mvn clean compile

# Test run (kalau DB jalan)
mvn javafx:run

# Kalau semua oke, merge ke main
git checkout main
git merge phase-2/repository-implementations
git push origin main

# Delete branch
git branch -d phase-2/repository-implementations
```

### Step 2: Mulai Phase 3 (Controller Logic)

```bash
git checkout main
git checkout -b phase-3/controller-logic

# ... kerja ...

# Merge ke main
git checkout main
git merge phase-3/controller-logic
git push origin main
git branch -d phase-3/controller-logic
```

### Step 3: Mulai Phase 4 (UI Components)

```bash
git checkout main
git checkout -b phase-4/ui-components

# ... kerja ...

# Merge ke main
git checkout main
git merge phase-4/ui-components
git push origin main
git branch -d phase-4/ui-components
```

### Kalau Ada Bug Setelah Merge?

```bash
# Buat hotfix branch dari main
git checkout main
git checkout -b hotfix/fix-repository-npe

# Fix bug + commit
git commit -m "fix: handle null case in PohonRepositoryImpl.findById()"

# Merge balik
git checkout main
git merge hotfix/fix-repository-npe
git push origin main

# Delete branch
git branch -d hotfix/fix-repository-npe
```

---

## 📝 NOTES

- ✅ **Commit sering** — setiap file/method selesai, langsung commit
- ✅ **Test sebelum merge** — `mvn clean compile` wajib pass
- ✅ **Jangan force push** — main harus selalu aman
- ✅ **Delete branch setelah merge** — biar rapi
- ⚠️ **Jangan kerja di main langsung** — selalu buat branch dulu
- ⚠️ **Jangan merge kalau belum test** — bisa breaking
- ⚠️ **Backup .env** — jangan sampai ke-commit
