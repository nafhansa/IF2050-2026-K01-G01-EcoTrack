# FULL-SPEC.md — EcoTrack
> **Sumber kebenaran: PDF Final DPPLOO-01 (51 halaman)**
> **Tujuan: Source code harus PERSIS mengikuti PDF Final**
> **UI/estetika/warna: TIDAK DIUBAH**

---

## 0. CODING ROADMAP — MULAI DARI MANA?

### Urutan yang disarankan (bottom-up, tidak ada dependensi maju):

```
PHASE 1 — FONDASI (Mulai di sini)
  Step 1. Setup project Maven/Gradle + tambah dependency JDBC PostgreSQL + JavaFX
  Step 2. Buat DBConnection.java (singleton JDBC)
  Step 3. Buat semua schema SQL di PostgreSQL (4 tabel)
  Step 4. Buat semua Entity class (User, DataPohon, DataPenanaman, LaporanPohon)

PHASE 2 — DATA LAYER
  Step 5. Buat PohonRepository.java     (Q-007 s/d Q-011)
  Step 6. Buat PenanamanRepository.java (Q-001 s/d Q-005)
  Step 7. Buat LaporanRepository.java   (Q-006)
  Step 8. Buat FileManager.java         (simpan/ambil foto lokal)

PHASE 3 — BUSINESS LOGIC
  Step 9.  Buat DataPohonController.java          (CRUD pohon + validasi + foto)
  Step 10. Buat PenanamanController.java      (CRUD penanaman + hitungEstimasiKarbon)
  Step 11. Buat LaporanPohonController.java   (prosesLaporan + hitungEstimasiKarbon)
  Step 12. Buat StatistikController.java      (hitungStatistik + terapkanFilter)

PHASE 4 — UI (JavaFX)
  Step 13. Buat HalamanDataPohon.fxml + HalamanDataPohon.java       (UC03, UC07)
  Step 14. Buat HalamanDataPenanaman.fxml + HalamanDataPenanaman.java (UC01, UC05)
  Step 15. Buat FormLaporanPohon.fxml + FormLaporanPohon.java        (UC02)
  Step 16. Buat HalamanStatistik.fxml + HalamanStatistik.java        (UC04, UC08)
  Step 17. Buat FormLaporanPenanaman.java (UC05)
  Step 18. Buat FormDataPohon.java (UC06, UC07)
  Step 19. Buat Main.java + navigasi sidebar (User method baru)
```

### Docker PostgreSQL Setup

**Struktur file:**
```
ecotrack/
├── docker-compose.yml
├── .env
├── .gitignore
├── db/
│   └── init.sql
└── src/
    └── ...
```

#### `docker-compose.yml`
```yaml
version: '3.8'

services:
  ecotrack-db:
    image: postgres:16.3
    container_name: ecotrack_postgres
    environment:
      POSTGRES_DB: ecotrack_db
      POSTGRES_USER: ecotrack_user
      POSTGRES_PASSWORD: ecotrack_pass
    ports:
      - "5432:5432"
    volumes:
      - ecotrack_data:/var/lib/postgresql/data
      - ./db/init.sql:/docker-entrypoint-initdb.d/init.sql
    restart: unless-stopped

volumes:
  ecotrack_data:
```

#### `db/init.sql`
```sql
-- =============================================
-- ECOTRACK DATABASE INIT SCRIPT
-- =============================================

CREATE TABLE IF NOT EXISTS "user" (
    id_user   VARCHAR(36) PRIMARY KEY,
    nama      VARCHAR(100) NOT NULL,
    role      VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS data_pohon (
    id_pohon                 VARCHAR(36)  PRIMARY KEY,
    id_user                  VARCHAR(36)  REFERENCES "user"(id_user),
    nama_pohon               VARCHAR(100) NOT NULL,
    usia                     INT,
    lokasi                   VARCHAR(200),
    serapan_karbon           FLOAT,
    file_foto                VARCHAR(500),
    created_at               TIMESTAMP DEFAULT NOW(),
    updated_at               TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS data_penanaman (
    id_penanaman      VARCHAR(36)  PRIMARY KEY,
    id_user           VARCHAR(36)  REFERENCES "user"(id_user),
    id_pohon          VARCHAR(36)  REFERENCES data_pohon(id_pohon),
    lokasi            VARCHAR(200) NOT NULL,
    jenis_pohon       VARCHAR(100) NOT NULL,
    jumlah_pohon      INT          NOT NULL,
    tanggal           DATE         NOT NULL,
    estimasi_karbon   FLOAT,
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS laporan_pohon (
    id_laporan      VARCHAR(36)  PRIMARY KEY,
    id_user         VARCHAR(36)  REFERENCES "user"(id_user),
    id_pohon        VARCHAR(36)  REFERENCES data_pohon(id_pohon),
    kondisi         VARCHAR(50)  NOT NULL,
    lokasi          VARCHAR(200) NOT NULL,
    file_foto       VARCHAR(500),
    estimasi_karbon FLOAT,
    tanggal_laporan DATE         NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW()
);

INSERT INTO "user" (id_user, nama, role)
VALUES ('user-001', 'Admin EcoTrack', 'admin')
ON CONFLICT DO NOTHING;
```

