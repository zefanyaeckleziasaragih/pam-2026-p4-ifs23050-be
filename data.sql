-- Tabel Plants (existing)
CREATE TABLE IF NOT EXISTS plants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    manfaat TEXT NOT NULL,
    efek_samping TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Tabel Flowers / Bahasa Bunga (new)
CREATE TABLE IF NOT EXISTS flowers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama_umum   VARCHAR(100) NOT NULL,
    nama_latin  VARCHAR(150) NOT NULL,
    makna       VARCHAR(200) NOT NULL,
    asal_budaya VARCHAR(200) NOT NULL,
    deskripsi   TEXT         NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);
