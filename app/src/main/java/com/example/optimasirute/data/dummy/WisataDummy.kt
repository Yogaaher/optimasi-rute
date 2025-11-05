package com.example.optimasirute.data.dummy

import com.example.optimasirute.data.model.Wisata

object WisataDummy {

    // Daftar lengkap 30 wisata Semarang dan sekitarnya
    val daftarWisata = listOf(
        Wisata("Lawang Sewu", 0, 480, 1200, 75),          // 08:00 - 20:00, durasi 1.25 jam | Pusat Kota
        Wisata("Klenteng Sam Poo Kong", 1, 480, 1200, 90),// 08:00 - 20:00, durasi 1.5 jam  | Barat
        Wisata("Kota Lama Semarang", 2, 0, 1439, 90),     // 24 jam, durasi 1.5 jam          | Pusat Kota
        Wisata("Taman Bunga Celosia", 3, 480, 1020, 105), // 08:00 - 17:00, durasi 1.75 jam| Kab. Semarang (Selatan)
        Wisata("Saloka Theme Park", 4, 600, 1140, 210),   // 10:00 - 19:00, durasi 3.5 jam   | Kab. Semarang (Selatan)
        Wisata("Umbul Sidomukti", 5, 480, 1020, 150),     // 08:00 - 17:00, durasi 2.5 jam   | Kab. Semarang (Selatan)
        Wisata("Cimory on The Valley", 6, 540, 1200, 105),// 09:00 - 20:00, durasi 1.75 jam | Kab. Semarang (Selatan)
        Wisata("Kampoeng Wisata Taman Lele", 7, 540, 1020, 75), // 09:00 - 17:00, durasi 1.25 jam | Barat
        Wisata("Watu Gunung", 8, 480, 960, 75),           // 08:00 - 16:00, durasi 1.25 jam  | Kab. Semarang (Selatan)
        Wisata("Taman Pandanaran", 9, 0, 1439, 45),       // 24 jam, durasi 45 menit         | Pusat Kota

        Wisata("Masjid Agung Jawa Tengah", 10, 240, 1320, 60), // 04:00–22:00 | Timur
        Wisata("Pagoda Avalokitesvara", 11, 420, 1080, 60),    // 07:00–18:00 | Selatan
        Wisata("Goa Kreo", 12, 480, 1020, 90),                 // 08:00–17:00 | Selatan
        Wisata("Brown Canyon", 13, 360, 1080, 90),             // 06:00–18:00 | Timur
        Wisata("Curug Lawe Benowo", 14, 420, 960, 150),        // 07:00–16:00 | Kab. Semarang (Selatan)
        Wisata("Puri Maerokoco", 15, 420, 1080, 120),          // 07:00–18:00 | Utara
        Wisata("Hutan Tinjomoyo", 16, 420, 1020, 120),         // 07:00–17:00 | Selatan
        Wisata("Tugu Muda", 17, 0, 1439, 30),                  // 24 jam      | Pusat Kota
        Wisata("Pantai Marina", 18, 360, 1080, 75),            // 06:00–18:00 | Utara
        Wisata("Pantai Baruna", 19, 360, 1080, 60),            // 06:00–18:00 | Utara
        Wisata("Eling Bening", 20, 480, 1080, 120),            // 08:00–18:00 | Kab. Semarang (Selatan)
        Wisata("Dusun Semilir", 21, 540, 1140, 180),           // 09:00–19:00 | Kab. Semarang (Selatan)
        Wisata("Museum Ronggowarsito", 22, 480, 960, 90),      // 08:00–16:00 | Barat
        Wisata("Kampung Pelangi", 23, 420, 1020, 60),          // 07:00–17:00 | Selatan
        Wisata("Kampung Batik Semarang", 24, 540, 1020, 90),   // 09:00–17:00 | Timur
        Wisata("Puncak Telomoyo", 25, 300, 1020, 180),         // 05:00–17:00 | Kab. Semarang (Jauh Selatan)
        Wisata("Curug Semirang", 26, 480, 960, 120),           // 08:00–16:00 | Kab. Semarang (Selatan)
        Wisata("Bukit Cinta Rawa Pening", 27, 420, 1080, 90),  // 07:00–18:00 | Kab. Semarang (Selatan)
        Wisata("Pantai Tirang", 28, 360, 1080, 90),            // 06:00–18:00 | Barat
        Wisata("Semarang Zoo (Mangkang)", 29, 480, 1020, 120)  // 08:00–17:00 | Barat (Jauh)
    )

