package com.example.laundryapp

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Model data satu pesanan laundry.
 * Setiap properti sesuai dengan kolom pada tabel "laundry" di SQLite.
 */
data class Laundry(
    val id: Int = 0,                 // 0 berarti data baru yang belum tersimpan
    val namaPelanggan: String,
    val nomorTelepon: String,
    val jenisLaundry: String,
    val berat: Double,
    val hargaPerKg: Int,
    val totalHarga: Int,
    val tanggalMasuk: String,        // format dd/MM/yyyy
    val tanggalSelesai: String,      // format dd/MM/yyyy
    val status: String,
    val catatan: String
) {
    // Menampilkan ID dalam format LD-0001
    fun idFormatted(): String = String.format(Locale.US, "LD-%04d", id)
}

/**
 * Konstanta dan aturan bisnis LaundryApp.
 * Dikumpulkan di satu tempat agar mudah diubah.
 */
object LaundryConstants {

    // Jenis laundry
    const val JENIS_REGULER = "Cuci Reguler"
    const val JENIS_EXPRESS = "Cuci Express"
    const val JENIS_KERING = "Cuci Kering"
    const val JENIS_SETRIKA = "Cuci Setrika"

    // Status pesanan
    const val STATUS_MENUNGGU = "Menunggu"
    const val STATUS_DIPROSES = "Diproses"
    const val STATUS_SELESAI = "Selesai"
    const val STATUS_DIAMBIL = "Sudah Diambil"

    // Daftar untuk dropdown dan filter
    val JENIS_LIST = listOf(JENIS_REGULER, JENIS_EXPRESS, JENIS_KERING, JENIS_SETRIKA)
    val STATUS_LIST = listOf(STATUS_MENUNGGU, STATUS_DIPROSES, STATUS_SELESAI, STATUS_DIAMBIL)

    // Format tanggal yang dipakai di seluruh aplikasi
    const val FORMAT_TANGGAL = "dd/MM/yyyy"

    // Harga default per kg untuk setiap jenis laundry
    private val HARGA_PER_KG = mapOf(
        JENIS_REGULER to 5000,
        JENIS_EXPRESS to 8000,
        JENIS_KERING to 6000,
        JENIS_SETRIKA to 7000
    )

    // Estimasi lama pengerjaan dalam hari
    private val ESTIMASI_HARI = mapOf(
        JENIS_REGULER to 3,
        JENIS_EXPRESS to 1,
        JENIS_KERING to 2,
        JENIS_SETRIKA to 3
    )

    // Mengembalikan harga per kg sesuai jenis laundry (0 jika jenis tidak dikenal)
    fun hargaPerKg(jenis: String): Int = HARGA_PER_KG[jenis] ?: 0

    // Total harga = berat x harga per kg, dibulatkan ke rupiah terdekat
    fun hitungTotal(berat: Double, hargaPerKg: Int): Int =
        (berat * hargaPerKg).roundToInt()

    // Tanggal hari ini dalam format dd/MM/yyyy
    fun tanggalHariIni(): String =
        SimpleDateFormat(FORMAT_TANGGAL, Locale.US).format(Date())

    // Estimasi tanggal selesai = tanggal masuk + lama pengerjaan jenis laundry
    fun hitungTanggalSelesai(tanggalMasuk: String, jenis: String): String {
        val format = SimpleDateFormat(FORMAT_TANGGAL, Locale.US)
        val calendar = Calendar.getInstance()
        calendar.time = format.parse(tanggalMasuk) ?: Date()
        calendar.add(Calendar.DAY_OF_YEAR, ESTIMASI_HARI[jenis] ?: 0)
        return format.format(calendar.time)
    }

    // Mengubah angka menjadi format rupiah, contoh 15000 menjadi "Rp15.000"
    fun formatRupiah(nominal: Int): String {
        val angka = nominal.toString().reversed().chunked(3).joinToString(".").reversed()
        return "Rp$angka"
    }

    // Berat tanpa angka nol di belakang koma, contoh 2.0 menjadi "2" dan 2.5 tetap "2.5"
    fun formatBerat(berat: Double): String =
        if (berat % 1.0 == 0.0) berat.toInt().toString() else berat.toString()
}
