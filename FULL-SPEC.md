# FULL-SPEC.md — EcoTrack
> **Dokumen DPPL asli: 51 halaman**  
> Halaman efektif (konten teknis): ~35 halaman (sisanya cover, daftar isi, daftar gambar, footer boilerplate)

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
  Step 5. Buat PohonRepository.java     (Q-005 s/d Q-010)
  Step 6. Buat PenanamanRepository.java (Q-001 s/d Q-004)
  Step 7. Buat LaporanRepository.java   (Q-011, Q-012)
  Step 8. Buat FileManager.java         (simpan/ambil foto lokal)

PHASE 3 — BUSINESS LOGIC
  Step 9.  Buat PohonController.java          (CRUD pohon + validasi + foto)
  Step 10. Buat PenanamanController.java      (CRUD penanaman + hitungEstimasi)
  Step 11. Buat LaporanPohonController.java   (prosesLaporan + hitungKapasitasBaru)
  Step 12. Buat StatistikController.java      (hitungStatistikTotal + ambilDataVisualisasi)

PHASE 4 — UI (JavaFX)
  Step 13. Buat HalamanDataPohon.fxml + HalamanDataPohon.java       (UC03, UC07)
  Step 14. Buat HalamanDataPenanaman.fxml + HalamanDataPenanaman.java (UC01, UC05)
  Step 15. Buat FormLaporanPohon.fxml + FormLaporanPohon.java        (UC02)
  Step 16. Buat HalamanStatistik.fxml + HalamanStatistik.java        (UC04, UC08)
  Step 17. Buat Main.java + navigasi sidebar (User.pilihMenu)
```

### Docker PostgreSQL Setup (biar semua tim pakai versi yang sama)

**Struktur file yang perlu dibuat di root project:**
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

#### `db/init.sql` — auto-run saat container pertama kali start
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
    kapasitas_serapan_karbon FLOAT,
    status                   VARCHAR(50),
    file_foto_path           VARCHAR(500),
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
    tanggal_penanaman DATE         NOT NULL,
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
    file_foto_path  VARCHAR(500),
    estimasi_karbon FLOAT,
    tanggal_laporan DATE         NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Seed user awal (opsional)
INSERT INTO "user" (id_user, nama, role)
VALUES ('user-001', 'Admin EcoTrack', 'admin')
ON CONFLICT DO NOTHING;
```

#### `.env` — simpan credentials (JANGAN di-commit ke Git)
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

#### Cara pakai (jalankan sekali, semua teman tinggal clone + docker compose up):
```bash
# Start DB (background)
docker compose up -d

# Cek apakah container jalan
docker compose ps

# Lihat log kalau ada error
docker compose logs ecotrack-db

# Stop DB
docker compose down

# Reset total (hapus semua data)
docker compose down -v
```

> **Catatan untuk tim:** `init.sql` hanya dieksekusi sekali saat volume belum ada.  
> Kalau mau reset schema, jalankan `docker compose down -v` dulu baru `docker compose up -d`.

---

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

#### `pom.xml` — dependency wajib (Maven)
```xml
<dependencies>
    <!-- PostgreSQL JDBC Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version>
    </dependency>

    <!-- JavaFX Controls -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.3</version>
    </dependency>

    <!-- JavaFX FXML -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.3</version>
    </dependency>
</dependencies>
```

---

### Kenapa urutan ini?
- Entity tidak bergantung apa-apa → paling aman dibuat duluan
- Repository butuh Entity + DBConnection → harus setelah Phase 1
- Controller butuh Repository → harus setelah Phase 2
- UI butuh Controller → harus paling akhir
- **Jangan mulai dari UI dulu** — bikin debugging jadi jauh lebih susah

### Page asli DPPL yang paling relevan per phase:
| Phase | Halaman DPPL |
|---|---|
| Phase 1 (Fondasi) | Hal. 9–10 (arsitektur), Hal. 50 (schema DB) |
| Phase 2 (Repository) | Hal. 35–41 (Query Q-001 s/d Q-012) |
| Phase 3 (Controller) | Hal. 33–43 (Algoritma + Query) |
| Phase 4 (UI) | Hal. 44–49 (Perancangan Antarmuka) |

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
| File Storage | Local Folder (foto pohon, ekspor laporan) |
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
        ├─► [Perhitungan Serapan Karbon]  ◄─ dipicu oleh Penanaman & Kondisi Pohon
        ├─► [Statistik dan Rekapitulasi]
        └─► [Laporan Berkala]
              ├─► [PostgreSQL]           ← data terstruktur
              └─► [Local File System]    ← foto pohon, ekspor laporan
```

### Package Structure (Java)
```
com.ecotrack/
├── Main.java
├── boundary/
│   ├── HalamanStatistik.java
│   ├── HalamanDataPohon.java
│   ├── HalamanDataPenanaman.java
│   ├── FormLaporanPohon.java
│   └── FormDataPohon.java
├── controller/
│   ├── StatistikController.java
│   ├── PohonController.java
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

## 3. DATABASE SCHEMA (PostgreSQL)

### Tabel: `user`
```sql
CREATE TABLE user (
    id_user   VARCHAR PRIMARY KEY,
    nama      VARCHAR NOT NULL,
    role      VARCHAR NOT NULL
);
```

### Tabel: `data_pohon`
```sql
CREATE TABLE data_pohon (
    id_pohon              VARCHAR PRIMARY KEY,
    id_user               VARCHAR REFERENCES user(id_user),
    nama_pohon            VARCHAR NOT NULL,
    usia                  INT,
    lokasi                VARCHAR,
    kapasitas_serapan_karbon FLOAT,
    status                VARCHAR,
    file_foto_path        VARCHAR,
    created_at            TIMESTAMP DEFAULT NOW(),
    updated_at            TIMESTAMP DEFAULT NOW()
);
```

