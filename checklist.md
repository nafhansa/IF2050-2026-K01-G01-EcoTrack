# IMPLEMENTATION CHECKLIST — EcoTrack FULL-SPEC Alignment

> Status tracking untuk penyelarasan source code dengan DPPLOO-01 (51 halaman)
> **Arsitektur: BCE (Boundary-Controller-Entity) — TANPA Repository layer**
> **Terakhir diupdate: Phase 1-6 DONE**

---

## PHASE 1 — FONDASI (Database & Config)

- [x] `db/init.sql` — Schema sesuai DPPLOO-01 (`serapan_karbon`, `file_foto`, `tanggal`, no `status`, VARCHAR tanpa length)
- [x] `DBConnection.java` — Hardcoded values, no `.env` loading, no `closeConnection()`
- [x] `.gitignore` — Sesuai spec
- [x] `FULL-SPEC.md` — Updated: hapus Repository layer, tambah semua 76 algoritma, sesuaikan kelas

## PHASE 2 — ENTITY CLASSES

- [x] `DataPohon.java` — Fields: `idPohon`, `namaPohon`, `usia`, `lokasi`, `serapanKarbon`, `fileFoto`. 7 methods: `cariDataPohon`, `getDataPohon`, `simpanData`, `ubahData`, `hapusData`, `simpanDataPohon`, `simpanFoto`
- [x] `DataPenanaman.java` — Fields: `idPenanaman`, `lokasi`, `jenisPohon`, `jumlahPohon`, `tanggal` (Date), `estimasiKarbon`. 5 methods: `cariData`, `simpanData`, `ubahData`, `hapusData`, `getDataPenanaman`
- [x] `LaporanPohon.java` — Fields: `idLaporan`, `kondisi`, `lokasi`, `fileFoto`, `estimasiKarbon`. 1 method: `simpanLaporan`
- [x] `User.java` — 3 fields + 10 methods: `pilihMenuPenanaman`, `lihatDataPohon`, `lihatStatistik`, `kirimLaporan`, `pilihOpsiKelolaData`, `isiDataPenanaman`, `unggahFoto`, `isiDataPohon`, `simpanData`, `konfirmasiHapus`

## PHASE 3 — REPOSITORY LAYER

- [x] **DIHAPUS** — DPPLOO-01 tidak memiliki Repository layer. Controller langsung panggil Entity.
- [x] `repository/` directory deleted

## PHASE 4 — CONTROLLER CLASSES

- [x] `DataPohonController.java` — **Renamed dari `PohonController.java`**. 10 methods sesuai C-13: `cariDataPohon`, `ambilDetailPohon`, `ambilDataPohon`, `prosesInputPohon`, `simpanDataPohon`, `ubahDataPohon`, `hapusDataPohon`, `simpanFoto`, `validasiData`, `tampilkanStatus`. Algo-061 s/d Algo-070.
- [x] `PenanamanController.java` — 7 methods sesuai C-11: `cariData`, `ambilDataPenanaman`, `simpanDataPenanaman`, `ubahDataPenanaman`, `hapusDataPenanaman`, `hitungEstimasiKarbon`, `teruskanKeView`. Algo-051 s/d Algo-057.
- [x] `LaporanPohonController.java` — 3 methods sesuai C-12: `prosesLaporan`, `hitungEstimasiKarbon`, `simpanLaporan`. Algo-058 s/d Algo-060.
- [x] `StatistikController.java` — 6 methods sesuai C-14: `hitungStatistik`, `getDataPohon`, `getDataPenanaman`, `ambilData`, `terapkanFilter`, `teruskanKeView`. Algo-071 s/d Algo-076.

## PHASE 5 — BOUNDARY CLASSES

- [x] `HalamanDataPohon.java` — 5 methods: `ambilDataPohon`, `tampilkanData(List)`, `tampilkanDaftar`, `hapusData(String)`, `tampilkanPesanError(String)`. Atribut: `daftarPohon: List`, `filterData: String`. Uses `DataPohonController`.
- [x] `HalamanDataPenanaman.java` — 3 methods: `ambilDataPenanaman`, `tampilkanData(List)`, `tampilkanPesanError(String)`. Uses `getTanggal()` (Date).
- [x] `FormLaporanPohon.java` — 3 methods: `validasiData(Object)`, `prosesLaporan(Object, File)`, `tampilkanStatus(String)`. Atribut: `fileFoto: String`, `dataInput: Object`. Kondisi lowercase (`rusak`, `mati`, `ditebang`).
- [x] `HalamanStatistik.java` — 6 methods: `ambilData`, `tampilkanStatistik(Object)`, `pilihPeriode(String)`, `tampilkanGrafik(Object)`, `perbaruiTampilan(Object)`, `tampilkanPesanError(String)`. Atribut: `grafikStatistik: Object`, `filterTerpilih: String`.
- [x] `FormDataPohon.java` — 6 methods: `tampilkanForm`, `validasiFile(File)`, `validasiData(Object)`, `prosesInputPohon(Object)`, `simpanDataPohon(Object)`, `tampilkanStatus(String)`. Atribut: `dataInput: Object`. Uses `DataPohonController`.
- [x] `FormLaporanPenanaman.java` — **CREATED**. 4 methods: `tampilkanForm`, `kumpulkanDataInput(DataPenanaman)`, `validasiData(DataPenanaman)`, `tampilkanStatus(String)`.

