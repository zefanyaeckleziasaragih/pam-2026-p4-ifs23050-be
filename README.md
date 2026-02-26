# pam-2026-p4-ifs23051-be (Extended with Flowers / Bahasa Bunga API)

Backend API berbasis **Ktor** (Kotlin) untuk aplikasi **Delcom Plants** yang telah diperluas dengan endpoint **Bahasa Bunga (Flowers)**.

---

## Tech Stack

| Komponen | Detail              |
|----------|---------------------|
| Runtime  | Kotlin / JVM        |
| Framework| Ktor 3.x            |
| ORM      | Exposed (JetBrains) |
| Database | PostgreSQL          |
| DI       | Koin                |
| Build    | Gradle (Kotlin DSL) |

---

## Environment Variables (`.env`)

```env
APP_HOST=0.0.0.0
APP_PORT=8080
DB_HOST=localhost
DB_PORT=5432
DB_NAME=delcom_plants
DB_USER=postgres
DB_PASSWORD=secret
```

---

## Database

Jalankan `data.sql` atau biarkan Exposed membuat tabel otomatis saat server pertama kali start (`SchemaUtils.createMissingTablesAndColumns`).

```sql
-- Tabel Flowers (baru)
CREATE TABLE IF NOT EXISTS flowers (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama_umum   VARCHAR(100) NOT NULL,
    nama_latin  VARCHAR(150) NOT NULL,
    makna       VARCHAR(200) NOT NULL,
    asal_budaya VARCHAR(200) NOT NULL,
    deskripsi   TEXT         NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);
```

---

## Building & Running

| Task                    | Deskripsi                             |
|-------------------------|---------------------------------------|
| `./gradlew run`         | Jalankan server langsung              |
| `./gradlew build`       | Build project                         |
| `./gradlew buildFatJar` | Build executable JAR semua dependensi |

---

## API Endpoints

### Plants

| Method | Endpoint             | Deskripsi                     |
|--------|----------------------|-------------------------------|
| GET    | `/plants`            | Ambil semua tumbuhan          |
| GET    | `/plants?search=`    | Cari tumbuhan berdasarkan nama|
| POST   | `/plants`            | Tambah tumbuhan baru          |
| GET    | `/plants/{id}`       | Ambil tumbuhan by ID          |
| PUT    | `/plants/{id}`       | Update tumbuhan by ID         |
| DELETE | `/plants/{id}`       | Hapus tumbuhan by ID          |
| GET    | `/plants/{id}/image` | Ambil gambar tumbuhan         |

### Flowers (Bahasa Bunga) — NEW

| Method | Endpoint               | Deskripsi                        |
|--------|------------------------|----------------------------------|
| GET    | `/flowers`             | Ambil semua data bahasa bunga    |
| GET    | `/flowers?search=`     | Cari bunga berdasarkan nama umum |
| POST   | `/flowers`             | Tambah data bunga baru           |
| GET    | `/flowers/{id}`        | Ambil bunga by ID                |
| PUT    | `/flowers/{id}`        | Update bunga by ID               |
| DELETE | `/flowers/{id}`        | Hapus bunga by ID                |
| GET    | `/flowers/{id}/image`  | Ambil gambar bunga               |

**POST / PUT `/flowers` — multipart form fields:**

| Field        | Type  | Required (POST) | Required (PUT)  |
|--------------|-------|-----------------|-----------------|
| `namaUmum`   | text  | Yes             | Yes             |
| `namaLatin`  | text  | Yes             | Yes             |
| `makna`      | text  | Yes             | Yes             |
| `asalBudaya` | text  | Yes             | Yes             |
| `deskripsi`  | text  | Yes             | Yes             |
| `file`       | image | Yes             | Optional        |

### Profile

| Method | Endpoint         | Deskripsi           |
|--------|------------------|---------------------|
| GET    | `/profile`       | Ambil data profil   |
| GET    | `/profile/photo` | Ambil foto profil   |

---

## Response Format

```json
{
  "status": "success",
  "message": "Berhasil mengambil daftar bahasa bunga",
  "data": {
    "flowers": [
      {
        "id": "uuid",
        "namaUmum": "Mawar Merah",
        "namaLatin": "Rosa damascena",
        "makna": "Cinta mendalam & gairah",
        "asalBudaya": "Eropa Victoria",
        "deskripsi": "...",
        "createdAt": "2026-02-25T10:00:00Z",
        "updatedAt": "2026-02-25T10:00:00Z"
      }
    ]
  }
}
```

---

## Struktur Proyek

```
src/main/kotlin/
├── Application.kt
├── Routing.kt               # plants + flowers + profile
├── dao/
│   ├── PlantDAO.kt
│   └── FlowerDAO.kt         NEW
├── data/
│   ├── PlantRequest.kt
│   └── FlowerRequest.kt     NEW
├── entities/
│   ├── Plant.kt
│   └── Flower.kt            NEW
├── helpers/
│   ├── DatabaseHelper.kt    auto-create tables
│   └── MappingHelper.kt     + flowerDaoToModel
├── module/AppModule.kt      + FlowerRepository + FlowerService
├── repositories/
│   ├── IFlowerRepository.kt NEW
│   └── FlowerRepository.kt  NEW
├── services/
│   └── FlowerService.kt     NEW
└── tables/
    └── FlowerTable.kt       NEW
```
