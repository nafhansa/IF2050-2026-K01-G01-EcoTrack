# IMPLEMENTATION CHECKLIST — EcoTrack FULL-SPEC Alignment

> Status tracking untuk penyelarasan source code dengan FULL-SPEC.md

---

## PHASE 1 — FONDASI (Database & Config)

- [x] `db/init.sql` — Schema disesuaikan spec (column names, types, hapus `status`)
- [x] `DBConnection.java` — Hardcoded values, hapus `.env` loading
- [x] `.gitignore` — Disesuaikan spec

## PHASE 2 — ENTITY CLASSES

- [x] `DataPohon.java` — Rename fields (`serapanKarbon`, `fileFoto`), hapus extra fields, tambah 7 methods
- [x] `DataPenanaman.java` — Rename `tanggalPenanaman` → `tanggal`, type `Date`, hapus extra fields, tambah 5 methods
- [x] `LaporanPohon.java` — Rename `fileFotoPath` → `fileFoto`, hapus extra fields, tambah `simpanLaporan`
- [x] `User.java` — Tambah 10 methods sesuai spec (C-01)

## PHASE 3 — REPOSITORY INTERFACES

- [x] `PohonRepository.java` — Rename `findByNama` → `findByIdOrNama`, fix query tags Q-007 s/d Q-011
- [x] `PenanamanRepository.java` — Tambah `findById`, fix query tags Q-001 s/d Q-005
- [x] `LaporanRepository.java` — Hapus `getHistoryLaporan`, hanya `save` (Q-006)

## PHASE 4 — CONTROLLER CLASSES

- [ ] `PohonController.java` → **Rename ke `DataPohonController.java`** — Fix semua method signatures (C-13), implement Algo-061 s/d Algo-070
- [ ] `PenanamanController.java` — Fix methods: `validasiInput` → `FormLaporanPenanaman.validasiData`, `hitungEstimasi` → `hitungEstimasiKarbon`, tambah 5 methods, implement Algo-051 s/d Algo-057
- [ ] `LaporanPohonController.java` — Rename `hitungKapasitasBaru` → `hitungEstimasiKarbon`, tambah `simpanLaporan`, implement Algo-058 s/d Algo-060
- [ ] `StatistikController.java` — Rename `hitungStatistikTotal` → `hitungStatistik`, tambah 5 methods, implement Algo-071 s/d Algo-076

## PHASE 5 — BOUNDARY CLASSES

- [ ] `HalamanDataPohon.java` — Fix methods: `ambilDataPohon()`, `tampilkanData(List)`, `tampilkanDaftar()`, `hapusData(String)`, `tampilkanPesanError(String)`
- [ ] `HalamanDataPenanaman.java` — Fix methods: `ambilDataPenanaman()`, `tampilkanData(List)`, `tampilkanPesanError(String)`
- [ ] `FormLaporanPohon.java` — Fix methods: `validasiData(Object)`, `prosesLaporan(Object, File)`, `tampilkanStatus(String)`
- [ ] `HalamanStatistik.java` — Fix methods: `ambilData()`, `tampilkanStatistik(Object)`, `pilihPeriode(String)`, `tampilkanGrafik(Object)`, `perbaruiTampilan(Object)`, `tampilkanPesanError(String)`
- [ ] `FormDataPohon.java` — Fix methods: `tampilkanForm()`, `validasiFile(File)`, `validasiData(Object)`, `prosesInputPohon(Object)`, `simpanDataPohon(Object)`, `tampilkanStatus(String)`
- [ ] **`FormLaporanPenanaman.java`** — CREATE NEW: `tampilkanForm()`, `kumpulkanDataInput(DataPenanaman)`, `validasiData(DataPenanaman)`, `tampilkanStatus(String)`

## PHASE 6 — MAIN & NAVIGATION

- [ ] `Main.java` — Update `PohonController` → `DataPohonController`, tambah navigation ke `FormLaporanPenanaman`, fix controller initialization

---

## SUMMARY

| Phase | Total | Done | Remaining |
|-------|-------|------|-----------|
| Phase 1 — Fondasi | 3 | 3 | 0 |
| Phase 2 — Entity | 4 | 4 | 0 |
| Phase 3 — Repository | 3 | 3 | 0 |
| Phase 4 — Controller | 4 | 0 | 4 |
| Phase 5 — Boundary | 6 | 0 | 6 |
| Phase 6 — Main | 1 | 0 | 1 |
| **TOTAL** | **21** | **10** | **11** |

---

## KEY CHANGES TO NOTE

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

### Class Rename
| Old | New |
|-----|-----|
| `PohonController` | `DataPohonController` |

### New File
| File | Phase |
|------|-------|
| `FormLaporanPenanaman.java` | Phase 5 |

### DB Schema Changes
| Old Column | New Column | Table |
|------------|------------|-------|
| `kapasitas_serapan_karbon` | `serapan_karbon` | data_pohon |
| `file_foto_path` | `file_foto` | data_pohon, laporan_pohon |
| `tanggal_penanaman` | `tanggal` | data_penanaman |
| `status` | *(removed)* | data_pohon |
