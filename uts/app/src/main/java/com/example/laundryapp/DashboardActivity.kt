package com.example.laundryapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.databinding.ActivityDashboardBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dashboard utama. Semua angka statistik dihitung langsung dari SQLite.
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: LaundryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(applicationContext)
        binding.tvDate.text = formatTanggalHariIni()

        setupRecyclerView()
        setupButtons()
    }

    // Dipanggil setiap layar tampil kembali, sehingga angka selalu sesuai database
    override fun onResume() {
        super.onResume()
        muatDataDashboard()
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        // Klik satu baris membuka halaman detail
        adapter = LaundryAdapter()
        binding.rvRecent.layoutManager = LinearLayoutManager(this)
        binding.rvRecent.adapter = adapter
    }

    // Tombol tambah membuka form pesanan baru. Lihat Semua membuka daftar pesanan
    private fun setupButtons() {
        binding.btnLogout.setOnClickListener { keluar() }
        binding.btnLihatSemua.setOnClickListener {
            startActivity(Intent(this, LaundryListActivity::class.java))
        }
        binding.fabTambah.setOnClickListener {
            startActivity(Intent(this, AddLaundryActivity::class.java))
        }
    }

    // Mengambil statistik dan lima pesanan terbaru dari database
    private fun muatDataDashboard() {
        binding.tvTotal.text = db.getTotalLaundryCount().toString()

        // Menghubungkan setiap TextView dengan status yang dihitungnya
        val kartuStatus = listOf(
            binding.tvMenunggu to LaundryConstants.STATUS_MENUNGGU,
            binding.tvDiproses to LaundryConstants.STATUS_DIPROSES,
            binding.tvSelesai to LaundryConstants.STATUS_SELESAI,
            binding.tvDiambil to LaundryConstants.STATUS_DIAMBIL
        )
        kartuStatus.forEach { (textView, status) ->
            textView.text = db.getLaundryCountByStatus(status).toString()
        }

        val pesananTerbaru = db.getAllLaundry().take(5)
        adapter.submitList(pesananTerbaru)
        binding.tvEmpty.visibility = if (pesananTerbaru.isEmpty()) View.VISIBLE else View.GONE
    }

    // Menghapus sesi dan kembali ke halaman login, tanpa bisa kembali dengan tombol Back
    private fun keluar() {
        getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // Tanggal hari ini dalam bahasa Indonesia, contoh: Kamis, 12 November 2026
    private fun formatTanggalHariIni(): String {
        val format = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        return format.format(Date())
    }
}