#### `.env`
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecotrack_db
DB_USER=ecotrack_user
DB_PASS=ecotrack_pass
```

#### `.gitignore`
```
.env
*.class
target/
.idea/
*.iml
*.log
```

#### `DBConnection.java`
```java
package com.ecotrack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String HOST = "localhost";
    private static final String PORT = "5432";
    private static final String DB   = "ecotrack_db";
    private static final String USER = "ecotrack_user";
    private static final String PASS = "ecotrack_pass";

    private static final String URL =
        "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;

    private static Connection instance;

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASS);
        }
        return instance;
    }
}
```

#### `pom.xml`
```xml
<dependencies>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.3</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.3</version>
    </dependency>
</dependencies>
```

---

> Aplikasi Manajemen Pendataan Pohon dan Penghijauan Lingkungan
> Bahasa: **Java** | UI: **JavaFX** | DB: **PostgreSQL** | Storage: **Local File System**
> Arsitektur: **Desktop Single-User (Windows)** | Pattern: **Boundary-Controller-Entity (BCE)**

---

## 1. TECH STACK

| Layer | Teknologi |
|---|---|
| Language | Java (JDK 17+) |
| UI Framework | JavaFX |
| Database | PostgreSQL |
| DB Driver | JDBC (PostgreSQL Driver) |
| File Storage | Local Folder |
| Build Tool | Maven atau Gradle |
| IDE | Visual Studio Code / IntelliJ |
| VCS | Git / GitHub |

---

## 2. ARSITEKTUR SISTEM

```
[User]
  └─► [Sistem Antarmuka - JavaFX]
        ├─► [Pencatatan Penanaman]
        ├─► [Pencatatan Data Pohon]
        ├─► [Pencatatan Kondisi Pohon]
        ├─► [Perhitungan Serapan Karbon]
        ├─► [Statistik dan Rekapitulasi]
        └─► [Laporan Berkala]
              ├─► [PostgreSQL]
              └─► [Local File System]
```

### Package Structure
```
com.ecotrack/
├── Main.java
├── boundary/
│   ├── HalamanStatistik.java
│   ├── HalamanDataPohon.java
│   ├── HalamanDataPenanaman.java
│   ├── FormLaporanPohon.java
│   ├── FormDataPohon.java
│   └── FormLaporanPenanaman.java
├── controller/
│   ├── StatistikController.java
│   ├── DataPohonController.java
│   ├── PenanamanController.java
│   └── LaporanPohonController.java
├── entity/
│   ├── User.java
│   ├── DataPohon.java
│   ├── DataPenanaman.java
│   └── LaporanPohon.java
├── repository/
│   ├── PohonRepository.java
│   ├── PenanamanRepository.java
│   └── LaporanRepository.java
└── util/
    ├── DBConnection.java
    └── FileManager.java
```

---

## 3. DATABASE SCHEMA

### Tabel: `user`
```sql
CREATE TABLE "user" (
    id_user   VARCHAR PRIMARY KEY,
    nama      VARCHAR NOT NULL,
    role      VARCHAR NOT NULL
);
```

### Tabel: `data_pohon`
```sql
CREATE TABLE data_pohon (
    id_pohon              VARCHAR PRIMARY KEY,
    id_user               VARCHAR REFERENCES "user"(id_user),
    nama_pohon            VARCHAR NOT NULL,
    usia                  INT,
    lokasi                VARCHAR,
    serapan_karbon        FLOAT,
    file_foto             VARCHAR,
    created_at            TIMESTAMP DEFAULT NOW(),
    updated_at            TIMESTAMP DEFAULT NOW()
);
```

### Tabel: `data_penanaman`
```sql
CREATE TABLE data_penanaman (
    id_penanaman      VARCHAR PRIMARY KEY,
    id_user           VARCHAR REFERENCES "user"(id_user),
    id_pohon          VARCHAR REFERENCES data_pohon(id_pohon),
    lokasi            VARCHAR NOT NULL,
    jenis_pohon       VARCHAR NOT NULL,
    jumlah_pohon      INT NOT NULL,
    tanggal           DATE NOT NULL,
    estimasi_karbon   FLOAT,
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);
```

### Tabel: `laporan_pohon`
```sql
CREATE TABLE laporan_pohon (
    id_laporan      VARCHAR PRIMARY KEY,
    id_user         VARCHAR REFERENCES "user"(id_user),
    id_pohon        VARCHAR REFERENCES data_pohon(id_pohon),
    kondisi         VARCHAR NOT NULL,
    lokasi          VARCHAR NOT NULL,
    file_foto       VARCHAR,
    estimasi_karbon FLOAT,
    tanggal_laporan DATE NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW()
);
```

---

## 4. ENTITY CLASSES

### 4.1 User (C-01)
```java
class User {
    private String idUser;
    private String nama;
    private String role;

    public void pilihMenuPenanaman();
    public void lihatDataPohon();
    public void lihatStatistik();
    public void kirimLaporan(Object dataInput, File fileFoto);
    public String pilihOpsiKelolaData(String opsi);
    public void isiDataPenanaman(DataPenanaman dataPenanaman);
    public Object unggahFoto(File fileFoto);
    public void isiDataPohon(Object dataPohon);
    public void simpanData(Object data, String jenisData);
    public String konfirmasiHapus(String idData);
}
```

### 4.2 DataPenanaman (C-03)
```java
class DataPenanaman {
    private String idPenanaman;
    private String lokasi;
    private String jenisPohon;
    private int jumlahPohon;
    private Date tanggal;
    private float estimasiKarbon;