    // Matriks waktu tempuh yang diperluas untuk 30 lokasi (estimasi)
    val waktuTempuh = arrayOf(
        //   0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21  22  23  24  25  26  27  28  29
        intArrayOf(0,  15,  5, 60, 50, 55, 45, 20, 50, 5, 20, 25, 30, 35, 60, 15, 30, 2, 20, 25, 55, 40, 15, 20, 15, 80, 55, 50, 25, 30), // 0 Lawang Sewu
        intArrayOf(15, 0,  15, 65, 55, 60, 50, 10, 55, 15, 30, 30, 25, 45, 65, 20, 25, 15, 25, 30, 60, 45, 5,  20, 35, 85, 60, 55, 15, 20), // 1 Sam Poo Kong
        intArrayOf(5,  15, 0,  60, 50, 55, 45, 20, 50, 5, 15, 25, 30, 30, 60, 10, 30, 5,  15, 20, 55, 40, 15, 20, 10, 80, 55, 50, 25, 30), // 2 Kota Lama
        intArrayOf(60, 65, 60, 0,  25, 5,  15, 65, 5,  60, 70, 40, 45, 80, 15, 65, 45, 60, 70, 75, 20, 10, 65, 45, 70, 45, 10, 15, 70, 75), // 3 Celosia
        intArrayOf(50, 55, 50, 25, 0,  30, 10, 60, 30, 50, 60, 35, 40, 70, 35, 55, 40, 50, 60, 65, 10, 5,  55, 40, 60, 40, 30, 5,  60, 65), // 4 Saloka
        intArrayOf(55, 60, 55, 5,  30, 0,  20, 60, 5,  55, 65, 35, 40, 75, 10, 60, 40, 55, 65, 70, 20, 15, 60, 40, 65, 40, 5,  15, 65, 70), // 5 Umbul Sidomukti
        intArrayOf(45, 50, 45, 15, 10, 20, 0,  55, 20, 45, 55, 25, 30, 65, 25, 50, 30, 45, 55, 60, 15, 5,  50, 30, 55, 45, 20, 10, 55, 60), // 6 Cimory
        intArrayOf(20, 10, 20, 65, 60, 60, 55, 0,  55, 20, 35, 35, 30, 50, 65, 25, 30, 20, 30, 35, 65, 50, 10, 25, 40, 90, 60, 55, 10, 15), // 7 Taman Lele
        intArrayOf(50, 55, 50, 5,  30, 5,  20, 55, 0,  50, 60, 30, 35, 70, 10, 55, 35, 50, 60, 65, 15, 15, 55, 35, 60, 45, 5,  20, 60, 65), // 8 Watu Gunung
        intArrayOf(5,  15, 5,  60, 50, 55, 45, 20, 50, 0,  20, 25, 30, 35, 60, 15, 30, 2,  20, 25, 55, 40, 15, 20, 15, 80, 55, 50, 25, 30), // 9 Taman Pandanaran
        intArrayOf(20, 30, 15, 70, 60, 65, 55, 35, 60, 20, 0,  40, 45, 20, 70, 20, 45, 20, 25, 20, 65, 50, 30, 35, 5,  90, 65, 60, 35, 40), // 10 MAJT
        intArrayOf(25, 30, 25, 40, 35, 35, 25, 35, 30, 25, 40, 0,  15, 50, 40, 30, 10, 25, 35, 40, 35, 20, 30, 5,  40, 60, 30, 25, 35, 40), // 11 Pagoda
        intArrayOf(30, 25, 30, 45, 40, 40, 30, 30, 35, 30, 45, 15, 0,  55, 45, 35, 10, 30, 40, 45, 40, 25, 25, 10, 45, 65, 35, 30, 30, 35), // 12 Goa Kreo
        intArrayOf(35, 45, 30, 80, 70, 75, 65, 50, 70, 35, 20, 50, 55, 0,  80, 35, 55, 35, 40, 35, 75, 60, 45, 50, 15, 100,75, 70, 50, 55), // 13 Brown Canyon
        intArrayOf(60, 65, 60, 15, 35, 10, 25, 65, 10, 60, 70, 40, 45, 80, 0,  65, 45, 60, 70, 75, 25, 20, 65, 45, 70, 35, 10, 20, 70, 75), // 14 Curug Lawe
        intArrayOf(15, 20, 10, 65, 55, 60, 50, 25, 55, 15, 20, 30, 35, 35, 65, 0,  35, 15, 5,  10, 60, 45, 20, 25, 20, 85, 60, 55, 15, 20), // 15 Puri Maerokoco
        intArrayOf(30, 25, 30, 45, 40, 40, 30, 30, 35, 30, 45, 10, 10, 55, 45, 35, 0,  30, 40, 45, 40, 25, 25, 5,  45, 65, 35, 30, 30, 35), // 16 Tinjomoyo
        intArrayOf(2,  15, 5,  60, 50, 55, 45, 20, 50, 2,  20, 25, 30, 35, 60, 15, 30, 0,  20, 25, 55, 40, 15, 20, 15, 80, 55, 50, 25, 30), // 17 Tugu Muda
        intArrayOf(20, 25, 15, 70, 60, 65, 55, 30, 60, 20, 25, 35, 40, 40, 70, 5,  40, 20, 0,  5,  65, 50, 25, 30, 25, 90, 65, 60, 10, 15), // 18 Marina
        intArrayOf(25, 30, 20, 75, 65, 70, 60, 35, 65, 25, 20, 40, 45, 35, 75, 10, 45, 25, 5,  0,  70, 55, 30, 35, 20, 95, 70, 65, 15, 20), // 19 Baruna
        intArrayOf(55, 60, 55, 20, 10, 20, 15, 65, 15, 55, 65, 35, 40, 75, 25, 60, 40, 55, 65, 70, 0,  10, 60, 40, 65, 30, 20, 5,  65, 70), // 20 Eling Bening
        intArrayOf(40, 45, 40, 10, 5,  15, 5,  50, 15, 40, 50, 20, 25, 60, 20, 45, 25, 40, 50, 55, 10, 0,  45, 25, 50, 35, 15, 5,  50, 55), // 21 Dusun Semilir
        intArrayOf(15, 5,  15, 65, 55, 60, 50, 10, 55, 15, 30, 30, 25, 45, 65, 20, 25, 15, 25, 30, 60, 45, 0,  20, 35, 85, 60, 55, 15, 20), // 22 Ronggowarsito
        intArrayOf(20, 20, 20, 45, 40, 40, 30, 25, 35, 20, 35, 5,  10, 50, 45, 25, 5,  20, 30, 35, 40, 25, 20, 0,  35, 65, 35, 30, 30, 35), // 23 Kampung Pelangi
        intArrayOf(15, 35, 10, 70, 60, 65, 55, 40, 60, 15, 5,  40, 45, 15, 70, 20, 45, 15, 25, 20, 65, 50, 35, 35, 0,  90, 65, 60, 40, 45), // 24 Kampung Batik
        intArrayOf(80, 85, 80, 45, 40, 40, 45, 90, 45, 80, 90, 60, 65, 100,35, 85, 65, 80, 90, 95, 30, 35, 85, 65, 90, 0,  40, 30, 90, 95), // 25 Telomoyo
        intArrayOf(55, 60, 55, 10, 30, 5,  20, 60, 5,  55, 65, 30, 35, 75, 10, 60, 35, 55, 65, 70, 20, 15, 60, 35, 65, 40, 0,  15, 65, 70), // 26 Curug Semirang
        intArrayOf(50, 55, 50, 15, 5,  15, 10, 55, 20, 50, 60, 25, 30, 70, 20, 55, 30, 50, 60, 65, 5,  5,  55, 30, 60, 30, 15, 0,  60, 65), // 27 Bukit Cinta
        intArrayOf(25, 15, 25, 70, 60, 65, 55, 10, 60, 25, 35, 35, 30, 50, 70, 15, 30, 25, 10, 15, 65, 50, 15, 30, 40, 90, 65, 60, 0,  5 ), // 28 Pantai Tirang
        intArrayOf(30, 20, 30, 75, 65, 70, 60, 15, 65, 30, 40, 40, 35, 55, 75, 20, 35, 30, 15, 20, 70, 55, 20, 35, 45, 95, 70, 65, 5,  0 )  // 29 Semarang Zoo
    )
}