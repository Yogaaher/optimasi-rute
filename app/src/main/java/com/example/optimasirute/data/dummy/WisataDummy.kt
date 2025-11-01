package com.example.optimasirute.data.dummy

import com.example.optimasirute.data.model.Wisata

object WisataDummy {
    val daftarWisata = listOf(
        Wisata("Selecta", 0, 420, 1020, 120), // 07:00 - 17:00, durasi 2 jam
        Wisata("Jatim Park 2", 1, 510, 990, 180), // 08:30 - 16:30, durasi 3 jam
        Wisata("Museum Angkut", 2, 720, 1200, 150), // 12:00 - 20:00, durasi 2.5 jam
        Wisata("Batu Night Spectacular", 3, 900, 1380, 120), // 15:00 - 23:00, durasi 2 jam
        Wisata("Coban Rondo", 4, 480, 960, 90) // 08:00 - 16:00, durasi 1.5 jam
    )

    // Matriks waktu tempuh dalam menit antar tempat wisata
    // Index sesuai dengan daftarWisata
    val waktuTempuh = arrayOf(
        //         Selecta, JTP 2, Museum, BNS, Coban Rondo
        intArrayOf(0,       25,    20,     15,  40), // From Selecta
        intArrayOf(25,      0,     5,      10,  35), // From Jatim Park 2
        intArrayOf(20,      5,     0,      5,   30), // From Museum Angkut
        intArrayOf(15,      10,    5,      0,   35), // From BNS
        intArrayOf(40,      35,    30,     35,  0)  // From Coban Rondo
    )
}