    public void cariData(String idPenanaman);
    public void simpanData(DataPenanaman data);
    public void ubahData(DataPenanaman data);
    public void hapusData(String idPenanaman);
    public void getDataPenanaman();
}
```

### 4.3 DataPohon (C-07)
```java
class DataPohon {
    private String idPohon;
    private String namaPohon;
    private int usia;
    private String lokasi;
    private float serapanKarbon;
    private String fileFoto;

    public void cariDataPohon(String kriteria);
    public void getDataPohon();
    public void simpanData(DataPohon data);
    public void ubahData(DataPohon data);
    public void hapusData(String idPohon);
    public void simpanDataPohon(Object data);
    public void simpanFoto(File file);
}
```

### 4.4 LaporanPohon (C-05)
```java
class LaporanPohon {
    private String idLaporan;
    private String kondisi;
    private String lokasi;
    private String fileFoto;
    private float estimasiKarbon;

    public void simpanLaporan(LaporanPohon data);
}
```

---

## 5. BOUNDARY CLASSES

### 5.1 HalamanStatistik (C-08)
```java
class HalamanStatistik {
    private Object grafikStatistik;
    private String filterTerpilih;

    public void ambilData();
    public void tampilkanStatistik(Object data);
    public void pilihPeriode(String periode);
    public void tampilkanGrafik(Object data);
    public void perbaruiTampilan(Object data);
    public void tampilkanPesanError(String pesan);
}
```

### 5.2 HalamanDataPohon (C-06)
```java
class HalamanDataPohon {
    private List daftarPohon;
    private String filterData;

    public void ambilDataPohon();
    public void tampilkanData(List dataPohonList);
    public void tampilkanDaftar();
    public void hapusData(String idPohon);
    public void tampilkanPesanError(String pesan);
}
```

### 5.3 HalamanDataPenanaman (C-02)
```java
class HalamanDataPenanaman {
    public void ambilDataPenanaman();
    public void tampilkanData(List data);
    public void tampilkanPesanError(String pesan);
}
```

### 5.4 FormLaporanPohon (C-04)
```java
class FormLaporanPohon {
    private String fileFoto;
    private Object dataInput;

    public boolean validasiData(Object dataInput);
    public void prosesLaporan(Object dataInput, File fileFoto);
    public void tampilkanStatus(String status);
}
```

### 5.5 FormDataPohon (C-10)
```java
class FormDataPohon {
    private Object dataInput;

    public void tampilkanForm();
    public boolean validasiFile(File file);
    public boolean validasiData(Object dataInput);
    public void prosesInputPohon(Object data);
    public void simpanDataPohon(Object data);
    public void tampilkanStatus(String status);
}
```

### 5.6 FormLaporanPenanaman (C-09)
```java
class FormLaporanPenanaman {
    public void tampilkanForm();
    public void kumpulkanDataInput(DataPenanaman data);
    public boolean validasiData(DataPenanaman data);
    public void tampilkanStatus(String status);
}
```

---

## 6. CONTROLLER CLASSES

### 6.1 PenanamanController (C-11)
```java
class PenanamanController {
    public void cariData(String idPenanaman);
    public void ambilDataPenanaman();
    public void simpanDataPenanaman(DataPenanaman dataPenanaman);
    public void ubahDataPenanaman(DataPenanaman dataPenanaman);
    public void hapusDataPenanaman(String idPenanaman);
    public float hitungEstimasiKarbon(DataPenanaman dataPenanaman);
    public void teruskanKeView(Object result);
}
```

**Algoritma:**

Algo-051 `cariData(idPenanaman)`:
```
result <- DataPenanaman.cariData(idPenanaman)
RETURN result
```

Algo-052 `ambilDataPenanaman()`:
```
result <- DataPenanaman.getDataPenanaman()
teruskanKeView(result)
RETURN result
```

Algo-053 `simpanDataPenanaman(dataPenanaman)`:
```
IF FormLaporanPenanaman.validasiData(dataPenanaman) = TRUE THEN
    dataPenanaman.estimasiKarbon <- hitungEstimasiKarbon(dataPenanaman)
    result <- DataPenanaman.simpanData(dataPenanaman)
    teruskanKeView(result)
ELSE
    result <- "Data penanaman tidak valid"
END IF
RETURN result
```

Algo-054 `ubahDataPenanaman(dataPenanaman)`:
```
IF FormLaporanPenanaman.validasiData(dataPenanaman) = TRUE THEN
    dataPenanaman.estimasiKarbon <- hitungEstimasiKarbon(dataPenanaman)
    result <- DataPenanaman.ubahData(dataPenanaman)
    teruskanKeView(result)
ELSE
    result <- "Data penanaman tidak valid"
