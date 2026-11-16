package com.example.laundryapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.laundryapp.databinding.ActivityAddLaundryBinding

/**
 * Form untuk menambah pesanan baru, sekaligus mengedit pesanan yang sudah ada.
 * Mode ditentukan oleh ada atau tidaknya EXTRA_LAUNDRY_ID.
 * Harga, total, dan estimasi selesai dihitung otomatis.
 */
class AddLaundryActivity : AppCompatActivity() {

    companion object {
        // Kunci untuk mengirim ID pesanan saat mode edit
        const val EXTRA_LAUNDRY_ID = "extra_laundry_id"
    }

    private lateinit var binding: ActivityAddLaundryBinding
    private lateinit var db: DatabaseHelper

    // ID pesanan yang sedang diedit. -1 berarti mode tambah baru
    private var laundryId = -1
    private val sedangEdit: Boolean get() = laundryId != -1

    // Tanggal masuk: hari ini untuk pesanan baru, atau tanggal lama saat edit
    private var tanggalMasuk = LaundryConstants.tanggalHariIni()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLaundryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(applicationContext)
        laundryId = intent.getIntExtra(EXTRA_LAUNDRY_ID, -1)

        setupDropdown()

        // Mode edit: isi form dengan data yang sudah ada
        if (sedangEdit && !isiFormDenganData()) {
            finish()
            return
        }

        binding.tvTitle.text = getString(if (sedangEdit) R.string.edit_title else R.string.add_title)
        binding.btnSimpan.text = getString(if (sedangEdit) R.string.edit_simpan else R.string.add_simpan)
        binding.tvTanggalMasuk.text = tanggalMasuk

        setupPerubahanInput()
        perbaruiRingkasan()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSimpan.setOnClickListener { simpanPesanan() }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    // Mengisi pilihan jenis dan status. Status awal adalah "Menunggu"
    private fun setupDropdown() {
        binding.actvJenis.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, LaundryConstants.JENIS_LIST)
        )
        binding.actvStatus.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, LaundryConstants.STATUS_LIST)
        )
        binding.actvStatus.setText(LaundryConstants.STATUS_MENUNGGU, false)
    }

    // Mengambil data pesanan dari SQLite dan memasukkannya ke setiap kolom form
    private fun isiFormDenganData(): Boolean {
        val data = db.getLaundryById(laundryId)
        if (data == null) {
            Toast.makeText(this, R.string.pesanan_tidak_ditemukan, Toast.LENGTH_SHORT).show()
            return false
        }

        tanggalMasuk = data.tanggalMasuk
        binding.etNama.setText(data.namaPelanggan)
        binding.etTelepon.setText(data.nomorTelepon)
        binding.actvJenis.setText(data.jenisLaundry, false)
        binding.etBerat.setText(LaundryConstants.formatBerat(data.berat))
        binding.actvStatus.setText(data.status, false)
        binding.etCatatan.setText(data.catatan)
        return true
    }

    // Ringkasan diperbarui setiap kali jenis laundry atau berat berubah
    private fun setupPerubahanInput() {
        binding.actvJenis.doAfterTextChanged { perbaruiRingkasan() }
        binding.etBerat.doAfterTextChanged { perbaruiRingkasan() }
    }

    // Menghitung harga per kg, estimasi selesai, dan total harga secara real-time
    private fun perbaruiRingkasan() {
        val jenis = binding.actvJenis.text.toString()
        val hargaPerKg = LaundryConstants.hargaPerKg(jenis)
        val berat = ambilBerat() ?: 0.0

        if (jenis in LaundryConstants.JENIS_LIST) {
            binding.tvHargaPerKg.text = "${LaundryConstants.formatRupiah(hargaPerKg)} / kg"
            binding.tvEstimasi.text = LaundryConstants.hitungTanggalSelesai(tanggalMasuk, jenis)
        } else {
            binding.tvHargaPerKg.text = "-"
            binding.tvEstimasi.text = "-"
        }

        val total = LaundryConstants.hitungTotal(berat, hargaPerKg)
        binding.tvTotal.text = LaundryConstants.formatRupiah(total)
    }

    // Membaca berat dari input. Koma diganti titik agar "2,5" tetap terbaca
    private fun ambilBerat(): Double? {
        return binding.etBerat.text?.toString()?.trim()?.replace(',', '.')?.toDoubleOrNull()
    }

    // Memvalidasi seluruh input, lalu menambah atau memperbarui pesanan di SQLite
    private fun simpanPesanan() {
        val nama = binding.etNama.text?.toString()?.trim().orEmpty()
        val telepon = binding.etTelepon.text?.toString()?.trim().orEmpty()
        val jenis = binding.actvJenis.text.toString()
        val berat = ambilBerat()
        val catatan = binding.etCatatan.text?.toString()?.trim().orEmpty()

        val statusDipilih = binding.actvStatus.text.toString()
        val status = if (statusDipilih in LaundryConstants.STATUS_LIST) {
            statusDipilih
        } else {
            LaundryConstants.STATUS_MENUNGGU
        }

        // Membersihkan pesan error dari percobaan sebelumnya
        binding.tilNama.error = null
        binding.tilTelepon.error = null
        binding.tilJenis.error = null
        binding.tilBerat.error = null

        var inputValid = true
        if (nama.isEmpty()) {
            binding.tilNama.error = getString(R.string.error_nama_kosong)
            inputValid = false
        }
        if (telepon.length < 8 || !telepon.all { it.isDigit() }) {
            binding.tilTelepon.error = getString(R.string.error_telepon_tidak_valid)
            inputValid = false
        }
        if (jenis !in LaundryConstants.JENIS_LIST) {
            binding.tilJenis.error = getString(R.string.error_jenis_kosong)
            inputValid = false
        }
        if (berat == null || berat <= 0.0) {
            binding.tilBerat.error = getString(R.string.error_berat_tidak_valid)
            inputValid = false
        }
        if (!inputValid) return

        // Berat sudah pasti terisi karena validasi di atas sudah lolos
        val beratKg = berat ?: return
        val hargaPerKg = LaundryConstants.hargaPerKg(jenis)

        val laundry = Laundry(
            id = if (sedangEdit) laundryId else 0,
            namaPelanggan = nama,
            nomorTelepon = telepon,
            jenisLaundry = jenis,
            berat = beratKg,
            hargaPerKg = hargaPerKg,
            totalHarga = LaundryConstants.hitungTotal(beratKg, hargaPerKg),
            tanggalMasuk = tanggalMasuk,
            tanggalSelesai = LaundryConstants.hitungTanggalSelesai(tanggalMasuk, jenis),
            status = status,
            catatan = catatan
        )

        // Mode edit memperbarui data, mode tambah menyimpan data baru
        val berhasil = if (sedangEdit) {
            db.updateLaundry(laundry) > 0
        } else {
            db.insertLaundry(laundry) != -1L
        }

        if (berhasil) {
            val pesanSukses = if (sedangEdit) R.string.edit_sukses else R.string.add_sukses
            Toast.makeText(this, pesanSukses, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            val pesanGagal = if (sedangEdit) R.string.edit_gagal else R.string.add_gagal
            Toast.makeText(this, pesanGagal, Toast.LENGTH_SHORT).show()
        }
    }
}
