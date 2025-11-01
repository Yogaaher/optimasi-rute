package com.example.optimasirute.ui.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.optimasirute.R
import com.example.optimasirute.data.model.Wisata

class WisataAdapter(private val daftarWisata: List<Wisata>, initialSelectionIds: Set<String> ) :
    RecyclerView.Adapter<WisataAdapter.WisataViewHolder>() {

    private val selectedItems = mutableSetOf<Wisata>().apply {
        addAll(daftarWisata.filter { initialSelectionIds.contains(it.nama) })
    }

    // Fungsi untuk mendapatkan daftar wisata yang dipilih
    fun getSelectedWisata(): List<Wisata> {
        return selectedItems.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WisataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wisata_input, parent, false)
        return WisataViewHolder(view)
    }

    override fun onBindViewHolder(holder: WisataViewHolder, position: Int) {
        val wisata = daftarWisata[position]
        holder.bind(wisata)
    }

    override fun getItemCount(): Int = daftarWisata.size

    inner class WisataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tv_nama_wisata)
        private val tvJam: TextView = itemView.findViewById(R.id.tv_jam_operasional)
        private val tvDurasi: TextView = itemView.findViewById(R.id.tv_durasi)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_pilih)

        fun bind(wisata: Wisata) {
            tvNama.text = wisata.nama
            tvJam.text = "Buka: ${formatMinutes(wisata.buka)} - ${formatMinutes(wisata.tutup)}"
            tvDurasi.text = "Durasi Kunjungan: ${formatDurasi(wisata.durasi)}"

            // Atur status checkbox berdasarkan data yang tersimpan
            checkBox.isChecked = selectedItems.contains(wisata)

            // Tambahkan listener klik pada seluruh item view
            itemView.setOnClickListener {
                if (checkBox.isChecked) {
                    selectedItems.remove(wisata)
                    checkBox.isChecked = false
                } else {
                    selectedItems.add(wisata)
                    checkBox.isChecked = true
                }
            }
        }

        private fun formatMinutes(minutes: Int): String {
            val hours = minutes / 60
            val mins = minutes % 60
            return String.format("%02d:%02d", hours, mins)
        }

        private fun formatDurasi(minutes: Int): String {
            if (minutes < 60) return "$minutes menit"
            val hours = minutes / 60
            val mins = minutes % 60
            return if (mins == 0) "$hours jam" else "$hours jam $mins menit"
        }
    }
}