END IF
RETURN result
```

Algo-055 `hapusDataPenanaman(idPenanaman)`:
```
result <- DataPenanaman.hapusData(idPenanaman)
teruskanKeView(result)
RETURN result
```

Algo-056 `hitungEstimasiKarbon(dataPenanaman)`:
```
dataPohon <- DataPohon.cariDataPohon(dataPenanaman.jenisPohon)
estimasiKarbon <- dataPenanaman.jumlahPohon * dataPohon.serapanKarbon
RETURN estimasiKarbon
```

Algo-057 `teruskanKeView(result)`:
```
RETURN result
```

---

### 6.2 DataPohonController (C-13)
```java
class DataPohonController {
    public void cariDataPohon(String kriteria);
    public void ambilDetailPohon(String idPohon);
    public void ambilDataPohon();
    public void prosesInputPohon(Object data);
    public void simpanDataPohon(Object data);
    public void ubahDataPohon(Object data);
    public void hapusDataPohon(String idPohon);
    public void simpanFoto(File file);
    public boolean validasiData(Object data);
    public void tampilkanStatus(String status);
}
```

**Algoritma:**

Algo-061 `cariDataPohon(kriteria)`:
```
result <- DataPohon.cariDataPohon(kriteria)
RETURN result
```

Algo-062 `ambilDetailPohon(idPohon)`:
```
result <- DataPohon.cariDataPohon(idPohon)
RETURN result
```

Algo-063 `ambilDataPohon()`:
```
result <- DataPohon.getDataPohon()
RETURN result
```

Algo-064 `prosesInputPohon(data)`:
```
IF validasiData(data) = TRUE THEN
    result <- simpanDataPohon(data)
ELSE
    result <- "Data pohon tidak valid"
END IF
tampilkanStatus(result)
RETURN result
```

Algo-065 `simpanDataPohon(data)`:
```
IF data.fileFoto IS NOT NULL THEN
    data.fileFoto <- simpanFoto(data.fileFoto)
END IF
result <- DataPohon.simpanDataPohon(data)
RETURN result
```

Algo-066 `ubahDataPohon(data)`:
```
IF validasiData(data) = TRUE THEN
    result <- DataPohon.ubahData(data)
ELSE
    result <- "Data pohon tidak valid"
END IF
RETURN result
```

Algo-067 `hapusDataPohon(idPohon)`:
```
result <- DataPohon.hapusData(idPohon)
RETURN result
```

Algo-068 `simpanFoto(file)`:
```
result <- DataPohon.simpanFoto(file)
RETURN result
```

Algo-069 `validasiData(data)`:
```
IF data.namaPohon IS NULL OR data.usia < 0 OR data.serapanKarbon < 0 THEN
    RETURN FALSE
ELSE
    RETURN TRUE
END IF
```

Algo-070 `tampilkanStatus(status)`:
```
RETURN status
```

---

### 6.3 LaporanPohonController (C-12)
```java
class LaporanPohonController {
    public void prosesLaporan(LaporanPohon dataLaporan);
    public float hitungEstimasiKarbon(LaporanPohon data);
    public void simpanLaporan(LaporanPohon dataLaporan);
}
```

**Algoritma:**

Algo-058 `prosesLaporan(dataLaporan)`:
```
dataLaporan.estimasiKarbon <- hitungEstimasiKarbon(dataLaporan)
result <- simpanLaporan(dataLaporan)
RETURN result
```

Algo-059 `hitungEstimasiKarbon(dataLaporan)`:
```
IF dataLaporan.kondisi = "ditebang" THEN
    estimasiKarbon <- 0
ELSE IF dataLaporan.kondisi = "rusak" THEN
    estimasiKarbon <- estimasi karbon lama * 0.5
ELSE
    estimasiKarbon <- estimasi karbon lama
