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

### 4. Build & Run Application

```bash
mvn clean javafx:run
```

## Tech Stack

- **Java 17** + **JavaFX 21** — Desktop UI
- **PostgreSQL 16** — Database
- **Maven** — Build tool
- **Docker** — Database containerization