### Tabel: `data_penanaman`
```sql
CREATE TABLE data_penanaman (
    id_penanaman      VARCHAR PRIMARY KEY,
    id_user           VARCHAR REFERENCES user(id_user),
    id_pohon          VARCHAR REFERENCES data_pohon(id_pohon),
    lokasi            VARCHAR NOT NULL,
    jenis_pohon       VARCHAR NOT NULL,
    jumlah_pohon      INT NOT NULL,
    tanggal_penanaman DATE NOT NULL,
    estimasi_karbon   FLOAT,
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);
```

### Tabel: `laporan_pohon`
```sql
CREATE TABLE laporan_pohon (
    id_laporan      VARCHAR PRIMARY KEY,
    id_user         VARCHAR REFERENCES user(id_user),
    id_pohon        VARCHAR REFERENCES data_pohon(id_pohon),
    kondisi         VARCHAR NOT NULL,   -- 'rusak' | 'mati' | 'ditebang'
    lokasi          VARCHAR NOT NULL,
    file_foto_path  VARCHAR,
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

    public void pilihMenu(String menuTujuan); // navigasi ke halaman tujuan
}
```

### 4.2 DataPohon (C-07)
```java
class DataPohon {
    private String idPohon;
    private String namaPohon;
    private float  serapanKarbon;  // kg/tahun
    private String status;
    private String fileFotoPath;
    // getters/setters
}
```

### 4.3 DataPenanaman (C-03)
```java
class DataPenanaman {
    private String idPenanaman;
    private String lokasi;
    private String jenisPohon;
    private int    jumlahPohon;
    private String tanggalPenanaman;
    private float  estimasiKarbon;
    // getters/setters
}
```

### 4.4 LaporanPohon (C-05)
```java
class LaporanPohon {
    private String idLaporan;
    private String kondisi;
    private String lokasi;
    private String fileFotoPath;
    private float  estimasiKarbon;
    private String tanggalLaporan;
    // getters/setters
}
```

---

## 5. BOUNDARY CLASSES — UI SPEC DETAIL (PIXEL-PRECISE)

---

### GLOBAL — DESIGN SYSTEM & COLOR PALETTE

```
WARNA UTAMA:
  Sidebar background     : #1B3A2D  (dark green teal)
  Sidebar active item bg : #A8E063  (lime green, text hitam)
  Sidebar inactive text  : #FFFFFF  (putih, opacity ~70%)
  Sidebar logo/icon      : putih

  Content area background: #F0FAF5  (off-white mint, sangat terang)
  Card background putih  : #FFFFFF
  Card background hijau  : linear-gradient(135deg, #4CAF7D → #2E7D52)  (Total Pohon)
  Card background teal   : linear-gradient(135deg, #2D6E6E → #1B4A4A)  (Serapan Karbon)

  Aksen hijau lime (button primer) : #A8E063  (teks hitam di atasnya)
  Aksen hijau teal (chart, badge)  : #26A69A  atau #00897B
  Aksen merah (delete icon)        : #EF5350
  Aksen teal (edit icon)           : #26A69A
  Teks judul halaman               : #1B3A2D  (dark green)
  Teks subtitle halaman            : #6B9080  (muted green-grey)
  Teks tabel header                : #9E9E9E  (abu-abu, huruf kapital kecil)
  Teks data tabel                  : #212121

FONT:
  Semua teks menggunakan sans-serif (Inter / Poppins)
  Judul halaman    : Bold, ~22px
  Subtitle         : Regular, ~12px, muted
  Angka card besar : Bold, ~36px
  Label card kecil : Regular, ~12px
  Tabel header     : SemiBold, ~11px, uppercase, letter-spacing
  Tabel data       : Regular, ~13px

RADIUS & SPACING:
  Card border-radius : 16px
  Modal border-radius: 12px
  Button border-radius: 8px
  Input border-radius : 8px
  Sidebar lebar      : ~220px
  Gap antar card     : 16px
  Padding content    : 24px

SIDEBAR MENU ITEMS (urutan dari atas ke bawah):
  1. 📊 Dashboard Statistik  → HalamanStatistik
  2. 🌳 Data Pohon           → HalamanDataPohon
  3. 🌿 Data Penanaman       → HalamanDataPenanaman
  4. 📋 Lapor Kondisi Pohon  → FormLaporanPohon
  Active item: background #A8E063, teks hitam, full-width rounded pill
  Inactive item: teks putih semi-transparan
```

---

### 5.1 HalamanStatistik (C-08) — LAYAR 1: Dashboard Statistik