END IF
RETURN estimasiKarbon
```

Algo-060 `simpanLaporan(dataLaporan)`:
```
result <- LaporanPohon.simpanLaporan(dataLaporan)
RETURN result
```

---

### 6.4 StatistikController (C-14)
```java
class StatistikController {
    public void hitungStatistik(DataPohon dataPohon, DataPenanaman dataPenanaman);
    public void getDataPohon();
    public void getDataPenanaman();
    public void ambilData();
    public void terapkanFilter(String filterPeriode);
    public void teruskanKeView(Object statistik);
}
```

**Algoritma:**

Algo-071 `hitungStatistik(dataPohon, dataPenanaman)`:
```
totalPohon <- SUM(dataPenanaman.jumlahPohon)
totalKarbon <- SUM(dataPenanaman.estimasiKarbon)
statistik <- { totalPohon, totalKarbon, dataPohon, dataPenanaman }
RETURN statistik
```

Algo-072 `getDataPohon()`:
```
result <- DataPohon.getDataPohon()
RETURN result
```

Algo-073 `getDataPenanaman()`:
```
result <- DataPenanaman.getDataPenanaman()
RETURN result
```

Algo-074 `ambilData()`:
```
dataPohon <- getDataPohon()
dataPenanaman <- getDataPenanaman()
statistik <- hitungStatistik(dataPohon, dataPenanaman)
teruskanKeView(statistik)
RETURN statistik
```

Algo-075 `terapkanFilter(filterPeriode)`:
```
dataPenanaman <- DO QUERY Q-012
dataPohon <- getDataPohon()
statistik <- hitungStatistik(dataPohon, dataPenanaman)
teruskanKeView(statistik)
RETURN statistik
```

Algo-076 `teruskanKeView(statistik)`:
```
RETURN statistik
```

---

## 7. REPOSITORY CLASSES

### 7.1 PenanamanRepository
```java
class PenanamanRepository {
    public List<DataPenanaman> findAll();
    public DataPenanaman findById(String id);
    public boolean save(DataPenanaman d);
    public boolean update(DataPenanaman d);
    public boolean delete(String idPenanaman);
}
```

### 7.2 PohonRepository
```java
class PohonRepository {
    public List<DataPohon> findAll();
    public DataPohon findByIdOrNama(String q);
    public boolean save(DataPohon d);
    public boolean update(DataPohon d);
    public boolean delete(String id);
}
```

### 7.3 LaporanRepository
```java
class LaporanRepository {
    public boolean save(LaporanPohon l);
}
```

---

## 8. SQL QUERIES

| Kode | SQL | Keterangan | Kelas |
|---|---|---|---|
| Q-001 | `SELECT * FROM data_penanaman ORDER BY tanggal DESC;` | Ambil semua penanaman | DataPenanaman |
| Q-002 | `SELECT * FROM data_penanaman WHERE id_penanaman = ?;` | Cari penanaman by ID | DataPenanaman |
| Q-003 | `INSERT INTO data_penanaman (id_penanaman, lokasi, jenis_pohon, jumlah_pohon, tanggal, estimasi_karbon) VALUES (?, ?, ?, ?, ?, ?);` | Simpan penanaman | DataPenanaman |
| Q-004 | `UPDATE data_penanaman SET lokasi=?, jenis_pohon=?, jumlah_pohon=?, tanggal=?, estimasi_karbon=? WHERE id_penanaman=?;` | Update penanaman | DataPenanaman |
| Q-005 | `DELETE FROM data_penanaman WHERE id_penanaman=?;` | Hapus penanaman | DataPenanaman |
| Q-006 | `INSERT INTO laporan_pohon (id_laporan, kondisi, lokasi, file_foto, estimasi_karbon) VALUES (?, ?, ?, ?, ?);` | Simpan laporan | LaporanPohon |
| Q-007 | `SELECT * FROM data_pohon ORDER BY nama_pohon ASC;` | Ambil semua pohon | DataPohon |
| Q-008 | `SELECT * FROM data_pohon WHERE id_pohon=? OR nama_pohon ILIKE ?;` | Cari pohon | DataPohon |
| Q-009 | `INSERT INTO data_pohon (id_pohon, nama_pohon, usia, lokasi, serapan_karbon, file_foto) VALUES (?, ?, ?, ?, ?, ?);` | Simpan pohon | DataPohon |
| Q-010 | `UPDATE data_pohon SET nama_pohon=?, usia=?, lokasi=?, serapan_karbon=?, file_foto=? WHERE id_pohon=?;` | Update pohon | DataPohon |
| Q-011 | `DELETE FROM data_pohon WHERE id_pohon=?;` | Hapus pohon | DataPohon |
| Q-012 | `SELECT * FROM data_penanaman WHERE tanggal BETWEEN ? AND ? ORDER BY tanggal ASC;` | Filter by periode | StatistikController |
| Q-013 | `SELECT SUM(jumlah_pohon) AS total_pohon, SUM(estimasi_karbon) AS total_karbon FROM data_penanaman;` | Total statistik | StatistikController |

---

## 9. ALGORITMA LENGKAP

### Algo-001 — User.pilihMenuPenanaman()
```
pilihMenuPenanaman():
  HalamanDataPenanaman.ambilDataPenanaman()
```

### Algo-002 — User.lihatDataPohon()
```
lihatDataPohon():
  HalamanDataPohon.ambilDataPohon()
```

### Algo-003 — User.lihatStatistik()
```
lihatStatistik():
  HalamanStatistik.ambilData()
```

### Algo-004 — User.kirimLaporan(dataInput, fileFoto)
```
kirimLaporan(dataInput, fileFoto):
  FormLaporanPohon.prosesLaporan(dataInput, fileFoto)
```

### Algo-005 — User.pilihOpsiKelolaData(opsi)
```
pilihOpsiKelolaData(opsi):
  IF opsi = "Tambah" OR opsi = "Ubah" OR opsi = "Hapus"
    RETURN opsi
  ELSE
    RETURN "Opsi tidak valid"
  END IF
```

### Algo-006 — User.isiDataPenanaman(dataPenanaman)
```
isiDataPenanaman(dataPenanaman):
  FormLaporanPenanaman.kumpulkanDataInput(dataPenanaman)
```

### Algo-007 — User.unggahFoto(fileFoto)
```
unggahFoto(fileFoto):
  IF fileFoto NOT NULL
    RETURN fileFoto
  ELSE
    RETURN "Foto belum dipilih"
  END IF
```

### Algo-008 — User.isiDataPohon(dataPohon)
```
isiDataPohon(dataPohon):
  FormDataPohon.prosesInputPohon(dataPohon)
```

### Algo-009 — User.simpanData(data, jenisData)
```
simpanData(data, jenisData):
  IF jenisData = "penanaman"
    PenanamanController.simpanDataPenanaman(data)
  ELSE IF jenisData = "pohon"
    DataPohonController.simpanDataPohon(data)
  END IF
