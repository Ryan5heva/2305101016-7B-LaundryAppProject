package com.example.laundryapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Mengelola database LaundryDB dan seluruh operasi CRUD pada tabel "laundry".
 * Semua query memakai parameter (?) agar aman dari SQL injection.
 * Gunakan applicationContext saat membuat objek ini dari Activity.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LaundryDB"
        private const val DATABASE_VERSION = 1

        // Nama tabel dan kolom
        private const val TABLE_LAUNDRY = "laundry"
        private const val COL_ID = "id"
        private const val COL_NAMA = "nama_pelanggan"
        private const val COL_TELEPON = "nomor_telepon"
        private const val COL_JENIS = "jenis_laundry"
        private const val COL_BERAT = "berat"
        private const val COL_HARGA_PER_KG = "harga_per_kg"
        private const val COL_TOTAL = "total_harga"
        private const val COL_TANGGAL_MASUK = "tanggal_masuk"
        private const val COL_TANGGAL_SELESAI = "tanggal_selesai"
        private const val COL_STATUS = "status"
        private const val COL_CATATAN = "catatan"
    }

    // Dipanggil sekali saat database pertama kali dibuat
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_LAUNDRY (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAMA TEXT NOT NULL,
                $COL_TELEPON TEXT NOT NULL,
                $COL_JENIS TEXT NOT NULL,
                $COL_BERAT REAL NOT NULL,
                $COL_HARGA_PER_KG INTEGER NOT NULL,
                $COL_TOTAL INTEGER NOT NULL,
                $COL_TANGGAL_MASUK TEXT NOT NULL,
                $COL_TANGGAL_SELESAI TEXT NOT NULL,
                $COL_STATUS TEXT NOT NULL,
                $COL_CATATAN TEXT
            )
            """.trimIndent()
        )
    }

    // Dipanggil saat DATABASE_VERSION dinaikkan.
    // Untuk tugas ini tabel dibuat ulang; data lama akan hilang.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LAUNDRY")
        onCreate(db)
    }

    // ================= CREATE =================

    // Menyimpan pesanan baru dan mengembalikan ID yang dibuat otomatis (-1 jika gagal)
    fun insertLaundry(laundry: Laundry): Long {
        return writableDatabase.insert(TABLE_LAUNDRY, null, toContentValues(laundry))
    }

    // ================= READ =================

    // Mengambil semua pesanan, terbaru di atas
    fun getAllLaundry(): List<Laundry> = searchLaundry("", null, null)

    // Mengambil satu pesanan berdasarkan ID, atau null jika tidak ditemukan
    fun getLaundryById(id: Int): Laundry? {
        return queryLaundry("$COL_ID = ?", arrayOf(id.toString()), null).firstOrNull()
    }

    // Mencari berdasarkan nama pelanggan atau ID (contoh: "LD-0003" atau "3"),
    // dengan filter opsional status dan jenis laundry
    fun searchLaundry(keyword: String, status: String?, jenis: String?): List<Laundry> {
        val selection = StringBuilder("($COL_NAMA LIKE ? OR $COL_ID = ?)")
        val args = mutableListOf("%$keyword%", parseId(keyword).toString())

        if (!status.isNullOrEmpty()) {
            selection.append(" AND $COL_STATUS = ?")
            args.add(status)
        }
        if (!jenis.isNullOrEmpty()) {
            selection.append(" AND $COL_JENIS = ?")
            args.add(jenis)
        }
        return queryLaundry(selection.toString(), args.toTypedArray(), "$COL_ID DESC")
    }

    // Menghitung pesanan dengan status tertentu, dipakai untuk kartu statistik dashboard
    fun getLaundryCountByStatus(status: String): Int {
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_LAUNDRY WHERE $COL_STATUS = ?",
            arrayOf(status)
        ).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    // Menghitung seluruh pesanan, dipakai untuk kartu "Total Pesanan" di dashboard
    fun getTotalLaundryCount(): Int {
        readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_LAUNDRY", null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    // ================= UPDATE =================

    // Memperbarui pesanan berdasarkan ID, mengembalikan jumlah baris yang berubah
    fun updateLaundry(laundry: Laundry): Int {
        return writableDatabase.update(
            TABLE_LAUNDRY,
            toContentValues(laundry),
            "$COL_ID = ?",
            arrayOf(laundry.id.toString())
        )
    }

    // ================= DELETE =================

    // Menghapus pesanan berdasarkan ID, mengembalikan jumlah baris yang terhapus
    fun deleteLaundry(id: Int): Int {
        return writableDatabase.delete(TABLE_LAUNDRY, "$COL_ID = ?", arrayOf(id.toString()))
    }

    // ================= HELPER PRIVATE =================

    // Menjalankan query SELECT dan mengubah setiap baris menjadi objek Laundry
    private fun queryLaundry(selection: String?, args: Array<String>?, orderBy: String?): List<Laundry> {
        val result = mutableListOf<Laundry>()
        readableDatabase.query(
            TABLE_LAUNDRY, null, selection, args, null, null, orderBy
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursorToLaundry(cursor))
            }
        }
        return result
    }

    // Mengubah satu baris Cursor menjadi objek Laundry
    private fun cursorToLaundry(cursor: Cursor): Laundry {
        return Laundry(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
            namaPelanggan = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA)),
            nomorTelepon = cursor.getString(cursor.getColumnIndexOrThrow(COL_TELEPON)),
            jenisLaundry = cursor.getString(cursor.getColumnIndexOrThrow(COL_JENIS)),
            berat = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BERAT)),
            hargaPerKg = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HARGA_PER_KG)),
            totalHarga = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TOTAL)),
            tanggalMasuk = cursor.getString(cursor.getColumnIndexOrThrow(COL_TANGGAL_MASUK)),
            tanggalSelesai = cursor.getString(cursor.getColumnIndexOrThrow(COL_TANGGAL_SELESAI)),
            status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
            catatan = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATATAN)) ?: ""
        )
    }

    // Mengubah objek Laundry menjadi ContentValues untuk insert dan update.
    // Kolom id tidak dimasukkan karena dikelola otomatis oleh SQLite.
    private fun toContentValues(laundry: Laundry): ContentValues {
        return ContentValues().apply {
            put(COL_NAMA, laundry.namaPelanggan)
            put(COL_TELEPON, laundry.nomorTelepon)
            put(COL_JENIS, laundry.jenisLaundry)
            put(COL_BERAT, laundry.berat)
            put(COL_HARGA_PER_KG, laundry.hargaPerKg)
            put(COL_TOTAL, laundry.totalHarga)
            put(COL_TANGGAL_MASUK, laundry.tanggalMasuk)
            put(COL_TANGGAL_SELESAI, laundry.tanggalSelesai)
            put(COL_STATUS, laundry.status)
            put(COL_CATATAN, laundry.catatan)
        }
    }

    // Mengambil angka ID dari kata kunci, misalnya "LD-0003" menjadi 3.
    // Mengembalikan -1 jika kata kunci bukan angka (tidak akan cocok dengan ID mana pun).
    private fun parseId(keyword: String): Int {
        return keyword.trim()
            .removePrefix("LD-")
            .removePrefix("ld-")
            .toIntOrNull() ?: -1
    }
}
