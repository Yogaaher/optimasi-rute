package com.example.optimasirute.algorithm

import com.example.optimasirute.data.model.Wisata
import java.util.Collections
import kotlin.random.Random

data class ItineraryItem(
    val placeName: String,
    val placePrice: Int,
    val arrivalTime: String,
    val visitStartTime: String,
    val visitEndTime: String,
    val travelToNextMinutes: Int? = null,
    val waitTimeMinutes: Int = 0
)

data class OptimizationResult(
    val bestRoute: List<Wisata>,
    val totalMinutes: Int,
    val fitness: Double,
    val itinerary: List<ItineraryItem>,
    val isValid: Boolean,
    val finalPopulationCount: Int,
    val totalGenerations: Int,
    val finalDiversity: Int,
    val executionTimeMillis: Long
)

class GeneticAlgorithm(
    private val selectedWisata: List<Wisata>,
    private val travelTimeMatrix: Array<IntArray>,
    private val startTimeInMinutes: Int,
    private val isWeekend: Boolean,
    private val startPoint: Wisata?,
    private val populationSize: Int = 100,
    private val generations: Int = 200,
    private var mutationRate: Double = 0.1,
    private val crossoverRate: Double = 0.8,
    private val tournamentSize: Int = 5
) {

    private data class Chromosome(val route: List<Wisata>, var fitness: Double = 0.0)

    fun run(): OptimizationResult {
        // Mulai timer
        val startTime = System.currentTimeMillis()

        if (selectedWisata.isEmpty()) {
            val endTime = System.currentTimeMillis()
            return OptimizationResult(emptyList(), 0, 0.0, emptyList(), false, 0, 0, 0, endTime - startTime)
        }
        if (selectedWisata.size == 1) {
            val place = selectedWisata.first()
            val start = maxOf(startTimeInMinutes, place.buka)
            val end = start + place.durasi
            val isValid = startTimeInMinutes < place.tutup
            val itinerary = if (isValid) listOf(
                ItineraryItem(
                    placeName = place.nama,
                    placePrice = place.harga,
                    arrivalTime = formatMinutes(startTimeInMinutes.toDouble()),
                    visitStartTime = formatMinutes(start.toDouble()),
                    visitEndTime = formatMinutes(end.toDouble())
                )
            ) else emptyList()
            val endTime = System.currentTimeMillis()
            return OptimizationResult(selectedWisata, end - startTimeInMinutes, 1.0, itinerary, isValid, 1, 1, 1, endTime - startTime)
        }

        var population = createInitialPopulation()
        var bestChromosomeOverall: Chromosome? = null

        repeat(generations) { currentGeneration ->
            population.forEach { it.fitness = calculateFitness(it.route).second }
            population = population.sortedByDescending { it.fitness }.toMutableList()

            val bestInCurrentGen = population.first()
            if (bestChromosomeOverall == null || bestInCurrentGen.fitness > bestChromosomeOverall!!.fitness) {
                bestChromosomeOverall = bestInCurrentGen
            }

            checkDiversityAndAdjustMutation(population)

            val newPopulation = mutableListOf(bestInCurrentGen)

            while (newPopulation.size < populationSize) {
                val parent1 = modifiedTournamentSelection(population)
                val parent2 = modifiedTournamentSelection(population)

                val (child1, child2) = if (Random.nextDouble() < crossoverRate) {
                    copyOrderCrossover(parent1, parent2)
                } else {
                    Pair(parent1, parent2)
                }

                enhancedSwapMutation(child1, mutationRate)
                enhancedSwapMutation(child2, mutationRate)

                newPopulation.add(child1)
                if (newPopulation.size < populationSize) {
                    newPopulation.add(child2)
                }
            }
            population = newPopulation
        }

        val finalBest = bestChromosomeOverall!!
        val (totalTime, fitness, itinerary) = calculateFitness(finalBest.route)
        val isRouteValid = itinerary.isNotEmpty()
        val uniqueIndividuals = population.distinctBy { it.route }.size

        // Hentikan timer
        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime

        return OptimizationResult(
            finalBest.route,
            totalTime,
            fitness,
            itinerary,
            isRouteValid,
            population.size,
            generations,
            uniqueIndividuals,
            executionTime
        )
    }

    private fun createInitialPopulation(): MutableList<Chromosome> {
        return MutableList(populationSize) {
            val route: List<Wisata>
            if (startPoint != null) {
                val remainingWisata = selectedWisata.filter { it != startPoint }
                route = listOf(startPoint) + remainingWisata.shuffled()
            } else {
                route = selectedWisata.shuffled()
            }
            Chromosome(route)
        }
    }

    private fun modifiedTournamentSelection(population: List<Chromosome>): Chromosome {
        val tournamentGroup = population.shuffled().take(tournamentSize)
        val bestInTournament = tournamentGroup.maxByOrNull { it.fitness }!!

        val allIdentical = tournamentGroup.all { it.route == tournamentGroup.first().route }
        if (allIdentical && population.size > tournamentSize) {
            val bestOutside = population.filterNot { tournamentGroup.contains(it) }.maxByOrNull { it.fitness }
            if (bestOutside != null && Random.nextBoolean()) {
                return bestOutside
            }
        }
        return bestInTournament
    }

    private fun copyOrderCrossover(parent1: Chromosome, parent2: Chromosome): Pair<Chromosome, Chromosome> {
        val size = parent1.route.size
        if (size < 2) return Pair(parent1, parent2)

        val p1 = parent1.route
        val p2 = parent2.route

        val offspring1 = MutableList<Wisata?>(size) { null }
        val offspring2 = MutableList<Wisata?>(size) { null }

        val crossoverStart = if (startPoint != null) 1 else 0
        if (size <= crossoverStart) return Pair(parent1, parent2)

        val start = Random.nextInt(crossoverStart, size)
        val end = Random.nextInt(start, size)

        val middle1 = p1.subList(start, end + 1)
        val middle2 = p2.subList(start, end + 1)

        for (i in start..end) {
            offspring1[i] = middle1[i - start]
            offspring2[i] = middle2[i - start]
        }

        var currentP2Index = 0
        var currentP1Index = 0

        for (i in (0 until start) + (end + 1 until size)) {
            while (middle1.contains(p2[currentP2Index])) {
                currentP2Index = (currentP2Index + 1) % size
            }
            offspring1[i] = p2[currentP2Index]
            currentP2Index = (currentP2Index + 1) % size

            while (middle2.contains(p1[currentP1Index])) {
                currentP1Index = (currentP1Index + 1) % size
            }
            offspring2[i] = p1[currentP1Index]
            currentP1Index = (currentP1Index + 1) % size
        }

        @Suppress("UNCHECKED_CAST")
        return Pair(Chromosome(offspring1 as List<Wisata>), Chromosome(offspring2 as List<Wisata>))
    }

    private fun enhancedSwapMutation(chromosome: Chromosome, rate: Double) {
        if (Random.nextDouble() >= rate) return

        val route = chromosome.route.toMutableList()
        val size = route.size
        val mutationStart = if (startPoint != null) 1 else 0
        if (size <= mutationStart) return

        val mutationCount = 2
        repeat(mutationCount) {
            val pos1 = Random.nextInt(mutationStart, size)
            val pos2 = Random.nextInt(mutationStart, size)
            if (pos1 != pos2) {
                Collections.swap(route, pos1, pos2)
            }
        }
    }

    private fun checkDiversityAndAdjustMutation(population: List<Chromosome>) {
        val uniqueCount = population.distinctBy { it.route }.size
        val diversityRatio = uniqueCount.toDouble() / population.size

        if (diversityRatio < 0.2) {
            mutationRate = minOf(0.9, mutationRate + 0.1)
        } else {
            mutationRate = 0.1
        }
    }

    private fun formatMinutes(minutes: Double): String {
        val hours = (minutes / 60).toInt()
        val mins = (minutes % 60).toInt()
        return String.format("%02d:%02d", hours, mins)
    }

    private fun calculateFitness(route: List<Wisata>): Triple<Int, Double, List<ItineraryItem>> {
        var currentTime = this.startTimeInMinutes.toDouble()
        var totalTravelTime = 0.0
        var penalty = 0.0
        val itinerary = mutableListOf<ItineraryItem>()

        for (i in route.indices) {
            val currentPlace = route[i]
            var arrivalTime = currentTime
            var waitTime = 0.0

            if (currentTime >= currentPlace.tutup) {
                break
            }

            if (currentTime < currentPlace.buka) {
                waitTime = currentPlace.buka - currentTime
                currentTime = currentPlace.buka.toDouble()
            }

            penalty += waitTime

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
                    placePrice = if (this.isWeekend && currentPlace.hargaWeekend != null) currentPlace.hargaWeekend else currentPlace.harga,
                    arrivalTime = formatMinutes(arrivalTime),
                    visitStartTime = formatMinutes(visitStartTime),
                    visitEndTime = formatMinutes(visitEndTime),
                    travelToNextMinutes = if (travelToNext > 0) travelToNext else null,
                    waitTimeMinutes = waitTime.toInt()
                )
            )
        }

        val totalCost = totalTravelTime + penalty
        val totalTimeWithWait = if (itinerary.isNotEmpty()) (currentTime - startTimeInMinutes).toInt() else 0
        val fitness = 1.0 / (totalCost + 1.0)

        return Triple(totalTimeWithWait, fitness, itinerary)
    }
}