```

### Algo-010 — User.konfirmasiHapus(idData)
```
konfirmasiHapus(idData):
  IF data dengan idData DITEMUKAN
    RETURN "Hapus disetujui"
  ELSE
    RETURN "Data tidak ditemukan"
  END IF
```

### Algo-011 — HalamanDataPenanaman.ambilDataPenanaman()
```
ambilDataPenanaman():
  dataPenanamanList <- PenanamanController.ambilDataPenanaman()
  IF dataPenanamanList IS NOT EMPTY THEN
    tampilkanData(dataPenanamanList)
  ELSE
    tampilkanPesanError("Data penanaman belum tersedia")
  END IF
```

### Algo-014 — DataPenanaman.cariData(idPenanaman)
```
cariData(idPenanaman):
  IF idPenanaman IS NULL
    Q-001
  ELSE
    Q-002
  END IF
```

### Algo-019 — FormLaporanPohon.validasiData(dataInput)
```
validasiData(dataInput):
  IF dataInput.kondisi IS NULL OR dataInput.lokasi IS NULL
    RETURN FALSE
  ELSE
    RETURN TRUE
  END IF
```

### Algo-020 — FormLaporanPohon.prosesLaporan(dataInput, fileFoto)
```
prosesLaporan(dataInput, fileFoto):
  IF validasiData(dataInput) = TRUE THEN
    dataInput.fileFoto <- fileFoto
    result <- LaporanPohonController.prosesLaporan(dataInput)
    tampilkanStatus(result)
  ELSE
    tampilkanStatus("Data laporan belum lengkap")
  END IF
```

### Algo-043 — FormLaporanPenanaman.validasiData(dataPenanaman)
```
validasiData(dataPenanaman):
  IF dataPenanaman.lokasi IS NULL OR dataPenanaman.jenisPohon IS NULL OR dataPenanaman.jumlahPohon <= 0
    RETURN FALSE
  ELSE
    RETURN TRUE
  END IF
```

### Algo-046 — FormDataPohon.validasiFile(file)
```
validasiFile(file):
  IF file IS NULL
    RETURN FALSE
  ELSE
    RETURN TRUE
  END IF
```

### Algo-047 — FormDataPohon.validasiData(dataInput)
```
validasiData(dataInput):
  IF dataInput.namaPohon IS NULL OR dataInput.usia < 0 OR dataInput.serapanKarbon < 0
    RETURN FALSE
  ELSE
    RETURN TRUE
  END IF
