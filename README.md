# IF2050-2026-K01-G01-EcoTrack

Aplikasi Manajemen Pendataan Pohon dan Penghijawan Lingkungan berbasis JavaFX dan PostgreSQL.

## Prerequisites

- Java 17 atau lebih tinggi
- Maven 3.6+
- Docker & Docker Compose

## Setup & Running

### 1. Clone Repository

```bash
git clone <repository-url>
cd drpl
```

### 2. Setup Environment Variables

```bash
cp .env.example .env
```

### 3. Start Database

```bash
docker compose up -d
```

Script `db/init.sql` akan otomatis dijalankan saat pertama kali container dibuat (tabel dan user default langsung terbuat).

### 4. Database Seeding

Isi data contoh (pohon & penanaman):

```bash
docker compose exec -T ecotrack-db psql -U ecotrack_user -d ecotrack_db < db/seed.sql
```

### 5. Reset Database (Opsional)

Jika ingin mulai dari awal (hapus semua data & tabel):

```bash
docker compose down -v
docker compose up -d
docker compose exec -T ecotrack-db psql -U ecotrack_user -d ecotrack_db < db/seed.sql
```

Flag `-v` akan menghapus volume sehingga `init.sql` dijalankan ulang saat `up`.

### 6. Build & Run Application

```bash
mvn clean javafx:run
```

## Login

Saat aplikasi pertama kali dijalankan, akan muncul halaman login. Pilih akun dari dropdown dan masukkan password:

| Role    | Nama            | Password      |
|---------|-----------------|---------------|
| admin   | Admin EcoTrack  | `adminpass`   |
| petugas | Petugas Default | `petugaspass` |

Untuk menambah user baru, insert langsung ke tabel `user`:

```sql
INSERT INTO "user" (id_user, nama, role, password)
VALUES ('user-003', 'Nama User', 'petugas', 'password123');
```

## Tech Stack

- **Java 17** + **JavaFX 21** — Desktop UI
- **PostgreSQL 16** — Database
- **Maven** — Build tool
- **Docker** — Database containerization
