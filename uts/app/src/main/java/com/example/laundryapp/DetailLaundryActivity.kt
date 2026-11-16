package com.example.laundryapp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.laundryapp.databinding.ActivityDetailLaundryBinding

/**
 * Menampilkan rincian satu pesanan, serta aksi Edit, Hapus, dan Ubah Status.
 */
class DetailLaundryActivity : AppCompatActivity() {

    companion object {
        // Kunci untuk mengirim ID pesanan dari halaman lain
        const val EXTRA_LAUNDRY_ID = "extra_laundry_id"
    }

    private lateinit var binding: ActivityDetailLaundryBinding
    private lateinit var db: DatabaseHelper
    private var laundryId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLaundryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(applicationContext)
        laundryId = intent.getIntExtra(EXTRA_LAUNDRY_ID, -1)

        setupButtons()
    }

    // Data dimuat ulang setiap halaman tampil, sehingga perubahan dari halaman Edit langsung terlihat
    override fun onResume() {
        super.onResume()
        muatDetail()
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnKembali.setOnClickListener { finish() }
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, AddLaundryActivity::class.java)
            intent.putExtra(AddLaundryActivity.EXTRA_LAUNDRY_ID, laundryId)
            startActivity(intent)
        }
        binding.btnUbahStatus.setOnClickListener { pilihStatusBaru() }
        binding.btnHapus.setOnClickListener { konfirmasiHapus() }
    }

    // Mengambil data pesanan dari SQLite. Jika tidak ada, halaman ditutup
    private fun muatDetail() {
        if (laundryId == -1) return

        val data = db.getLaundryById(laundryId)
        if (data == null) {
            Toast.makeText(this, R.string.pesanan_tidak_ditemukan, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        tampilkan(data)
    }

    private fun tampilkan(data: Laundry) {
        binding.tvId.text = data.idFormatted()
        binding.tvNama.text = data.namaPelanggan

        binding.tvStatus.text = data.status
        binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, LaundryAdapter.statusColorRes(data.status))
        )

        binding.tvTelepon.text = data.nomorTelepon
        binding.tvJenis.text = data.jenisLaundry
        binding.tvBerat.text = "${LaundryConstants.formatBerat(data.berat)} kg"
        binding.tvHargaPerKg.text = LaundryConstants.formatRupiah(data.hargaPerKg)
        binding.tvTotal.text = LaundryConstants.formatRupiah(data.totalHarga)
        binding.tvTanggalMasuk.text = data.tanggalMasuk
        binding.tvEstimasi.text = data.tanggalSelesai
        binding.tvCatatan.text = data.catatan.ifBlank { "-" }
    }

    // Menampilkan daftar status. Pesanan langsung diperbarui setelah dipilih
    private fun pilihStatusBaru() {
        val daftarStatus: Array<CharSequence> = LaundryConstants.STATUS_LIST.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(R.string.ubah_status_judul)
            .setItems(daftarStatus) { _, which ->
                ubahStatus(LaundryConstants.STATUS_LIST[which])
            }
            .setNegativeButton(R.string.batal, null)
            .show()
    }

    // Menyimpan status baru ke SQLite dengan mengubah salinan data yang ada
    private fun ubahStatus(statusBaru: String) {
        val data = db.getLaundryById(laundryId) ?: return
        if (data.status == statusBaru) return

        db.updateLaundry(data.copy(status = statusBaru))
        Toast.makeText(this, getString(R.string.status_berhasil, statusBaru), Toast.LENGTH_SHORT).show()
        muatDetail()
    }

    // Meminta konfirmasi sebelum menghapus, agar tidak terhapus tidak sengaja
    private fun konfirmasiHapus() {
        AlertDialog.Builder(this)
            .setTitle(R.string.hapus_judul)
            .setMessage(R.string.hapus_pesan)
            .setPositiveButton(R.string.hapus_ya) { _, _ -> hapusPesanan() }
            .setNegativeButton(R.string.batal, null)
            .show()
    }

    private fun hapusPesanan() {
        db.deleteLaundry(laundryId)
        Toast.makeText(this, R.string.hapus_berhasil, Toast.LENGTH_SHORT).show()
        finish()
    }
}