```

---

## 10. USE CASE MAPPING

| UC | Nama | Boundary | Controller | Entity |
|---|---|---|---|---|
| UC01 | Melihat Data Penanaman | HalamanDataPenanaman | PenanamanController | DataPenanaman |
| UC02 | Melaporkan Pohon Rusak | FormLaporanPohon | LaporanPohonController | LaporanPohon |
| UC03 | Melihat Data Pohon | HalamanDataPohon | DataPohonController | DataPohon |
| UC04 | Melihat Statistik | HalamanStatistik | StatistikController | DataPohon, DataPenanaman |
| UC05 | Mengelola Data Penanaman | FormLaporanPenanaman | PenanamanController | DataPenanaman |
| UC06 | Menginput Foto dan Data Pohon | FormDataPohon | DataPohonController | DataPohon |
| UC07 | Mengelola Data Pohon | HalamanDataPohon, FormDataPohon | DataPohonController | DataPohon |
| UC08 | Memfilter Statistik | HalamanStatistik | StatistikController | DataPenanaman, DataPohon |

---

## 11. SEQUENCE FLOWS

### UC01 — Normal
```
User.pilihMenuPenanaman()
→ HalamanDataPenanaman.ambilDataPenanaman()
→ PenanamanController.ambilDataPenanaman()
→ DataPenanaman.cariData() / getDataPenanaman()
← dataPenanaman
← dataPenanaman
→ HalamanDataPenanaman.tampilkanData()
← User
```

### UC01 — Data Kosong
```
... → DataPenanaman.cariData() → dataKosong
← dataKosong → PenanamanController → HalamanDataPenanaman
→ HalamanDataPenanaman.tampilkanPesanError()
```

### UC02 — Normal
```
User.isiLaporan() + User.unggahFoto()
→ FormLaporanPohon.kumpulkanDataInput()
User.kirimLaporan()
→ FormLaporanPohon.validasiData()
→ FormLaporanPohon.prosesLaporan(dataInput, fileFoto)
→ LaporanPohonController.prosesLaporan(dataLaporan)
→ LaporanPohonController.hitungEstimasiKarbon()
→ LaporanPohon.simpanLaporan()
← statusSimpan
← laporanBerhasil
→ FormLaporanPohon.tampilkanStatus()
```

### UC02 — Data Tidak Lengkap
```
User.kirimLaporan()
→ FormLaporanPohon.validasiData() → GAGAL
→ FormLaporanPohon.tampilkanStatus("Data laporan belum lengkap")
```

### UC03 — Normal
```
User.lihatDataPohon()
→ HalamanDataPohon.ambilDataPohon()
→ DataPohonController.ambilDataPohon()
→ DataPohon.cariDataPohon() / getDataPohon()
← daftarPohon
→ HalamanDataPohon.tampilkanData()
User.pilihDataPohon()
→ HalamanDataPohon.ambilDetailPohon(idPohon)
→ DataPohonController.ambilDetailPohon(idPohon)
→ DataPohon.cariDataPohon(idPohon)
← detailPohon
→ HalamanDataPohon.tampilkanData()
```

### UC04 — Tanpa Filter
```
User.lihatStatistik()
→ HalamanStatistik.ambilData()
→ StatistikController.ambilData()
→ DataPohon.getDataPohon() → dataPohon
→ DataPenanaman.getDataPenanaman() → dataPenanaman
→ StatistikController.hitungStatistik()
← hasilStatistik
→ HalamanStatistik.tampilkanStatistik()
```

### UC08 — Filter Periode
```
User.pilihFilterPeriode()
→ HalamanStatistik.pilihPeriode(filter)
→ StatistikController.terapkanFilter(filter)
→ DataPenanaman [Q-012]
→ DataPohon.getDataPohon()
→ StatistikController.hitungStatistik()
→ StatistikController.teruskanKeView()
→ HalamanStatistik.perbaruiTampilan()
→ tampilkan grafik
```

### UC05 — Normal
```
User.pilihOpsiKelolaData()
→ FormLaporanPenanaman.tampilkanForm()
User.isiDataPenanaman()
→ FormLaporanPenanaman.simpanDataPenanaman(data)
→ PenanamanController.validasiData()
→ DataPenanaman.simpanData() → status
→ FormLaporanPenanaman.tampilkanStatus("berhasil")
← tampilkan pesan
```

### UC06 — Normal
```
→ FormDataPohon.tampilkanForm()
User.unggahFoto()
User.isiDataPohon()
→ FormDataPohon.prosesInputPohon(data)
→ DataPohonController.validasiData()
→ DataPohon.simpanFoto(file)
→ DataPohon.simpanDataPohon(data) → status
→ FormDataPohon.tampilkanStatus("berhasil"/"gagal")
```

### UC07 — Normal
```
User.pilihOpsiKelolaData()
→ HalamanDataPohon.tampilkanDaftar()
User.pilihTambah/Edit()
→ FormDataPohon.tampilkanForm()
User.isiDataPohon()
→ DataPohonController.simpanDataPohon(data)
→ DataPohonController.validasiData()
→ DataPohon.simpanData() → status
→ FormDataPohon.tampilkanStatus()
→ HalamanDataPohon.refreshData()
```

---

## 12. CLASS DIAGRAM DETAIL

### Boundary Layer
```
HalamanStatistik
  + ambilData(): void
  + tampilkanStatistik(data: Object): void
  + pilihPeriode(periode: String): void
  + tampilkanGrafik(data: Object): void
  + perbaruiTampilan(data: Object): void
  + tampilkanPesanError(pesan: String): void

HalamanDataPohon
  + ambilDataPohon(): void
  + tampilkanData(dataPohonList: List): void
  + tampilkanDaftar(): void
  + hapusData(idPohon: String): void
  + tampilkanPesanError(pesan: String): void

HalamanDataPenanaman
  + ambilDataPenanaman(): void
  + tampilkanData(data: List): void
  + tampilkanPesanError(pesan: String): void

FormLaporanPohon
  + validasiData(dataInput: Object): boolean
  + prosesLaporan(dataInput: Object, fileFoto: File): void
  + tampilkanStatus(status: String): void

FormDataPohon
  + tampilkanForm(): void
  + validasiFile(file: File): boolean
  + validasiData(dataInput: Object): boolean
  + prosesInputPohon(data: Object): void
  + simpanDataPohon(data: Object): void
  + tampilkanStatus(status: String): void

FormLaporanPenanaman
  + tampilkanForm(): void
  + kumpulkanDataInput(data: DataPenanaman): void
  + validasiData(data: DataPenanaman): boolean
  + tampilkanStatus(status: String): void
```

### Controller Layer
```
PenanamanController
  + cariData(idPenanaman: String): void
  + ambilDataPenanaman(): void
  + simpanDataPenanaman(data: DataPenanaman): void
  + ubahDataPenanaman(data: DataPenanaman): void
  + hapusDataPenanaman(id: String): void
  + hitungEstimasiKarbon(data: DataPenanaman): float
  + teruskanKeView(result: Object): void

DataPohonController
  + cariDataPohon(kriteria: String): void
  + ambilDetailPohon(idPohon: String): void
  + ambilDataPohon(): void
  + prosesInputPohon(data: Object): void
  + simpanDataPohon(data: Object): void
  + ubahDataPohon(data: Object): void
  + hapusDataPohon(idPohon: String): void
  + simpanFoto(file: File): void
  + validasiData(data: Object): boolean
  + tampilkanStatus(status: String): void

LaporanPohonController
  + prosesLaporan(dataLaporan: LaporanPohon): void
  + hitungEstimasiKarbon(data: LaporanPohon): float
  + simpanLaporan(dataLaporan: LaporanPohon): void

StatistikController
  + hitungStatistik(dp: DataPohon, dpt: DataPenanaman): void
  + getDataPohon(): void
  + getDataPenanaman(): void
  + ambilData(): void
  + terapkanFilter(filterPeriode: String): void
  + teruskanKeView(statistik: Object): void
