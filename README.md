# Pengumpulan UTS Pemrograman Mobile

## Identitas Mahasiswa

- Nama: Ryan Sheva Danarindra
- NIM: 2305101016
- Kelas: 7B
- Program Studi: Teknik Informatika
- Mata Kuliah: Pemrograman Mobile
- Tahun Akademik: 2026-2027

## Deskripsi

Repository ini berisi source code, dokumentasi, dan hasil implementasi
aplikasi mobile untuk pengerjaan Ujian Tengah Semester (UTS)
mata kuliah Pemrograman Mobile.

## Deskripsi Pengerjaan UTS

Aplikasi **LaundryApp** adalah aplikasi manajemen laundry sederhana untuk Android.
Aplikasi memungkinkan admin mencatat pesanan laundry, memantau statusnya,
serta mencari dan memfilter pesanan. Seluruh data disimpan secara lokal
menggunakan SQLite sehingga tetap tersimpan ketika aplikasi ditutup.

## Teknologi yang Digunakan

- Bahasa Pemrograman: Kotlin
- Framework: Android SDK (Empty Views Activity, tanpa Jetpack Compose)
- Tools: Android Studio, Gradle Kotlin DSL
- Database: SQLite menggunakan SQLiteOpenHelper
- Komponen UI: XML Layout, RecyclerView, ViewBinding, Material Design 3
- Minimum SDK: API 24 (Android 7.0)

## Fitur yang Diimplementasikan

1. Login lokal dengan validasi input (akun demo: admin / admin123).
2. Dashboard dengan statistik real dari SQLite: total pesanan, menunggu, diproses, selesai, dan sudah diambil.
3. CRUD pesanan laundry: tambah, lihat daftar, lihat detail, edit, dan hapus dengan konfirmasi.
4. Perhitungan total harga otomatis berdasarkan berat dan jenis laundry, dengan format rupiah.
5. Estimasi tanggal selesai otomatis berdasarkan jenis laundry.
6. Pencarian pesanan berdasarkan nama pelanggan atau ID pesanan.
7. Filter pesanan berdasarkan status dan jenis laundry.
8. Perubahan status pesanan melalui dialog pilihan.
9. Data tetap tersimpan setelah aplikasi ditutup.

## Struktur Folder

- `uts/`: Source code project Android dan file pendukung UTS.
- `uts/screenshots/`: Bukti visual hasil implementasi aplikasi.
  - `halaman-utama.png`: Halaman dashboard.
  - `halaman-kedua.png`: Halaman daftar pesanan.
  - `hasil-implementasi.png`: Halaman detail dan hasil implementasi fitur.

## Persyaratan Menjalankan Aplikasi

- Android Studio (disarankan versi terbaru)
- JDK 17 atau yang disediakan Android Studio
- Android SDK dengan API 24 atau lebih tinggi
- Emulator Android atau perangkat Android fisik dengan USB Debugging aktif

## Cara Menjalankan Aplikasi

1. Clone repository ini atau unduh sebagai ZIP.
2. Buka folder `uts/` menggunakan **File → Open** di Android Studio.
3. Tunggu proses Gradle Sync selesai.
4. Pilih emulator atau perangkat fisik pada daftar perangkat.
5. Tekan tombol **Run** (Shift+F10).
6. Login menggunakan username `admin` dan password `admin123`.

## Screenshot Hasil Implementasi

Screenshot tersedia pada folder `uts/screenshots/`:

- `halaman-utama.png`
- `halaman-kedua.png`
- `hasil-implementasi.png`

## File APK

**Build → Build App Bundle(s) / APK(s) → Build APK(s)** di Android Studio.

## Referensi

- Dokumentasi Android Developers: https://developer.android.com
- Material Design 3: https://m3.material.io
- Dokumentasi SQLite Android: https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
