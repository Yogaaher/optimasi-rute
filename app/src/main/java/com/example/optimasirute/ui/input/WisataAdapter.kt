package com.example.optimasirute.ui.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.optimasirute.R
import com.example.optimasirute.data.model.Wisata

class WisataAdapter(
    private val daftarWisata: List<Wisata>,
    initialSelectionIds: Set<String>
) : RecyclerView.Adapter<WisataAdapter.WisataViewHolder>() {

    private val selectedItems = mutableSetOf<Wisata>().apply {
        addAll(daftarWisata.filter { initialSelectionIds.contains(it.nama) })
    }

    private var currentTimeInMinutes: Int = 480

    fun updateCurrentTime(newTimeInMinutes: Int) {
        currentTimeInMinutes = newTimeInMinutes
        notifyDataSetChanged()
    }

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
        // Kirim waktu saat ini ke ViewHolder untuk logika tampilan
        holder.bind(wisata, currentTimeInMinutes)
    }

    override fun getItemCount(): Int = daftarWisata.size

    inner class WisataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tv_nama_wisata)
        private val tvJam: TextView = itemView.findViewById(R.id.tv_jam_operasional)
        private val tvDurasi: TextView = itemView.findViewById(R.id.tv_durasi)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_pilih)

        fun bind(wisata: Wisata, currentTime: Int) {
            tvNama.text = wisata.nama
            tvJam.text = "Buka: ${formatMinutes(wisata.buka)} - ${formatMinutes(wisata.tutup)}"
            tvDurasi.text = "Durasi Kunjungan: ${formatDurasi(wisata.durasi)}"

            val isPossibleToVisit = currentTime < wisata.tutup

            itemView.isEnabled = isPossibleToVisit
            itemView.alpha = if (isPossibleToVisit) 1.0f else 0.5f
            if (!isPossibleToVisit) {
                selectedItems.remove(wisata)
            }
            checkBox.isChecked = selectedItems.contains(wisata)

            itemView.setOnClickListener {
                if (!itemView.isEnabled) return@setOnClickListener

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