```

### Entity Layer
```
User
  - idUser: String
  - nama: String
  - role: String
  + pilihMenuPenanaman(): void
  + lihatDataPohon(): void
  + lihatStatistik(): void
  + kirimLaporan(dataInput: Object, fileFoto: File): void
  + pilihOpsiKelolaData(opsi: String): String
  + isiDataPenanaman(dataPenanaman: DataPenanaman): void
  + unggahFoto(fileFoto: File): Object
  + isiDataPohon(dataPohon: Object): void
  + simpanData(data: Object, jenisData: String): void
  + konfirmasiHapus(idData: String): String

DataPohon
  - idPohon: String
  - namaPohon: String
  - usia: int
  - lokasi: String
  - serapanKarbon: float
  - fileFoto: String
  + cariDataPohon(kriteria: String): void
  + getDataPohon(): void
  + simpanData(data: DataPohon): void
  + ubahData(data: DataPohon): void
  + hapusData(idPohon: String): void
  + simpanDataPohon(data: Object): void
  + simpanFoto(file: File): void

DataPenanaman
  - idPenanaman: String
  - lokasi: String
  - jenisPohon: String
  - jumlahPohon: int
  - tanggal: Date
  - estimasiKarbon: float
  + cariData(idPenanaman: String): void
  + simpanData(data: DataPenanaman): void
  + ubahData(data: DataPenanaman): void
  + hapusData(idPenanaman: String): void
  + getDataPenanaman(): void

LaporanPohon
  - idLaporan: String
  - kondisi: String
  - lokasi: String
  - fileFoto: String
  - estimasiKarbon: float
  + simpanLaporan(data: LaporanPohon): void
```

---

## 13. UTIL CLASSES

### DBConnection.java
```java
class DBConnection {
    private static Connection instance;
    public static Connection getConnection();
}
```

### FileManager.java
```java
class FileManager {
    public static String saveFoto(File sourceFile, String destDir);
    public static boolean deleteFile(String filePath);
    public static File getFile(String filePath);
}
```

---

## 14. TRACEABILITY MATRIX

| Kelas Analisis | Kelas Perancangan | Use Case |
|---|---|---|
| C-01 User | User | UC01–UC08 |
| C-02 HalamanDataPenanaman | HalamanDataPenanaman | UC01, UC05 |
| C-03 DataPenanaman | DataPenanaman | UC01, UC04, UC05, UC08 |
| C-03 DataPenanaman | PenanamanRepository | UC01, UC04, UC05, UC08 |
| C-04 FormLaporanPohon | FormLaporanPohon | UC02 |
| C-05 LaporanPohon | LaporanPohon | UC02 |
| C-05 LaporanPohon | LaporanRepository | UC02 |
| C-06 HalamanDataPohon | HalamanDataPohon | UC03, UC07 |
| C-07 DataPohon | DataPohon | UC03, UC04, UC06, UC07, UC08 |
| C-07 DataPohon | PohonRepository | UC03, UC04, UC06, UC07, UC08 |
| C-08 HalamanStatistik | HalamanStatistik | UC04, UC08 |
| C-09 FormLaporanPenanaman | FormLaporanPenanaman | UC05 |
| C-10 FormDataPohon | FormDataPohon | UC06, UC07 |
| C-11 PenanamanController | PenanamanController | UC01, UC05 |
| C-12 LaporanPohonController | LaporanPohonController | UC02 |
| C-13 DataPohonController | DataPohonController | UC03, UC06, UC07 |
| C-14 StatistikController | StatistikController | UC04, UC08 |

---

## 15. NOTES & CONSTRAINTS

1. **Aplikasi desktop single-user** — tidak ada authentication kompleks.
2. **Foto pohon** disimpan di local folder, DB hanya menyimpan path.
3. **Estimasi karbon dihitung otomatis** saat menyimpan penanaman baru maupun laporan.
4. **Filter statistik** berdasarkan periode waktu atau tanpa filter.
5. **JavaFX Charts** untuk Line Chart dan Bar Chart di HalamanStatistik.
6. Semua query menggunakan **PreparedStatement**.
7. `id_*` fields menggunakan **UUID**.
8. **Controller langsung panggil Entity** — PDF tidak menyebut Repository layer secara eksplisit.
9. **`DataPohon.cariDataPohon(jenisPohon)`** cari by nama pohon (bukan ID).
10. **Q-008**: `WHERE id_pohon = ? OR nama_pohon ILIKE ?` — satu query untuk dua kebutuhan.
11. **`FormLaporanPenanaman`** boundary terpisah untuk input penanaman. **`HalamanDataPenanaman`** hanya display list.
12. **`prosesLaporan()`** di `FormLaporanPohon` set `fileFoto` ke `dataInput.fileFoto` SEBELUM kirim ke controller.
13. **`hitungEstimasiKarbon()`** berbeda:
    - LaporanPohonController: ditebang=0, rusak=*0.5, else=sama
    - PenanamanController: jumlahPohon * serapanKarbon
14. **Status/kondisi laporan**: `"ditebang"`, `"rusak"`, `"mati"` (lowercase).
15. **UI/estetika/warna TIDAK DIUBAH** — color palette, spacing, layout tetap sama.
