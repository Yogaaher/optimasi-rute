package com.example.optimasirute.data.dummy

import com.example.optimasirute.data.model.Wisata

object WisataDummy {

    // Daftar wisata Semarang berdasarkan data yang Anda berikan
    val daftarWisata = listOf(
        Wisata("Lawang Sewu", 0, 480, 1200, 75), // 08:00 - 20:00, durasi 1.25 jam
        Wisata("Klenteng Sam Poo Kong", 1, 480, 1200, 90), // 08:00 - 20:00, durasi 1.5 jam
        Wisata("Kota Lama Semarang", 2, 0, 1439, 90), // Buka 24 jam (00:00 - 23:59), durasi 1.5 jam
        Wisata("Taman Bunga Celosia", 3, 480, 1020, 105), // 08:00 - 17:00, durasi 1.75 jam
        Wisata("Saloka Theme Park", 4, 600, 1140, 210), // 10:00 - 19:00, durasi 3.5 jam
        Wisata("Umbul Sidomukti", 5, 480, 1020, 150), // 08:00 - 17:00, durasi 2.5 jam
        Wisata("Cimory on The Valley", 6, 540, 1200, 105), // 09:00 - 20:00, durasi 1.75 jam
        Wisata("Kampoeng Wisata Taman Lele", 7, 540, 1020, 75), // 09:00 - 17:00, durasi 1.25 jam
        Wisata("Watu Gunung", 8, 480, 960, 75), // 08:00 - 16:00, durasi 1.25 jam
        Wisata("Taman Pandanaran", 9, 0, 1439, 45)  // Buka 24 jam (00:00 - 23:59), durasi 45 menit
    )

    // Estimasi matriks waktu tempuh dalam menit antar lokasi di Semarang dan sekitarnya
    // Urutan: LS, SPK, KL, Celosia, Saloka, Umbul, Cimory, Tmn Lele, Watu G, Tmn Pandanaran
    val waktuTempuh = arrayOf(
        //         0   1   2   3   4   5   6   7   8   9
        intArrayOf(0,  15,  10, 45, 50, 40, 35, 20, 35, 5 ), // 0 Lawang Sewu
        intArrayOf(15, 0,   20, 55, 60, 50, 45, 10, 45, 15), // 1 Sam Poo Kong
        intArrayOf(10, 20,  0,  50, 55, 45, 40, 25, 40, 10), // 2 Kota Lama
        intArrayOf(45, 55,  50, 0,  25, 5,  15, 60, 10, 50), // 3 Celosia
        intArrayOf(50, 60,  55, 25, 0,  30, 10, 65, 35, 55), // 4 Saloka
        intArrayOf(40, 50,  45, 5,  30, 0,  20, 55, 5,  45), // 5 Umbul Sidomukti
        intArrayOf(35, 45,  40, 15, 10, 20, 0,  50, 25, 40), // 6 Cimory
        intArrayOf(20, 10,  25, 60, 65, 55, 50, 0,  50, 20), // 7 Taman Lele
        intArrayOf(35, 45,  40, 10, 35, 5,  25, 50, 0,  40), // 8 Watu Gunung
        intArrayOf(5,  15,  10, 50, 55, 45, 40, 20, 40, 0 )  // 9 Taman Pandanaran
    )
}