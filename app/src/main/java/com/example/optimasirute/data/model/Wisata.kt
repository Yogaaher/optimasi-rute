package com.example.optimasirute.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wisata(
    val nama: String,
    val index: Int,
    val buka: Int,
    val tutup: Int,
    val durasi: Int,
    val harga: Int,
    val hargaWeekend: Int?
) : Parcelable