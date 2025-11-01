package com.example.optimasirute.algorithm

import com.example.optimasirute.data.model.Wisata
import kotlin.random.Random

// Data class baru untuk menyimpan satu baris jadwal
data class ItineraryItem(
    val placeName: String,
    val arrivalTime: String,
    val visitStartTime: String,
    val visitEndTime: String,
    val travelToNextMinutes: Int? = null,
    val waitTimeMinutes: Int = 0
)

// Modifikasi data class hasil agar bisa membawa list jadwal
data class OptimizationResult(
    val bestRoute: List<Wisata>,
    val totalMinutes: Int,
    val fitness: Double,
    val itinerary: List<ItineraryItem> // TAMBAHAN
)

class EvolutionStrategies(
    private val selectedWisata: List<Wisata>,
    private val travelTimeMatrix: Array<IntArray>,
    private val startTimeInMinutes: Int = 480 // 08:00
) {

    private data class Chromosome(val route: List<Wisata>, var fitness: Double = 0.0)

    private fun formatMinutes(minutes: Double): String {
        val hours = (minutes / 60).toInt()
        val mins = (minutes % 60).toInt()
        return String.format("%02d:%02d", hours, mins)
    }

    fun run(): OptimizationResult {
        if (selectedWisata.isEmpty()) {
            return OptimizationResult(emptyList(), 0, 0.0, emptyList())
        }
        if (selectedWisata.size == 1) {
            val place = selectedWisata.first()
            val start = maxOf(startTimeInMinutes, place.buka)
            val end = start + place.durasi
            val itinerary = listOf(
                ItineraryItem(
                    place.nama,
                    formatMinutes(startTimeInMinutes.toDouble()),
                    formatMinutes(start.toDouble()),
                    formatMinutes(end.toDouble())
                )
            )
            return OptimizationResult(selectedWisata, end - startTimeInMinutes, 1.0, itinerary)
        }

        var population = List(90) { Chromosome(selectedWisata.shuffled()) }
        var bestChromosome: Chromosome? = null

        repeat(100) {
            population.forEach { it.fitness = calculateFitnessAndItinerary(it.route).second }
            population = population.sortedByDescending { it.fitness }
            val elite = population.first()

            if (bestChromosome == null || elite.fitness > bestChromosome!!.fitness) {
                bestChromosome = elite
            }

            val newPopulation = mutableListOf(elite)
            while (newPopulation.size < 90) {
                newPopulation.add(Chromosome(mutate(elite.route)))
            }
            population = newPopulation
        }

        val finalBest = population.maxByOrNull { it.fitness } ?: bestChromosome!!
        val (totalTime, fitness, itinerary) = calculateFitnessAndItinerary(finalBest.route)

        return OptimizationResult(finalBest.route, totalTime, fitness, itinerary)
    }

    private fun mutate(route: List<Wisata>): List<Wisata> {
        val mutableRoute = route.toMutableList()
        val pos1 = Random.nextInt(mutableRoute.size)
        var pos2 = Random.nextInt(mutableRoute.size)
        while (pos1 == pos2) {
            pos2 = Random.nextInt(mutableRoute.size)
        }
        val temp = mutableRoute[pos1]
        mutableRoute[pos1] = mutableRoute[pos2]
        mutableRoute[pos2] = temp
        return mutableRoute
    }

    private fun calculateFitnessAndItinerary(route: List<Wisata>): Triple<Int, Double, List<ItineraryItem>> {
        var currentTime = startTimeInMinutes.toDouble()
        var totalTravelTime = 0.0
        var penalty = 0.0
        val itinerary = mutableListOf<ItineraryItem>()

        for (i in route.indices) {
            val currentPlace = route[i]
            var arrivalTime = currentTime
            var waitTime = 0.0

            if (currentTime < currentPlace.buka) {
                waitTime = currentPlace.buka - currentTime
                currentTime = currentPlace.buka.toDouble()
            }

            penalty += waitTime

            if (currentTime > currentPlace.tutup) {
                penalty += 10000
                continue
            }

            val visitStartTime = currentTime
            currentTime += currentPlace.durasi
            val visitEndTime = currentTime

            var travelToNext = 0
            if (i < route.size - 1) {
                val nextPlace = route[i + 1]
                travelToNext = travelTimeMatrix[currentPlace.index][nextPlace.index]
                totalTravelTime += travelToNext
                currentTime += travelToNext
            }

            itinerary.add(
                ItineraryItem(
                    placeName = currentPlace.nama,
                    arrivalTime = formatMinutes(arrivalTime),
                    visitStartTime = formatMinutes(visitStartTime),
                    visitEndTime = formatMinutes(visitEndTime),
                    travelToNextMinutes = if (travelToNext > 0) travelToNext else null,
                    waitTimeMinutes = waitTime.toInt()
                )
            )
        }

        val totalCost = totalTravelTime + penalty
        val totalTimeWithWait = (currentTime - startTimeInMinutes).toInt()
        val fitness = 1.0 / (totalCost + 1.0)

        return Triple(totalTimeWithWait, fitness, itinerary)
    }
}