#### Layout Struktur
```
┌─────────────────────────────────────────────────────────────────┐
│ SIDEBAR (220px)  │  CONTENT AREA (sisa width)                   │
│                  │  ┌─────────────────────────────────────────┐ │
│ [Logo EcoTrack]  │  │ Header: "Dashboard Statistik"           │ │
│                  │  │ Sub: "Monitoring serapan karbon..."     │ │
│ [Dashboard ●]    │  │                          [📅 Bulanan ▼] │ │
│ [Data Pohon]     │  ├─────────────────────────────────────────┤ │
│ [Data Penanaman] │  │ [Card Hijau: Total Pohon]               │ │
│ [Lapor Kondisi]  │  │ [Card Teal: Serapan Karbon]             │ │
│                  │  │ [Card Putih: Rata-rata Serapan]         │ │
│                  │  ├─────────────────────────────────────────┤ │
│                  │  │ [Line Chart: Jumlah Pohon]              │ │
│                  │  │ [Bar Chart: Serapan Karbon]             │ │
│                  │  └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

#### Header
- Judul: `"Dashboard Statistik"` — Bold, dark green
- Subtitle: `"Monitoring serapan karbon dan penghijauan kota"` — muted, kecil
- Dropdown filter `"📅 Bulanan ▼"` — pojok kanan atas content, border tipis, rounded

#### 3 Summary Cards (sejajar horizontal, lebar sama rata)

**Card 1 — Total Pohon** (background gradient hijau)
```
Label : "Total Pohon"
Angka : "906"  → bold, ~36px, putih
Sub   : "6 bulan terakhir"  → putih, kecil
Badge : "↑ +12.5% dari periode lalu"  → lime green pill, teks hitam kecil
Visual: ilustrasi pohon/landscape di pojok kanan card (dekoratif)
```

**Card 2 — Serapan Karbon** (background gradient dark teal)
```
Label : "Serapan Karbon"
Angka : "2,491"  → bold, ~36px, putih
Sub   : "kg CO₂ per tahun"  → putih, kecil
Badge : "↑ +8.2% dari periode lalu"  → badge putih/lime
Visual: ilustrasi awan & pohon di pojok kanan card (dekoratif/SVG)
```

**Card 3 — Rata-rata Serapan** (background putih)
```
Label : "Rata-rata Serapan"
Angka : "415"  → bold, ~36px, dark green
Sub   : "kg CO₂ / periode"  → muted, kecil
Badge : "→ Konsisten meningkat"  → teks muted, tanpa background badge
```

#### 2 Charts (sejajar horizontal, masing-masing ~50% width)

**Chart Kiri — Jumlah Pohon (Line Chart)**
```
Judul    : "Jumlah Pohon"
Subtitle : "Pertumbuhan pohon yang ditanam"
X-axis   : Jan, Feb, Mar, Apr, Mei, Jun
Y-axis   : 0, 60, 120, 180, 240
Line     : warna hijau (#4CAF7D), dengan filled area di bawah line
           (area fill: hijau transparan, opacity ~20%)
Points   : lingkaran kecil di tiap data point, outline hijau
Background chart: putih, rounded card
Grid lines: horizontal, abu-abu sangat tipis
```

**Chart Kanan — Serapan Karbon (Bar Chart)**
```
Judul    : "Serapan Karbon"
Subtitle : "Estimasi penyerapan CO₂ (kg/tahun)"
X-axis   : Jan, Feb, Mar, Apr, Mei, Jun
Y-axis   : 0, 200, 400, 600, 800
Bars     : warna teal (#26A69A), lebar sedang, rounded top
           Bar Jun paling tinggi (~750), Jan paling rendah (~150)
Background chart: putih, rounded card
Grid lines: horizontal, abu-abu tipis
```

#### JavaFX Implementation Notes
```
- Gunakan javafx.scene.chart.LineChart untuk chart kiri
- Gunakan javafx.scene.chart.BarChart untuk chart kanan
- Data di-feed dari StatistikController.ambilDataVisualisasi()
- Dropdown filter "Bulanan" → onChange trigger hitungStatistikTotal(periode)
- Cards angka update otomatis saat filter berubah
```

**Methods:**
```java
public void tampilkanGrafik(String filterPeriode);
public void pilihFilterPeriode();
public void updateCards(Map<String, Object> statistik);
```

---

### 5.2 HalamanDataPohon (C-06) — LAYAR 2: Data Pohon

#### Layout Struktur
```
┌─────────────────────────────────────────────────────────────────┐
│ SIDEBAR          │  CONTENT AREA                                 │
│                  │  ┌─────────────────────────────────────────┐ │
│ [Dashboard]      │  │ "Data Pohon"              [+Tambah Pohon]│ │
│ [Data Pohon ●]   │  │ "Kelola master data referensi jenis pohon"│ │
│ [Data Penanaman] │  │ 🔔 Total Jenis Pohon: 5                  │ │
│ [Lapor Kondisi]  │  ├─────────────────────────────────────────┤ │
│                  │  │ [Tabel Daftar Pohon]                     │ │
│                  │  │  NAMA POHON | USIA | SERAPAN | AKSI      │ │
│                  │  │  row...                                   │ │
│                  │  └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

#### Header Area
- Judul: `"Data Pohon"` — Bold, dark green
- Subtitle: `"Kelola master data referensi jenis pohon"` — muted kecil
- Badge info: `🔔 Total Jenis Pohon  [5]` — icon bell, teks muted, angka bold
- Button `"+ Tambah Pohon"` — pojok kanan atas, background #A8E063 (lime green), teks hitam bold, rounded 8px

#### Tabel Daftar Pohon
```
Background tabel: #FFFFFF, rounded 16px, shadow tipis

HEADER ROW (semua uppercase, abu-abu, font kecil):
  NAMA POHON | USIA (TAHUN) | SERAPAN KARBON (KG/TAHUN) | AKSI

DATA ROW (per pohon):
  Col 1 - Nama Pohon:
    → Icon lingkaran kecil (filled #A8E063 / lime) di kiri nama
    → Teks nama pohon: "Mahoni", "Jati", dll.
  Col 2 - Usia:
    → Badge pill: background #E8F5E9 (hijau muda), teks "#2E7D52"
    → Contoh: "5 tahun", "10 tahun", "8 tahun"
  Col 3 - Serapan Karbon:
    → Teks biasa + satuan subscript/kecil "kg"
    → Contoh: "28.5kg", "45.2kg", "62.8kg"
  Col 4 - Aksi:
    → Icon ✏️ (edit): warna teal (#26A69A), clickable
    → Icon 🗑️ (delete): warna merah (#EF5350), clickable
    → Keduanya tanpa background, spacing rapat

DATA ROWS (dari gambar persis):
  Row 1: Mahoni    | 5 tahun  | 28.5kg  | edit | delete
  Row 2: Jati      | 10 tahun | 45.2kg  | edit | delete
  Row 3: Trembesi  | 8 tahun  | 62.8kg  | edit | delete
  Row 4: Akasia    | 3 tahun  | 18.4kg  | edit | delete
  Row 5: Mangrove  | 6 tahun  | 35.7kg  | edit | delete

Row separator: border bottom tipis #F0F0F0
Row hover: background #F5FFF5 (hijau sangat muda)
```

#### Modal — Tambah Data Pohon (Image 3)
```
OVERLAY: background hitam transparan (opacity 50%) menutupi seluruh layar

MODAL CARD:
  Background : #FFFFFF
  Width      : ~400px
  Padding    : 24px
  Radius     : 12px
  Shadow     : medium drop shadow

HEADER MODAL:
  Judul   : "Tambah Data Pohon"  — Bold, dark green, ~16px
  Sub     : "Masukkan informasi pohon baru ke dalam database" — muted, ~12px
  [X] button : pojok kanan atas, teks abu-abu, no background

FORM FIELDS (3 fields, label di atas input):

  Field 1 — Nama Pohon
    Label      : "Nama Pohon"  — semibold, ~13px
    Input      : TextField, placeholder "Contoh: Mahoni"
    Border     : 1px solid #E0E0E0, radius 8px
    Focus      : border #A8E063 (lime green)

  Field 2 — Usia Pohon (tahun)
    Label      : "Usia Pohon (tahun)"
    Input      : TextField, placeholder "Contoh: 5"
    (sama styling dengan Field 1)

  Field 3 — Kapasitas Serapan Karbon (kg/tahun)
    Label      : "Kapasitas Serapan Karbon (kg/tahun)"
    Input      : TextField, placeholder "Contoh: 28.5"
    (sama styling)

FOOTER MODAL (2 button, aligned right):
  [Batal]         — outline button, border abu-abu, teks abu-abu, no fill
  [Tambah Pohon]  — filled #A8E063, teks hitam bold, radius 8px
```

**Methods:**
```java
public void tampilkanDaftarPohon();
public void tampilkanDetailPohon(String idPohon);
public void tampilkanModalTambah();
public void tutupModal();
```

---

### 5.3 HalamanDataPenanaman (C-02) — LAYAR 3: Data Penanaman

#### Layout Struktur
```
┌─────────────────────────────────────────────────────────────────┐
│ SIDEBAR          │  CONTENT AREA                                 │
│                  │  ┌─────────────────────────────────────────┐ │
│ [Dashboard]      │  │ "Data Penanaman"    [+ Catat Penanaman] │ │
│ [Data Pohon]     │  │ "Catat dan kelola riwayat penanaman..." │ │
│ [Data Penanaman●]│  │ [Card Hijau: Total Pohon Ditanam]       │ │
│ [Lapor Kondisi]  │  │ [Card Teal: Estimasi Total Serapan]     │ │
│                  │  ├─────────────────────────────────────────┤ │
│                  │  │ "Riwayat Penanaman"                     │ │
│                  │  │ [Tabel Riwayat]                         │ │
│                  │  └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

#### Header Area
- Judul: `"Data Penanaman"` — Bold, dark green
- Subtitle: `"Catat dan kelola riwayat penanaman pohon"` — muted
- Button `"+ Catat Penanaman Baru"` — pojok kanan atas, lime green, teks hitam bold

#### 2 Summary Cards (sejajar horizontal)

**Card Kiri — Total Pohon Ditanam** (background gradient hijau)
```
Label  : "Total Pohon Ditanam"
Angka  : "155"  → bold, ~36px, putih
Sub    : "Akumulasi seluruh pencatatan"  → putih kecil
Icon   : 🌿 (daun) di pojok kanan card, putih/transparan
```

**Card Kanan — Estimasi Total Serapan** (background dark teal/navy teal)
```
Label  : "Estimasi Total Serapan"
Angka  : "6,699"  → bold, ~36px, putih
Sub    : "kg CO₂/tahun kapasitas tahunan"  → putih kecil
Icon   : ⇄ (panah) di pojok kanan card
```

#### Tabel Riwayat Penanaman
```
Section title: "Riwayat Penanaman"  — semibold, dark green, ~14px

HEADER ROW:
  TANGGAL | LOKASI | JENIS POHON | JUMLAH | EST. SERAPAN (KG/THN)

DATA ROWS (dari gambar persis):

  Row 1:
    Tanggal     : "15 Jan 2026"  → teks teal/hijau (#26A69A), bukan hitam
    Lokasi      : 📍 "Taman Kota A"  → icon pin teal kecil di kiri
    Jenis Pohon : badge pill "Mahoni"  → background hijau muda, teks hijau gelap
    Jumlah      : "50pohon"  → angka bold, "pohon" subscript/kecil muted
    Est. Serapan: "1,425kg"  → angka bold, "kg" subscript kecil

  Row 2:
    Tanggal     : "20 Feb 2026"  → teal
    Lokasi      : 📍 "Jalan Raya B"
    Jenis Pohon : badge "Trembesi"  → warna badge bisa berbeda (lime/kuning muda)
    Jumlah      : "30pohon"
    Est. Serapan: "1,884kg"

  Row 3:
    Tanggal     : "10 Mar 2026"  → teal
    Lokasi      : 📍 "Hutan Kota C"
    Jenis Pohon : badge "Jati"  → warna badge beda lagi (kuning muda)
    Jumlah      : "75pohon"
    Est. Serapan: "3,390kg"

Badge jenis pohon: tiap jenis pohon punya warna badge unik (beda-beda), semua
pastel/muted. Implementasi: buat map namaPohon → warna background badge.
```

#### Modal — Catat Penanaman Baru (Image 4 bawah)
```
OVERLAY: hitam semi-transparan

MODAL CARD:
  Width  : ~420px
  Padding: 24px
  Radius : 12px

HEADER MODAL:
  Judul : "Catat Penanaman Baru"  — Bold, dark green
  Sub   : "Masukkan informasi penanaman pohon untuk perhitungan serapan karbon otomatis"
          — muted, ~12px, 2 baris
  [X]   : pojok kanan atas

FORM FIELDS (4 fields):

  Field 1 — Lokasi Penanaman
    Label      : "Lokasi Penanaman"
    Input      : TextField, placeholder "Contoh: Taman Kota A"

  Field 2 — Jenis Pohon
    Label      : "Jenis Pohon"
    Input      : ComboBox / DropdownButton
    Placeholder: "Pilih jenis pohon"
    Arrow      : ▼ di sisi kanan
    Options    : list nama pohon dari data_pohon (dynamic dari DB)

  Field 3 — Jumlah Pohon
    Label      : "Jumlah Pohon"
    Input      : TextField (numeric), placeholder "Contoh: 50"

  Field 4 — Tanggal Penanaman
    Label      : "Tanggal Penanaman"
    Input      : DatePicker JavaFX

FOOTER MODAL:
  [Batal]               — outline, teks abu-abu
  [Simpan Data Penanaman] — filled #A8E063, teks hitam bold
```

**Methods:**
```java
public void tampilkanDaftarPenanaman();
public void tampilkanFormInput();
public void tutupModal();
public void populateDropdownJenisPohon();  // load dari PohonRepository
```

---

### 5.4 FormLaporanPohon (C-04) — LAYAR 4: Lapor Kondisi Pohon

#### Layout Struktur — SPLIT LAYOUT
```
┌─────────────────────────────────────────────────────────────────┐
│ SIDEBAR          │  CONTENT AREA                                 │
│                  │  ┌──────────────────┬──────────────────────┐ │
│ [Dashboard]      │  │  PANEL KIRI      │  PANEL KANAN         │ │
│ [Data Pohon]     │  │  (~65% width)    │  (~35% width)        │ │
│ [Data Penanaman] │  │  Form Laporan    │  Riwayat Laporan     │ │
│ [Lapor Kondisi●] │  │  Baru            │                      │ │
│                  │  └──────────────────┴──────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

#### Header Area (di atas split)
- Judul: `"Lapor Kondisi Pohon"` — Bold, dark green
- Subtitle: `"Catat dan pantau kondisi pohon dengan bukti visual"` — muted

#### Panel Kiri — Form Laporan Baru
```
Section title: "Form Laporan Baru"  — semibold, dark, ~14px
Sub title    : "Masukkan detail kondisi pohon yang ditemukan"  — muted, ~12px

Background   : #FFFFFF, rounded 16px, shadow tipis
Padding      : 20px

FORM FIELDS (4 fields + upload area):

  Field 1 — Lokasi
    Label      : "Lokasi"
    Input      : TextField, placeholder "Contoh: Taman Kota A, Blok B"
    Width      : full width

  Field 2 — Status Kondisi
    Label      : "Status Kondisi"
    Input      : ComboBox / Dropdown
    Placeholder: "Pilih status kondisi"
    Arrow      : ▼
    Options    : ["Rusak", "Mati", "Ditebang"]

  Field 3 — Catatan Tambahan
    Label      : "Catatan Tambahan"
    Input      : TextArea (multiline, ~4 baris tinggi)
    Placeholder: "Deskripsi kondisi atau keterangan tambahan..."

  Field 4 — Foto Bukti (Upload Area)
    Label      : "Foto Bukti"
    Area       : Dashed border (#A8E063 atau abu-abu), rounded 12px
                 Tinggi ~130px, lebar full
    Isi area   :
      Icon     : ⬆ (upload arrow, abu-abu, ~32px)
      Text 1   : "Drag & drop foto di sini"  — bold, abu-abu gelap
      Text 2   : "atau"  — muted kecil
      Button   : [Browse File]  — outline button kecil, border abu-abu
      Text 3   : "Mendukung format PNG, JPG, atau JPEG"  — muted sangat kecil
    onDragOver : highlight border jadi lime green
    onDrop/onClick → FileChooser → simpan path → preview nama file

BUTTON SUBMIT (full width, di bawah form):
  [         Simpan Laporan         ]
  Background : #A8E063 (lime green)
  Teks       : "Simpan Laporan"  — bold, hitam, center
  Height     : ~48px
  Radius     : 8px
  onClick    → kirimDataLaporan() → LaporanPohonController.prosesLaporan()
```

#### Panel Kanan — Riwayat Laporan
```
Section title: "Riwayat Laporan"  — semibold, ~14px
Background   : #FFFFFF, rounded 16px, shadow tipis
Padding      : 20px

STATE KOSONG (seperti di gambar):
  Icon       : ilustrasi dokumen/file (SVG, abu-abu muda, ~64px)
  Text 1     : "Belum ada laporan"  — semibold, abu-abu gelap
  Text 2     : "Mulai tambahkan laporan kondisi pohon menggunakan form"
               — muted kecil, center, max 2 baris

STATE ADA DATA (saat laporan sudah ada):
  List cards per laporan:
    - Tanggal laporan
    - Lokasi
    - Status kondisi (badge pill warna sesuai: merah=mati, oranye=rusak, abu=ditebang)
    - Estimasi karbon
```

**Methods:**
```java
public void tampilkanFormLaporan();
public void kirimDataLaporan(LaporanPohon dataLaporan);
public void tampilkanRiwayatLaporan();
public void handleFileUpload();       // FileChooser + drag-drop handler
public void handleDragOver(DragEvent e);
public void handleDrop(DragEvent e);
```

---

### 5.5 FormDataPohon (C-10) — Embedded dalam HalamanDataPohon
Tidak punya halaman tersendiri — muncul sebagai modal di atas HalamanDataPohon.  
Spec modal sudah tercakup di 5.2 (Modal Tambah Data Pohon).

**Methods:**
```java
public void tampilkanForm();
public void unggahFoto();
public void isiDataPohon();
```

---

### GLOBAL UI BEHAVIOR NOTES
```
1. SIDEBAR NAVIGATION
   - Active menu: background pill #A8E063, teks hitam
   - Klik item → load halaman baru (ganti center pane JavaFX)
   - Logo "EcoTrack" + icon daun di pojok atas sidebar

2. EMPTY STATE
   - Semua tabel/panel kosong wajib tampilkan ilustrasi + teks informatif
   - Jangan tampilkan tabel kosong tanpa pesan

3. LOADING STATE
   - Saat fetch data dari DB: tampilkan ProgressIndicator di tengah panel

4. ERROR STATE
   - Validasi gagal: border input jadi merah, tampilkan label error di bawah field
   - DB error: tampilkan Alert dialog JavaFX

5. MODAL BEHAVIOR
   - Klik overlay (area luar modal) → tutup modal
   - Tombol [Batal] → tutup modal tanpa save
   - Tombol [X] → tutup modal

6. BADGE WARNA JENIS POHON (map statis, bisa dikembangkan):
   Mahoni   → background #E8F5E9, teks #2E7D52
   Jati     → background #FFF9C4, teks #F9A825
   Trembesi → background #E0F2F1, teks #00796B
   Akasia   → background #FFF3E0, teks #E65100
   Mangrove → background #E3F2FD, teks #1565C0
   Default  → background #F3E5F5, teks #6A1B9A

7. TABEL ROW HOVER
   Semua tabel: hover row → background #F0FFF4

8. BUTTON STATES
   Primer (lime): normal #A8E063, hover #95D14F, disabled opacity 50%
   Outline: normal border #E0E0E0, hover border #A8E063
```

---

## 6. CONTROLLER CLASSES

### 6.1 PenanamanController (C-11)
```java
class PenanamanController {

    // Ambil semua data penanaman dari repository
    public List<DataPenanaman> getDataPenanaman();

    // Validasi input form penanaman (format & kelengkapan)
    public boolean validasiInput(DataPenanaman data);

    // Hitung estimasi serapan karbon
    // Pseudocode (Algo-003):
    //   IF jenisPohon IS NULL OR jumlahPohon <= 0 → error
    //   dataPohon ← Query Q-010 (cari by nama_pohon)
    //   IF dataPohon IS EMPTY → error
    //   estimasiKarbon = jumlahPohon * dataPohon.serapanKarbon
    public float hitungEstimasi(String jenisPohon, int jumlahPohon);

    // Simpan data penanaman (Algo-004):
    //   validasiInput → hitungEstimasi → Query Q-002
    public String simpanDataPenanaman(DataPenanaman data);
}
```

---

### 6.2 PohonController (C-13)
```java
class PohonController {

    // Ambil semua data pohon (Query Q-005)
    public List<DataPohon> getDataPohon();

    // Validasi input data pohon (Algo-010):
    //   namaPohon tidak boleh null/kosong
    //   serapanKarbon tidak boleh negatif
    public boolean validasiData(DataPohon data);

    // Simpan foto ke local file system, return path
    public String prosesSimpanFoto(File file);

    // Simpan data pohon (Query Q-006)
    public String simpanDataPohon(DataPohon data);

    // Ambil detail satu pohon (Query Q-007)
    public DataPohon getDetailPohon(String idPohon);

    // Update data pohon (Query Q-008)
    public String updateDataPohon(DataPohon data);

    // Hapus data pohon (Query Q-009)
    public String hapusDataPohon(String idPohon);
}
```

---

### 6.3 LaporanPohonController (C-12)
```java
class LaporanPohonController {

    // Proses laporan pohon rusak (Algo-015):
    //   IF kondisi/lokasi null → error
    //   IF ada foto → prosesSimpanFoto() → simpan path
    //   hitungKapasitasBaru()
    //   Query Q-011 → simpan laporan
    public String prosesLaporan(LaporanPohon data);

    // Hitung ulang estimasi karbon setelah pohon rusak/mati
    public float hitungKapasitasBaru(LaporanPohon data);
}
```

---

### 6.4 StatistikController (C-14)
```java
class StatistikController {

    // Hitung statistik total atau berdasarkan filter periode (Algo-020):
    //   IF filterPeriode == null → Query Q-013
    //   ELSE → Query Q-014
    //   Return: totalPohon, totalSerapanKarbon, filterPeriode
    public Map<String, Object> hitungStatistikTotal(String filterPeriode);

    // Format data untuk grafik JavaFX (Algo-021):
    //   Panggil hitungStatistikTotal()
    //   Return data dalam format siap render chart
    public Object ambilDataVisualisasi(String filterPeriode);
}
```

---

## 7. REPOSITORY CLASSES

### 7.1 PenanamanRepository
```java
class PenanamanRepository {
    public List<DataPenanaman> findAll();      // Q-001
    public boolean save(DataPenanaman d);      // Q-002
    public boolean update(DataPenanaman d);    // Q-003
    public boolean delete(String idPenanaman); // Q-004
}
```

### 7.2 PohonRepository
```java
class PohonRepository {
    public List<DataPohon> findAll();          // Q-005
    public boolean save(DataPohon d);          // Q-006
    public DataPohon findById(String id);      // Q-007
    public boolean update(DataPohon d);        // Q-008
    public boolean delete(String id);          // Q-009
    public DataPohon findByNama(String nama);  // Q-010
}
```

### 7.3 LaporanRepository
```java
class LaporanRepository {
    public boolean save(LaporanPohon l);           // Q-011
    public List<LaporanPohon> getHistoryLaporan(); // Q-012
}
```

---

## 8. SQL QUERIES LENGKAP

| Kode | SQL | Keterangan |
|---|---|---|
| Q-001 | `SELECT * FROM data_penanaman ORDER BY tanggal_penanaman DESC;` | Semua data penanaman |
| Q-002 | `INSERT INTO data_penanaman (id_penanaman, lokasi, jenis_pohon, jumlah_pohon, tanggal_penanaman, estimasi_karbon) VALUES (?, ?, ?, ?, ?, ?);` | Simpan penanaman baru |
| Q-003 | `UPDATE data_penanaman SET lokasi=?, jenis_pohon=?, jumlah_pohon=?, tanggal_penanaman=?, estimasi_karbon=? WHERE id_penanaman=?;` | Update penanaman |
| Q-004 | `DELETE FROM data_penanaman WHERE id_penanaman=?;` | Hapus penanaman |
| Q-005 | `SELECT * FROM data_pohon ORDER BY nama_pohon ASC;` | Semua data pohon |
| Q-006 | `INSERT INTO data_pohon (id_pohon, nama_pohon, serapan_karbon, status, file_foto) VALUES (?, ?, ?, ?, ?);` | Simpan pohon baru |
| Q-007 | `SELECT * FROM data_pohon WHERE id_pohon=?;` | Detail pohon by ID |
| Q-008 | `UPDATE data_pohon SET nama_pohon=?, serapan_karbon=?, status=?, file_foto=? WHERE id_pohon=?;` | Update pohon |
| Q-009 | `DELETE FROM data_pohon WHERE id_pohon=?;` | Hapus pohon |
| Q-010 | `SELECT * FROM data_pohon WHERE nama_pohon=?;` | Cari pohon by nama (untuk hitung karbon) |
| Q-011 | `INSERT INTO laporan_pohon (id_laporan, id_pohon, kondisi, lokasi, file_foto, estimasi_karbon, tanggal_laporan) VALUES (?, ?, ?, ?, ?, ?, ?);` | Simpan laporan |
| Q-012 | `SELECT * FROM laporan_pohon ORDER BY tanggal_laporan DESC;` | Riwayat laporan |
| Q-013 | `SELECT SUM(jumlah_pohon) AS total_pohon, SUM(estimasi_karbon) AS total_karbon FROM data_penanaman;` | Statistik total tanpa filter |
| Q-014 | `SELECT SUM(jumlah_pohon) AS total_pohon, SUM(estimasi_karbon) AS total_karbon FROM data_penanaman WHERE tanggal_penanaman BETWEEN ? AND ?;` | Statistik dengan filter periode |
| Q-015 | `SELECT lokasi, SUM(jumlah_pohon) AS total_pohon FROM data_penanaman GROUP BY lokasi;` | Statistik per lokasi |

---

## 9. ALGORITMA LENGKAP (Pseudocode)

### Algo-001 — User.pilihMenu()
```
pilihMenu(menuTujuan):
  IF menuTujuan NULL OR ""  → return "Menu tidak valid"
  IF "Data Penanaman"       → open HalamanDataPenanaman
  IF "Data Pohon"           → open HalamanDataPohon
  IF "Laporan Pohon"        → open FormLaporanPohon
  IF "Statistik"            → open HalamanStatistik
  ELSE                      → return "Menu tidak ditemukan"
```

### Algo-003 — PenanamanController.hitungEstimasi()
```
hitungEstimasi(jenisPohon, jumlahPohon):
  IF jenisPohon NULL OR jumlahPohon <= 0 → error
  dataPohon ← Q-010 (WHERE nama_pohon = jenisPohon)
  IF dataPohon EMPTY → error "pohon tidak ditemukan"
  estimasiKarbon = jumlahPohon * dataPohon.serapanKarbon
  return estimasiKarbon
```

### Algo-004 — PenanamanController.simpanDataPenanaman()
```
simpanDataPenanaman(data):
  IF validasiInput(data) == FALSE → error
  data.estimasiKarbon = hitungEstimasi(data.jenisPohon, data.jumlahPohon)
  result ← Q-002
  return result == SUCCESS ? "Berhasil" : "Gagal"
```

### Algo-010 — PohonController.validasiData()
```
validasiData(dataPohon):
  IF namaPohon NULL OR ""  → return FALSE
  IF serapanKarbon < 0     → return FALSE
  return TRUE
```

### Algo-015 — LaporanPohonController.prosesLaporan()
```
prosesLaporan(dataLaporan):
  IF kondisi NULL OR lokasi NULL → error "Data belum lengkap"
  IF fileFoto NOT NULL:
    filePath = PohonController.prosesSimpanFoto(fileFoto)
    dataLaporan.fileFoto = filePath
  dataLaporan.estimasiKarbon = hitungKapasitasBaru(dataLaporan)
  result ← Q-011
  return result == SUCCESS ? SUCCESS : FAILED
```

### Algo-020 — StatistikController.hitungStatistikTotal()
```
hitungStatistikTotal(filterPeriode):
  IF filterPeriode NULL → statistikData ← Q-013
  ELSE                  → statistikData ← Q-014
  totalPohon       = SUM(jumlah_pohon)
  totalSerapan     = SUM(estimasi_karbon)
  return { totalPohon, totalSerapanKarbon, filterPeriode }
```

### Algo-021 — StatistikController.ambilDataVisualisasi()
```
ambilDataVisualisasi(filterPeriode):
  statistik = hitungStatistikTotal(filterPeriode)
  IF statistik NOT EMPTY → format ke data grafik JavaFX → return
  ELSE                   → return EMPTY DATA
```

---

## 10. USE CASE SUMMARY

| UC | Nama | Boundary | Controller | Entity |
|---|---|---|---|---|
| UC01 | Melihat Data Penanaman | HalamanDataPenanaman | PenanamanController | DataPenanaman |
| UC02 | Melaporkan Pohon Rusak | FormLaporanPohon | LaporanPohonController | LaporanPohon |
| UC03 | Melihat Data Pohon | HalamanDataPohon | PohonController (DataPohonController) | DataPohon |
| UC04 | Melihat Statistik Serapan Karbon | HalamanStatistik | StatistikController | DataPohon, DataPenanaman |
| UC05 | Mengelola Data Penanaman (CRUD) | FormLaporanPenanaman | PenanamanController | DataPenanaman |
| UC06 | Menginput Foto dan Data Pohon | FormDataPohon | PohonController | DataPohon |
| UC07 | Mengelola Data Pohon (CRUD) | HalamanDataPohon, FormDataPohon | PohonController | DataPohon |
| UC08 | Memfilter Statistik | HalamanStatistik | StatistikController | DataPenanaman, DataPohon |

---

## 11. SEQUENCE FLOWS RINGKAS

### UC01 — Normal
```
User → pilihMenuPenanaman() → HalamanDataPenanaman
HalamanDataPenanaman → ambilDataPenanaman() → PenanamanController
PenanamanController → cariData() → DataPenanaman
DataPenanaman → dataPenanaman → PenanamanController
PenanamanController → dataPenanaman → HalamanDataPenanaman
HalamanDataPenanaman → tampilkanData() → User
```

### UC01 — Data Kosong
```
... → DataPenanaman → dataKosong → PenanamanController
PenanamanController → dataKosong → HalamanDataPenanaman
HalamanDataPenanaman → tampilkanPesanError() → User
```

### UC02 — Normal
```
User → isiLaporan() + unggahFoto() → FormLaporanPohon
FormLaporanPohon → kumpulkanDataInput()
User → kirimLaporan() → FormLaporanPohon
FormLaporanPohon → validasiData()
FormLaporanPohon → prosesLaporan(dataInput, fileFoto) → LaporanPohonController
LaporanPohonController → hitungEstimasiKarbon()
LaporanPohonController → simpanLaporan() → LaporanPohon
LaporanPohon → statusSimpan → LaporanPohonController
LaporanPohonController → laporanBerhasil → FormLaporanPohon
FormLaporanPohon → tampilkanStatus() → User
```

### UC02 — Data Tidak Lengkap
```
User → kirimLaporan() → FormLaporanPohon
FormLaporanPohon → validasiData() → GAGAL
FormLaporanPohon → tampilkanStatus("Data tidak lengkap") → User
```

### UC03 — Normal
```
User → lihatDataPohon() → HalamanDataPohon
HalamanDataPohon → ambilDataPohon() → PohonController
PohonController → cariDataPohon() → DataPohon
DataPohon → daftarPohon → PohonController → HalamanDataPohon → tampilkanData() → User
User → pilihDataPohon() → HalamanDataPohon
HalamanDataPohon → ambilDetailPohon(idPohon) → PohonController
PohonController → cariDataPohon(idPohon) → DataPohon
DataPohon → detailPohon → PohonController → HalamanDataPohon → tampilkanData() → User
```

### UC04 — Tanpa Filter
```
User → lihatStatistik() → HalamanStatistik
HalamanStatistik → ambilData() → StatistikController
StatistikController → getDataPohon() → DataPohon → dataPohon
StatistikController → getDataPenanaman() → DataPenanaman → dataPenanaman
StatistikController → hitungStatistik()
StatistikController → hasilStatistik → HalamanStatistik
HalamanStatistik → tampilkanStatistik() → User
```

### UC08 — Dengan Filter Periode
```
User → pilihFilterPeriode() → HalamanStatistik
HalamanStatistik → terapkanFilter(filter) → StatistikController
StatistikController → getDataFiltered() → DataPenanaman
StatistikController → getDataFiltered() → DataPohon
StatistikController → hitungStatistik()
StatistikController → updateGrafik() → HalamanStatistik → tampilkan grafik → User
```

---

## 12. CLASS DIAGRAM DETAIL

### Boundary Layer
```
HalamanStatistik
  + tampilkanGrafik(filterPeriode: String): void
  + pilihFilterPeriode(): void

HalamanDataPohon
  + tampilkanDaftarPohon(): void
  + tampilkanDetailPohon(): void

HalamanDataPenanaman
  + tampilkanDaftarPenanaman(): void
  + tampilkanFormInput(): void

FormLaporanPohon
  + tampilkanFormLaporan(): void
  + kirimDataLaporan(data: LaporanPohon): void

FormDataPohon
  + tampilkanForm(): void
  + unggahFoto(): void
  + isiDataPohon(): void
```

### Controller Layer
```
PenanamanController
  + getDataPenanaman(): List<DataPenanaman>
  + validasiInput(data: DataPenanaman): boolean
  + hitungEstimasi(jenisPohon: String, jumlahPohon: int): float
  + simpanDataPenanaman(data: DataPenanaman): String

PohonController  (alias DataPohonController)
  + getDataPohon(): List<DataPohon>
  + validasiData(data: DataPohon): boolean
  + prosesSimpanFoto(file: File): String
  + simpanDataPohon(data: DataPohon): String
  + getDetailPohon(idPohon: String): DataPohon
  + updateDataPohon(data: DataPohon): String
  + hapusDataPohon(idPohon: String): String

LaporanPohonController
  + prosesLaporan(data: LaporanPohon): String
  + hitungKapasitasBaru(data: LaporanPohon): float

StatistikController
  + hitungStatistikTotal(filterPeriode: String): Map<String, Object>
  + ambilDataVisualisasi(filterPeriode: String): Object
```

### Entity Layer
```
User
  - idUser: String
  - nama: String
  - role: String
  + pilihMenu(menuTujuan: String): void

DataPohon
  - idPohon: String
  - namaPohon: String
  - serapanKarbon: float
  - status: String
  - fileFotoPath: String

DataPenanaman
  - idPenanaman: String
  - lokasi: String
  - jenisPohon: String
  - jumlahPohon: int
  - tanggalPenanaman: String
  - estimasiKarbon: float

LaporanPohon
  - idLaporan: String
  - kondisi: String
  - lokasi: String
  - fileFotoPath: String
  - estimasiKarbon: float
  - tanggalLaporan: String
```

---

## 13. UTIL CLASSES

### DBConnection.java
```java
// Singleton JDBC connection ke PostgreSQL
class DBConnection {
    private static Connection instance;
    public static Connection getConnection();
    // config: host, port, dbname, user, password dari config file
}
```

### FileManager.java
```java
// Mengelola penyimpanan foto dan ekspor laporan ke local folder
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
| C-13 DataPohonController | PohonController | UC03, UC06, UC07 |
| C-14 StatistikController | StatistikController | UC04, UC08 |

---

## 15. NOTES & CONSTRAINTS

1. **Aplikasi desktop single-user** — tidak ada authentication kompleks, cukup `User` entity sederhana.
2. **Foto pohon** disimpan di local folder (bukan DB), DB hanya menyimpan path string-nya.
3. **Statechart tidak diimplementasikan** — perubahan status pohon (rusak/mati/ditebang) langsung dieksekusi ke DB tanpa state machine.
4. **Estimasi karbon dihitung otomatis** saat menyimpan penanaman baru maupun laporan kondisi pohon.
5. **Filter statistik** bisa berdasarkan periode waktu (tanggal mulai — tanggal akhir) atau tanpa filter (semua data).
6. **Ekspor laporan** (PDF/CSV) disimpan ke local folder via `FileManager`.
7. **JavaFX Charts** digunakan untuk `Line Chart` (jumlah pohon) dan `Bar Chart` (serapan karbon) di `HalamanStatistik`.
8. Semua query menggunakan **PreparedStatement** (parameterized) untuk menghindari SQL injection.
9. `id_*` fields menggunakan **UUID** yang di-generate di Java sebelum INSERT.
10. Tidak ada fitur login/autentikasi berdasarkan dokumen DPPL — jika diperlukan, tambahkan sebagai extension.
