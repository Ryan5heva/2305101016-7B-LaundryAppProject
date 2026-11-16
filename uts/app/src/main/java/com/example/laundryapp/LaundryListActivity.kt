package com.example.laundryapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laundryapp.databinding.ActivityLaundryListBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Halaman daftar semua pesanan dengan pencarian dan filter.
 * Semua data diambil langsung dari SQLite melalui DatabaseHelper.searchLaundry().
 */
class LaundryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaundryListBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: LaundryAdapter

    // Kata pencarian dan filter yang sedang aktif
    private var keyword = ""
    private var filterStatus: String? = null   // null berarti semua status
    private var filterJenis: String? = null    // null berarti semua jenis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaundryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(applicationContext)

        setupRecyclerView()
        setupPencarian()
        setupFilter()
        binding.btnBack.setOnClickListener { finish() }
    }

    // Data dimuat ulang setiap halaman tampil, agar perubahan terbaru langsung terlihat
    override fun onResume() {
        super.onResume()
        muatData()
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        // Klik satu baris membuka halaman detail
        adapter = LaundryAdapter()
        binding.rvLaundry.layoutManager = LinearLayoutManager(this)
        binding.rvLaundry.adapter = adapter
    }

    // Setiap perubahan teks langsung memperbarui daftar
    private fun setupPencarian() {
        binding.etSearch.doAfterTextChanged { teks ->
            keyword = teks?.toString()?.trim().orEmpty()
            muatData()
        }
    }

    // Filter status dan jenis. Pilihan "Semua" berarti tidak difilter
    private fun setupFilter() {
        val semua = getString(R.string.filter_semua)

        isiChip(binding.cgStatus, listOf(semua) + LaundryConstants.STATUS_LIST) { pilihan ->
            filterStatus = if (pilihan == semua) null else pilihan
            muatData()
        }

        isiChip(binding.cgJenis, listOf(semua) + LaundryConstants.JENIS_LIST) { pilihan ->
            filterJenis = if (pilihan == semua) null else pilihan
            muatData()
        }
    }

    // Membuat chip dari daftar teks. Chip pertama dipilih secara default
    private fun isiChip(grup: ChipGroup, labels: List<String>, onPilih: (String) -> Unit) {
        labels.forEachIndexed { index, label ->
            val chip = Chip(this).apply {
                id = View.generateViewId()
                text = label
                isCheckable = true
                isChecked = index == 0
            }
            grup.addView(chip)
        }

        grup.setOnCheckedStateChangeListener { group, checkedIds ->
            val idTerpilih = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val label = group.findViewById<Chip>(idTerpilih).text.toString()
            onPilih(label)
        }
    }

    // Mengambil data sesuai kata pencarian dan filter, lalu memperbarui tampilan
    private fun muatData() {
        val hasil = db.searchLaundry(keyword, filterStatus, filterJenis)

        adapter.submitList(hasil)
        binding.tvJumlah.text = getString(R.string.list_jumlah, hasil.size)
        binding.tvEmpty.visibility = if (hasil.isEmpty()) View.VISIBLE else View.GONE
    }
}