## PHASE 6 — MAIN & NAVIGATION

- [x] `Main.java` — Uses `DataPohonController` (bukan `PohonController`). No repository references. No `.env` loading. Clean controller initialization.

---

## SUMMARY

| Phase | Total | Done | Remaining |
|-------|-------|------|-----------|
| Phase 1 — Fondasi | 4 | 4 | 0 |
| Phase 2 — Entity | 4 | 4 | 0 |
| Phase 3 — Repository | 1 | 1 (deleted) | 0 |
| Phase 4 — Controller | 4 | 4 | 0 |
| Phase 5 — Boundary | 6 | 6 | 0 |
| Phase 6 — Main | 1 | 1 | 0 |
| **TOTAL** | **20** | **20** | **0** |

---

## FILE STRUCTURE (Final)

```
com.ecotrack/
├── Main.java
├── boundary/
│   ├── HalamanStatistik.java          (C-08)
│   ├── HalamanDataPohon.java          (C-06)
│   ├── HalamanDataPenanaman.java      (C-02)
│   ├── FormLaporanPohon.java          (C-04)
│   ├── FormDataPohon.java             (C-10)
│   └── FormLaporanPenanaman.java      (C-09) ← NEW
├── controller/
│   ├── StatistikController.java       (C-14)
│   ├── DataPohonController.java       (C-13) ← RENAMED
│   ├── PenanamanController.java       (C-11)
│   └── LaporanPohonController.java    (C-12)
├── entity/
│   ├── User.java                      (C-01)
│   ├── DataPohon.java                 (C-07)
│   ├── DataPenanaman.java             (C-03)
│   └── LaporanPohon.java              (C-05)
└── util/
    ├── DBConnection.java
    ├── FileManager.java
    └── UIConstants.java
```

## KEY CHANGES FROM OLD CODE → DPPLOO-01

### Field Renames
| Old | New | File |
|-----|-----|------|
| `kapasitasSerapanKarbon` | `serapanKarbon` | DataPohon |
| `fileFotoPath` | `fileFoto` | DataPohon, LaporanPohon |
| `tanggalPenanaman` | `tanggal` | DataPenanaman |

### Removed Fields
| Field | From |
|-------|------|
| `idUser`, `status`, `createdAt`, `updatedAt` | DataPohon |
| `idUser`, `idPohon`, `createdAt`, `updatedAt` | DataPenanaman |
| `idUser`, `idPohon`, `createdAt` | LaporanPohon |

### Removed Layer
| Item | Status |
|------|--------|
| `repository/` package | **DELETED** |
| `PohonRepository` | Deleted |
| `PenanamanRepository` | Deleted |
| `LaporanRepository` | Deleted |

### Class Rename
| Old | New |
|-----|-----|
| `PohonController` | `DataPohonController` |

### Controller Signature Changes
| Old | New |
|-----|-----|
| `PohonController(PohonRepository repo)` | `DataPohonController()` (no params) |
| `PenanamanController(PenanamanRepository repo)` | `PenanamanController()` (no params) |
| `LaporanPohonController(LaporanRepository repo, PohonController pc)` | `LaporanPohonController()` (no params) |
| `StatistikController.hitungStatistikTotal(String)` | `StatistikController.hitungStatistik(DataPohon, DataPenanaman)` |
| `StatistikController.ambilDataVisualisasi(String)` | `StatistikController.ambilData()` |
| `LaporanPohonController.hitungKapasitasBaru()` | `LaporanPohonController.hitungEstimasiKarbon()` |

### DB Schema Changes
| Old Column | New Column | Table |
|------------|------------|-------|
| `kapasitas_serapan_karbon` | `serapan_karbon` | data_pohon |
| `file_foto_path` | `file_foto` | data_pohon, laporan_pohon |
| `tanggal_penanaman` | `tanggal` | data_penanaman |
| `status` | *(removed)* | data_pohon |
| `VARCHAR(36)`, `VARCHAR(100)` | `VARCHAR` | all tables |
