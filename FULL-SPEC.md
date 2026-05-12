# FULL-SPEC.md — EcoTrack
> **Sumber kebenaran: DPPLOO-01 (51 halaman) — Dasar Perancangan Perangkat Lunak**
> **Tujuan: Source code harus PERSIS mengikuti DPPLOO-01**
> **UI/estetika/warna: TIDAK DIUBAH**
> **Arsitektur: BCE (Boundary-Controller-Entity) — TANPA Repository layer**

---

## 0. CODING ROADMAP

```
PHASE 1 — FONDASI
  Step 1. Setup project Maven + dependency JDBC PostgreSQL + JavaFX
  Step 2. Buat DBConnection.java (singleton JDBC, hardcoded values)
  Step 3. Buat schema SQL di PostgreSQL (4 tabel)
  Step 4. Buat Entity classes (User, DataPohon, DataPenanaman, LaporanPohon)

PHASE 2 — BUSINESS LOGIC (Controller langsung panggil Entity)
  Step 5.  Buat PenanamanController.java      (C-11, Algo-051 s/d Algo-057)
  Step 6.  Buat LaporanPohonController.java   (C-12, Algo-058 s/d Algo-060)
  Step 7.  Buat DataPohonController.java      (C-13, Algo-061 s/d Algo-070)
  Step 8.  Buat StatistikController.java      (C-14, Algo-071 s/d Algo-076)
  Step 9.  Buat FileManager.java              (simpan/ambil foto lokal)

PHASE 3 — UI (JavaFX Boundary)
  Step 10. Buat HalamanDataPenanaman.java     (C-02, UC01, UC05)
  Step 11. Buat FormLaporanPohon.java         (C-04, UC02)
  Step 12. Buat HalamanDataPohon.java         (C-06, UC03, UC07)
  Step 13. Buat HalamanStatistik.java         (C-08, UC04, UC08)
  Step 14. Buat FormLaporanPenanaman.java     (C-09, UC05)
  Step 15. Buat FormDataPohon.java            (C-10, UC06, UC07)
  Step 16. Buat Main.java + navigasi sidebar
```

