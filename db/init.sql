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
