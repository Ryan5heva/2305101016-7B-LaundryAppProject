package com.example.laundryapp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.laundryapp.databinding.ItemLaundryBinding

/**
 * Adapter RecyclerView untuk menampilkan daftar pesanan.
 * Dipakai di Dashboard dan halaman daftar pesanan.
 * Klik satu baris akan membuka halaman detail.
 */
class LaundryAdapter : RecyclerView.Adapter<LaundryAdapter.LaundryViewHolder>() {

    private val items = mutableListOf<Laundry>()

    // Mengganti seluruh isi daftar dengan data baru
    fun submitList(newItems: List<Laundry>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class LaundryViewHolder(val binding: ItemLaundryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaundryViewHolder {
        val binding = ItemLaundryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LaundryViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LaundryViewHolder, position: Int) {
        val laundry = items[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvNamaPelanggan.text = laundry.namaPelanggan
            tvIdJenis.text = "${laundry.idFormatted()} • ${laundry.jenisLaundry}"
            tvTanggal.text = "Masuk: ${laundry.tanggalMasuk}"
            tvTotalHarga.text = LaundryConstants.formatRupiah(laundry.totalHarga)

            // Badge status berwarna sesuai status pesanan
            tvStatus.text = laundry.status
            tvStatus.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, statusColorRes(laundry.status))
            )

            root.setOnClickListener { bukaDetail(context, laundry.id) }
        }
    }

    companion object {
        // Warna badge untuk setiap status. Juga dipakai di halaman detail
        fun statusColorRes(status: String): Int = when (status) {
            LaundryConstants.STATUS_MENUNGGU -> R.color.status_menunggu
            LaundryConstants.STATUS_DIPROSES -> R.color.status_diproses
            LaundryConstants.STATUS_SELESAI -> R.color.status_selesai
            LaundryConstants.STATUS_DIAMBIL -> R.color.status_diambil
            else -> R.color.navy
        }

        // Membuka halaman detail untuk pesanan dengan ID tertentu
        fun bukaDetail(context: Context, laundryId: Int) {
            val intent = Intent(context, DetailLaundryActivity::class.java)
            intent.putExtra(DetailLaundryActivity.EXTRA_LAUNDRY_ID, laundryId)
            context.startActivity(intent)
        }
    }
}