### Docker PostgreSQL Setup

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
CREATE TABLE IF NOT EXISTS "user" (
    id_user   VARCHAR PRIMARY KEY,
    nama      VARCHAR NOT NULL,
    role      VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS data_pohon (
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

CREATE TABLE IF NOT EXISTS data_penanaman (
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

CREATE TABLE IF NOT EXISTS laporan_pohon (
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

INSERT INTO "user" (id_user, nama, role)
VALUES ('user-001', 'Admin EcoTrack', 'admin')
ON CONFLICT DO NOTHING;
```

#### `.env`
```
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
| Build Tool | Maven |
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

### Package Structure (BCE — TANPA Repository)
```
com.ecotrack/
├── Main.java
├── boundary/
│   ├── HalamanStatistik.java          (C-08)
│   ├── HalamanDataPohon.java          (C-06)
│   ├── HalamanDataPenanaman.java      (C-02)
│   ├── FormLaporanPohon.java          (C-04)
│   ├── FormDataPohon.java             (C-10)
│   └── FormLaporanPenanaman.java      (C-09)
├── controller/
│   ├── StatistikController.java       (C-14)
│   ├── DataPohonController.java       (C-13)
│   ├── PenanamanController.java       (C-11)
│   └── LaporanPohonController.java    (C-12)
├── entity/
│   ├── User.java                      (C-01)
│   ├── DataPohon.java                 (C-07)
│   ├── DataPenanaman.java             (C-03)
│   └── LaporanPohon.java              (C-05)
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

    public void pilihMenuPenanaman();                        // Algo-001
    public void lihatDataPohon();                            // Algo-002
    public void lihatStatistik();                            // Algo-003
    public void kirimLaporan(Object dataInput, File fileFoto); // Algo-004
    public String pilihOpsiKelolaData(String opsi);          // Algo-005
    public void isiDataPenanaman(DataPenanaman dataPenanaman); // Algo-006
    public Object unggahFoto(File fileFoto);                 // Algo-007
    public void isiDataPohon(Object dataPohon);              // Algo-008
    public void simpanData(Object data, String jenisData);   // Algo-009
    public String konfirmasiHapus(String idData);            // Algo-010
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

    public void cariData(String idPenanaman);      // Algo-014, Q-001/Q-002
    public void simpanData(DataPenanaman data);    // Algo-015, Q-003
    public void ubahData(DataPenanaman data);      // Algo-016, Q-004
    public void hapusData(String idPenanaman);     // Algo-017, Q-005
    public void getDataPenanaman();                // Algo-018, Q-001
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

    public void cariDataPohon(String kriteria);  // Algo-028, Q-007/Q-008
    public void getDataPohon();                  // Algo-029, Q-007
    public void simpanData(DataPohon data);      // Algo-030, Q-009
    public void ubahData(DataPohon data);        // Algo-031, Q-010
    public void hapusData(String idPohon);       // Algo-032, Q-011
    public void simpanDataPohon(Object data);    // Algo-033
    public void simpanFoto(File file);           // Algo-034
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

    public void simpanLaporan(LaporanPohon data); // Algo-022, Q-006
}
```

---

## 5. BOUNDARY CLASSES

### 5.1 HalamanStatistik (C-08)
```java
class HalamanStatistik {
    private Object grafikStatistik;
    private String filterTerpilih;

    public void ambilData();                   // Algo-035
    public void tampilkanStatistik(Object data); // Algo-036
    public void pilihPeriode(String periode);  // Algo-037
    public void tampilkanGrafik(Object data);  // Algo-038
    public void perbaruiTampilan(Object data); // Algo-039
    public void tampilkanPesanError(String pesan); // Algo-040
}
```

### 5.2 HalamanDataPohon (C-06)
```java
class HalamanDataPohon {
    private List daftarPohon;
    private String filterData;

    public void ambilDataPohon();              // Algo-023
    public void tampilkanData(List dataPohonList); // Algo-024
    public void tampilkanDaftar();             // Algo-025
    public void hapusData(String idPohon);     // Algo-026
    public void tampilkanPesanError(String pesan); // Algo-027
}
```

### 5.3 HalamanDataPenanaman (C-02)
```java
class HalamanDataPenanaman {
    public void ambilDataPenanaman();          // Algo-011
    public void tampilkanData(List data);      // Algo-012
    public void tampilkanPesanError(String pesan); // Algo-013
}
```

### 5.4 FormLaporanPohon (C-04)
```java
class FormLaporanPohon {
    private String fileFoto;
    private Object dataInput;

    public boolean validasiData(Object dataInput);        // Algo-019
    public void prosesLaporan(Object dataInput, File fileFoto); // Algo-020
    public void tampilkanStatus(String status);           // Algo-021
}
```

### 5.5 FormDataPohon (C-10)
```java
class FormDataPohon {
    private Object dataInput;

    public void tampilkanForm();               // Algo-045
    public boolean validasiFile(File file);    // Algo-046
    public boolean validasiData(Object dataInput); // Algo-047
    public void prosesInputPohon(Object data); // Algo-048
    public void simpanDataPohon(Object data);  // Algo-049
    public void tampilkanStatus(String status); // Algo-050
}
```

### 5.6 FormLaporanPenanaman (C-09)
```java
class FormLaporanPenanaman {
    public void tampilkanForm();               // Algo-041
    public void kumpulkanDataInput(DataPenanaman data); // Algo-042
    public boolean validasiData(DataPenanaman data); // Algo-043
    public void tampilkanStatus(String status); // Algo-044
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

## 7. SQL QUERIES

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

## 8. ALGORITMA LENGKAP (Algo-001 s/d Algo-076)

### 8.1 Kelas User

Algo-001 `pilihMenuPenanaman()`:
```
HalamanDataPenanaman.ambilDataPenanaman()
```

Algo-002 `lihatDataPohon()`:
```
HalamanDataPohon.ambilDataPohon()
```

Algo-003 `lihatStatistik()`:
```
HalamanStatistik.ambilData()
```

Algo-004 `kirimLaporan(dataInput, fileFoto)`:
```
FormLaporanPohon.prosesLaporan(dataInput, fileFoto)
```

Algo-005 `pilihOpsiKelolaData(opsi)`:
```
IF opsi = "Tambah" OR opsi = "Ubah" OR opsi = "Hapus"
    RETURN opsi
ELSE
    RETURN "Opsi tidak valid"
END IF
```

Algo-006 `isiDataPenanaman(dataPenanaman)`:
```
FormLaporanPenanaman.kumpulkanDataInput(dataPenanaman)
```

Algo-007 `unggahFoto(fileFoto)`:
```
IF fileFoto IS NOT NULL
    RETURN fileFoto
ELSE
    RETURN "Foto belum dipilih"
END IF
```

Algo-008 `isiDataPohon(dataPohon)`:
```
FormDataPohon.prosesInputPohon(dataPohon)
```

Algo-009 `simpanData(data, jenisData)`:
```
IF jenisData = "Penanaman"
    PenanamanController.simpanDataPenanaman(data)
ELSE IF jenisData = "Pohon"
    DataPohonController.simpanDataPohon(data)
ELSE
    RETURN "Jenis data tidak valid"
END IF
```

Algo-010 `konfirmasiHapus(idData)`:
```
IF idData IS NOT NULL
    RETURN "Hapus disetujui"
ELSE
    RETURN "Data tidak ditemukan"
END IF
```

### 8.2 Kelas HalamanDataPenanaman

Algo-011 `ambilDataPenanaman()`:
```
dataPenanamanList <- PenanamanController.ambilDataPenanaman()
IF dataPenanamanList IS NOT EMPTY
    tampilkanData(dataPenanamanList)
ELSE
    tampilkanPesanError("Data penanaman belum tersedia")
END IF
```

Algo-012 `tampilkanData(dataPenanamanList)`:
```
DISPLAY dataPenanamanList
```

Algo-013 `tampilkanPesanError(pesan)`:
```
DISPLAY pesan
```

### 8.3 Kelas DataPenanaman

Algo-014 `cariData(idPenanaman)`:
```
IF idPenanaman IS NULL
    result <- DO QUERY Q-001
ELSE
    result <- DO QUERY Q-002
END IF
RETURN result
```

Algo-015 `simpanData(dataPenanaman)`:
```
result <- DO QUERY Q-003
RETURN result
```

Algo-016 `ubahData(dataPenanaman)`:
```
result <- DO QUERY Q-004
RETURN result
```

Algo-017 `hapusData(idPenanaman)`:
```
result <- DO QUERY Q-005
RETURN result
```

Algo-018 `getDataPenanaman()`:
```
result <- DO QUERY Q-001
RETURN result
```

### 8.4 Kelas FormLaporanPohon

Algo-019 `validasiData(dataInput)`:
```
IF dataInput.kondisi IS NULL OR dataInput.lokasi IS NULL
    RETURN FALSE
ELSE
    RETURN TRUE
END IF
```

Algo-020 `prosesLaporan(dataInput, fileFoto)`:
```
IF validasiData(dataInput) = TRUE THEN
    dataInput.fileFoto <- fileFoto
    result <- LaporanPohonController.prosesLaporan(dataInput)
    tampilkanStatus(result)
ELSE
    tampilkanStatus("Data laporan belum lengkap")
END IF
```

Algo-021 `tampilkanStatus(status)`:
```
DISPLAY status
```

### 8.5 Kelas LaporanPohon

Algo-022 `simpanLaporan(dataLaporan)`:
```
result <- DO QUERY Q-006
RETURN result
```

### 8.6 Kelas HalamanDataPohon

Algo-023 `ambilDataPohon()`:
```
dataPohonList <- DataPohonController.ambilDataPohon()
IF dataPohonList IS NOT EMPTY
    tampilkanData(dataPohonList)
ELSE
    tampilkanPesanError("Data pohon belum tersedia")
END IF
```

Algo-024 `tampilkanData(dataPohonList)`:
```
DISPLAY dataPohonList
```

Algo-025 `tampilkanDaftar()`:
```
dataPohonList <- DataPohonController.ambilDataPohon()
tampilkanData(dataPohonList)
```

Algo-026 `hapusData(idPohon)`:
```
result <- DataPohonController.hapusDataPohon(idPohon)
RETURN result
```

Algo-027 `tampilkanPesanError(pesan)`:
```
DISPLAY pesan
```

### 8.7 Kelas DataPohon

Algo-028 `cariDataPohon(kriteria)`:
```
IF kriteria IS NULL
    result <- DO QUERY Q-007
ELSE
    result <- DO QUERY Q-008
END IF
RETURN result
```

Algo-029 `getDataPohon()`:
```
result <- DO QUERY Q-007
RETURN result
```

Algo-030 `simpanData(dataPohon)`:
```
result <- DO QUERY Q-009
RETURN result
```

Algo-031 `ubahData(dataPohon)`:
```
result <- DO QUERY Q-010
RETURN result
```

Algo-032 `hapusData(idPohon)`:
```
result <- DO QUERY Q-011
RETURN result
```

Algo-033 `simpanDataPohon(data)`:
```
result <- simpanData(data)
RETURN result
```

Algo-034 `simpanFoto(file)`:
```
IF file IS NOT NULL
    filePath <- simpan file ke local folder
    RETURN filePath
ELSE
    RETURN "File foto tidak ditemukan"
END IF
```

### 8.8 Kelas HalamanStatistik

Algo-035 `ambilData()`:
```
dataStatistik <- StatistikController.ambilData()
IF dataStatistik IS NOT EMPTY
    tampilkanStatistik(dataStatistik)
    tampilkanGrafik(dataStatistik)
ELSE
    tampilkanPesanError("Belum ada data penghijauan")
END IF
```

Algo-036 `tampilkanStatistik(dataStatistik)`:
```
DISPLAY dataStatistik
```

Algo-037 `pilihPeriode(periode)`:
```
filterTerpilih <- periode
dataStatistik <- StatistikController.terapkanFilter(filterTerpilih)
perbaruiTampilan(dataStatistik)
```

Algo-038 `tampilkanGrafik(dataStatistik)`:
```
DISPLAY grafikStatistik
```

Algo-039 `perbaruiTampilan(dataStatistik)`:
```
tampilkanStatistik(dataStatistik)
tampilkanGrafik(dataStatistik)
```

Algo-040 `tampilkanPesanError(pesan)`:
```
DISPLAY pesan
```

### 8.9 Kelas FormLaporanPenanaman

Algo-041 `tampilkanForm()`:
```
DISPLAY form laporan penanaman
```

Algo-042 `kumpulkanDataInput(dataPenanaman)`:
```
RETURN dataPenanaman
```

Algo-043 `validasiData(dataPenanaman)`:
```
IF dataPenanaman.lokasi IS NULL OR dataPenanaman.jenisPohon IS NULL OR dataPenanaman.jumlahPohon <= 0
    RETURN FALSE
ELSE
    RETURN TRUE
END IF
```

Algo-044 `tampilkanStatus(status)`:
```
DISPLAY status
```

### 8.10 Kelas FormDataPohon

Algo-045 `tampilkanForm()`:
```
DISPLAY form data pohon
```

Algo-046 `validasiFile(file)`:
```
IF file IS NULL
    RETURN FALSE
ELSE
    RETURN TRUE
END IF
```

Algo-047 `validasiData(dataInput)`:
```
IF dataInput.namaPohon IS NULL OR dataInput.usia < 0 OR dataInput.serapanKarbon < 0
    RETURN FALSE
ELSE
    RETURN TRUE
END IF
```

Algo-048 `prosesInputPohon(data)`:
```
IF validasiData(data) = TRUE THEN
    result <- DataPohonController.prosesInputPohon(data)
    tampilkanStatus(result)
ELSE
    tampilkanStatus("Data pohon tidak valid")
END IF
```

Algo-049 `simpanDataPohon(data)`:
```
result <- DataPohonController.simpanDataPohon(data)
tampilkanStatus(result)
```

Algo-050 `tampilkanStatus(status)`:
```
DISPLAY status
```

### 8.11 Kelas PenanamanController (Algo-051 s/d Algo-057)
*Lihat Section 6.1*

### 8.12 Kelas LaporanPohonController (Algo-058 s/d Algo-060)
*Lihat Section 6.3*

### 8.13 Kelas DataPohonController (Algo-061 s/d Algo-070)
*Lihat Section 6.2*

### 8.14 Kelas StatistikController (Algo-071 s/d Algo-076)
*Lihat Section 6.4*

---

## 9. USE CASE MAPPING

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

## 10. SEQUENCE FLOWS

### UC01 — Normal
```
User.pilihMenuPenanaman()
→ HalamanDataPenanaman.ambilDataPenanaman()
→ PenanamanController.ambilDataPenanaman()
→ DataPenanaman.getDataPenanaman()
← dataPenanamanList
← dataPenanamanList
→ HalamanDataPenanaman.tampilkanData()
← User
```

### UC01 — Data Kosong
```
→ DataPenanaman.getDataPenanaman() → dataKosong
← dataKosong → PenanamanController → HalamanDataPenanaman
→ HalamanDataPenanaman.tampilkanPesanError()
```

### UC02 — Normal
```
User.isiLaporan() + User.unggahFoto()
→ FormLaporanPohon.validasiData() → TRUE
→ FormLaporanPohon.prosesLaporan(dataInput, fileFoto)
→ LaporanPohonController.prosesLaporan(dataLaporan)
→ LaporanPohonController.hitungEstimasiKarbon()
→ LaporanPohon.simpanLaporan()
← statusSimpan
→ FormLaporanPohon.tampilkanStatus()
```

### UC02 — Data Tidak Lengkap
```
→ FormLaporanPohon.validasiData() → FALSE
→ FormLaporanPohon.tampilkanStatus("Data laporan belum lengkap")
```

### UC03 — Normal
```
User.lihatDataPohon()
→ HalamanDataPohon.ambilDataPohon()
→ DataPohonController.ambilDataPohon()
→ DataPohon.getDataPohon()
← daftarPohon
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
```

### UC05 — Normal
```
User.pilihOpsiKelolaData()
→ FormLaporanPenanaman.tampilkanForm()
User.isiDataPenanaman()
→ FormLaporanPenanaman.validasiData() → TRUE
→ PenanamanController.simpanDataPenanaman(data)
→ DataPenanaman.simpanData() → status
→ FormLaporanPenanaman.tampilkanStatus("berhasil")
```

### UC06 — Normal
```
→ FormDataPohon.tampilkanForm()
User.unggahFoto()
User.isiDataPohon()
→ FormDataPohon.validasiData() → TRUE
→ FormDataPohon.prosesInputPohon(data)
→ DataPohonController.simpanDataPohon(data)
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

## 11. CLASS DIAGRAM DETAIL

### Boundary Layer
```
HalamanStatistik (C-08)
  - grafikStatistik: Object
  - filterTerpilih: String
  + ambilData(): void
  + tampilkanStatistik(data: Object): void
  + pilihPeriode(periode: String): void
  + tampilkanGrafik(data: Object): void
  + perbaruiTampilan(data: Object): void
  + tampilkanPesanError(pesan: String): void

HalamanDataPohon (C-06)
  - daftarPohon: List
  - filterData: String
  + ambilDataPohon(): void
  + tampilkanData(dataPohonList: List): void
  + tampilkanDaftar(): void
  + hapusData(idPohon: String): void
  + tampilkanPesanError(pesan: String): void

HalamanDataPenanaman (C-02)
  + ambilDataPenanaman(): void
  + tampilkanData(data: List): void
  + tampilkanPesanError(pesan: String): void

FormLaporanPohon (C-04)
  - fileFoto: String
  - dataInput: Object
  + validasiData(dataInput: Object): boolean
  + prosesLaporan(dataInput: Object, fileFoto: File): void
  + tampilkanStatus(status: String): void

FormDataPohon (C-10)
  - dataInput: Object
  + tampilkanForm(): void
  + validasiFile(file: File): boolean
  + validasiData(dataInput: Object): boolean
  + prosesInputPohon(data: Object): void
  + simpanDataPohon(data: Object): void
  + tampilkanStatus(status: String): void

FormLaporanPenanaman (C-09)
  + tampilkanForm(): void
  + kumpulkanDataInput(data: DataPenanaman): void
  + validasiData(data: DataPenanaman): boolean
  + tampilkanStatus(status: String): void
```

### Controller Layer
```
PenanamanController (C-11)
  + cariData(idPenanaman: String): void
  + ambilDataPenanaman(): void
  + simpanDataPenanaman(data: DataPenanaman): void
  + ubahDataPenanaman(data: DataPenanaman): void
  + hapusDataPenanaman(id: String): void
  + hitungEstimasiKarbon(data: DataPenanaman): float
  + teruskanKeView(result: Object): void

DataPohonController (C-13)
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

LaporanPohonController (C-12)
  + prosesLaporan(dataLaporan: LaporanPohon): void
  + hitungEstimasiKarbon(data: LaporanPohon): float
  + simpanLaporan(dataLaporan: LaporanPohon): void

StatistikController (C-14)
  + hitungStatistik(dp: DataPohon, dpt: DataPenanaman): void
  + getDataPohon(): void
  + getDataPenanaman(): void
  + ambilData(): void
  + terapkanFilter(filterPeriode: String): void
  + teruskanKeView(statistik: Object): void
```

### Entity Layer
```
User (C-01)
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

DataPohon (C-07)
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

DataPenanaman (C-03)
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

LaporanPohon (C-05)
  - idLaporan: String
  - kondisi: String
  - lokasi: String
  - fileFoto: String
  - estimasiKarbon: float
  + simpanLaporan(data: LaporanPohon): void
```

---

## 12. AUTHENTICATION / LOGIN

EcoTrack menyediakan halaman login ringan untuk memilih akun pengguna dari tabel `user`.

- Tipe autentikasi: pilih identitas (no password) — cocok untuk deployment internal atau demo. Untuk autentikasi berbasis password, tambahkan kolom `password` pada tabel `user` dan perbarui controller/UI.
- Boundary: `LoginPage` — modal JavaFX yang menampilkan daftar akun (nama + role) dan tombol "Masuk". Tema visual mengikuti `UIConstants` (`CONTENT_BG` untuk latar, `ACCENT_LIME` untuk tombol). Ini menjaga konsistensi warna dengan halaman lain.
- Controller: `UserController` menyediakan `findAll()` dan `findById()` untuk membaca akun dari DB.
- Util: `Session` menyimpan `currentUser` setelah login agar boundary/controller lain mengakses identitas saat runtime.
- DB seed: `db/init.sql` menyertakan akun `user-001` (Admin EcoTrack) dan `user-002` (Petugas Default).

Flow:
1. Aplikasi memanggil `LoginPage.showLogin(primaryStage)` pada startup.
2. Pengguna memilih akun lalu menekan `Masuk`.
3. `Session.setCurrentUser(user)` dipanggil; jika batal, aplikasi keluar.
4. UI utama dimuat setelah login berhasil.

Keamanan dan pengembangan lanjut:
- Untuk produksi dengan banyak pengguna, tambahkan password dan hashing, atau integrasi SSO/LDAP sesuai kebijakan organisasi.


## 12. UTIL CLASSES

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

## 13. TRACEABILITY MATRIX (DPPLOO-01)

| No | Kelas Perancangan | Kelas Analisis | Use Case |
|---|---|---|---|
| 1 | User | C-01 User | UC01–UC08 |
| 2 | HalamanDataPenanaman | C-02 HalamanDataPenanaman | UC01, UC05 |
| 3 | DataPenanaman | C-03 DataPenanaman | UC01, UC04, UC05, UC08 |
| 4 | FormLaporanPohon | C-04 FormLaporanPohon | UC02 |
| 5 | LaporanPohon | C-05 LaporanPohon | UC02 |
| 6 | HalamanDataPohon | C-06 HalamanDataPohon | UC03, UC07 |
| 7 | DataPohon | C-07 DataPohon | UC03, UC04, UC06, UC07, UC08 |
| 8 | HalamanStatistik | C-08 HalamanStatistik | UC04, UC08 |
| 9 | FormLaporanPenanaman | C-09 FormLaporanPenanaman | UC05 |
| 10 | FormDataPohon | C-10 FormDataPohon | UC06, UC07 |
| 11 | PenanamanController | C-11 PenanamanController | UC01, UC05 |
| 12 | LaporanPohonController | C-12 LaporanPohonController | UC02 |
| 13 | DataPohonController | C-13 DataPohonController | UC03, UC06, UC07 |
| 14 | StatistikController | C-14 StatistikController | UC04, UC08 |

---

## 14. NOTES & CONSTRAINTS

1. **Aplikasi desktop single-user** — tidak ada authentication kompleks.
2. **Foto pohon** disimpan di local folder, DB hanya menyimpan path.
3. **Estimasi karbon dihitung otomatis** saat menyimpan penanaman baru maupun laporan.
4. **Filter statistik** berdasarkan periode waktu atau tanpa filter.
5. **JavaFX Charts** untuk Line Chart dan Bar Chart di HalamanStatistik.
6. Semua query menggunakan **PreparedStatement**.
7. `id_*` fields menggunakan **UUID**.
8. **Controller langsung panggil Entity** — DPPLOO-01 TIDAK memiliki Repository layer. Arsitektur murni BCE.
9. **`DataPohon.cariDataPohon(jenisPohon)`** cari by nama pohon (bukan ID).
10. **Q-008**: `WHERE id_pohon = ? OR nama_pohon ILIKE ?` — satu query untuk dua kebutuhan.
11. **`FormLaporanPenanaman`** boundary terpisah untuk input penanaman. **`HalamanDataPenanaman`** hanya display list.
12. **`prosesLaporan()`** di `FormLaporanPohon` set `fileFoto` ke `dataInput.fileFoto` SEBELUM kirim ke controller.
13. **`hitungEstimasiKarbon()`** berbeda:
    - LaporanPohonController: ditebang=0, rusak=*0.5, else=sama
    - PenanamanController: jumlahPohon * serapanKarbon
14. **Status/kondisi laporan**: `"ditebang"`, `"rusak"`, `"mati"` (lowercase).
15. **UI/estetika/warna TIDAK DIUBAH** — color palette, spacing, layout tetap sama.
16. **Tidak ada Repository layer** — Entity methods langsung menjalankan query (Q-001 s/d Q-013).
17. **Bahasa di DPPLOO-01 section 2.1 tertulis "JavaScript"** — ini typo di dokumen asli, actual implementation adalah **Java + JavaFX**.
