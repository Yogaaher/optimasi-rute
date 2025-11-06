package com.example.optimasirute.ui.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.optimasirute.R
import com.example.optimasirute.data.model.Wisata
import java.text.NumberFormat
import java.util.Locale

class WisataAdapter(
    private val daftarWisata: List<Wisata>,
    initialSelectionIds: Set<String>,
    private val onSelectionChanged: () -> Unit // Callback
) : RecyclerView.Adapter<WisataAdapter.WisataViewHolder>() {

    // Menyimpan item yang dipilih pengguna
    private val selectedItems = mutableSetOf<Wisata>().apply {
        addAll(daftarWisata.filter { initialSelectionIds.contains(it.nama) })
    }

    // Variabel untuk menyimpan state filter
    private var currentTimeInMinutes: Int = 480 // Default 08:00

    private val selectionOrder = mutableListOf<Wisata>()

    private val affordabilityMap = mutableMapOf<Wisata, Boolean>()

    // Fungsi ini dipanggil dari Fragment untuk memperbarui waktu
    fun updateCurrentTime(newTimeInMinutes: Int) {
        currentTimeInMinutes = newTimeInMinutes
        notifyDataSetChanged()
    }

    // Fungsi ini dipanggil dari Fragment untuk memperbarui status budget
    fun updateBudgetAndAvailability(budget: Long, isBudgetFeatureActive: Boolean) {
        affordabilityMap.clear()

        if (isBudgetFeatureActive) {
            val currentTotal = selectedItems.sumOf { it.harga }
            val remainingBudget = budget - currentTotal

            daftarWisata.forEach { wisata ->
                val canAfford = if (!selectedItems.contains(wisata)) {
                    wisata.harga <= remainingBudget
                } else {
                    true
                }
                affordabilityMap[wisata] = canAfford
            }
        }
        notifyDataSetChanged()
    }

    fun getSelectedWisata(): List<Wisata> {
        return selectedItems.toList()
    }

    fun removeLastSelectedItem() {
        if (selectionOrder.isNotEmpty()) {
            val lastItem = selectionOrder.removeAt(selectionOrder.lastIndex)
            selectedItems.remove(lastItem)
            notifyDataSetChanged()
        }
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
        private val tvHarga: TextView = itemView.findViewById(R.id.tv_harga)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_pilih)

        fun bind(wisata: Wisata) {
            // 1. Tampilkan semua data
            tvNama.text = wisata.nama
            tvJam.text = "Buka: ${formatMinutes(wisata.buka)} - ${formatMinutes(wisata.tutup)}"
            tvDurasi.text = "Durasi Kunjungan: ${formatDurasi(wisata.durasi)}"
            tvHarga.text = "Harga: ${formatRupiah(wisata.harga)}"

            // 2. Tentukan apakah item bisa dipilih (cek waktu & budget)
            val isPossibleByTime = currentTimeInMinutes < wisata.tutup
            val isPossibleByBudget = affordabilityMap[wisata] ?: true
            val isEnabled = isPossibleByTime && isPossibleByBudget

            // 3. Atur tampilan berdasarkan status
            itemView.isEnabled = isEnabled
            itemView.alpha = if (isEnabled) 1.0f else 0.5f

            // 4. Jika item menjadi tidak bisa dipilih, hapus dari seleksi
            if (!isEnabled) {
                selectedItems.remove(wisata)
            }
            checkBox.isChecked = selectedItems.contains(wisata)

            // 5. Atur listener klik
            itemView.setOnClickListener {
                if (!itemView.isEnabled) return@setOnClickListener

                if (checkBox.isChecked) {
                    selectedItems.remove(wisata)
                    selectionOrder.remove(wisata)
                    checkBox.isChecked = false
                } else {
                    selectedItems.add(wisata)
                    selectionOrder.add(wisata)
                    checkBox.isChecked = true
                }
                // Beri tahu Fragment bahwa ada perubahan seleksi
                onSelectionChanged()
            }
        }

        // --- Fungsi Helper ---
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

        private fun formatRupiah(number: Int): String {
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.maximumFractionDigits = 0
            return numberFormat.format(number)
        }
    }
}