-- =============================================
-- ECOTRACK DATABASE INIT SCRIPT
-- =============================================

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
