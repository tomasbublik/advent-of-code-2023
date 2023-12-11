@file:OptIn(ExperimentalStdlibApi::class)

package cz.tomasbublik

import kotlin.math.max
import kotlin.math.min

data class MappingStructure(
    val seeds: List<Long>,
    val seedToSoil: List<Triple<Long, Long, Long>>,
    val soilToFertilizer: List<Triple<Long, Long, Long>>,
    val fertilizerToWater: List<Triple<Long, Long, Long>>,
    val waterToLight: List<Triple<Long, Long, Long>>,
    val lightToTemperature: List<Triple<Long, Long, Long>>,
    val temperatureToHumidity: List<Triple<Long, Long, Long>>,
    val humidityToLocation: List<Triple<Long, Long, Long>>
)

fun main() {

    fun parseMappingData(input: List<String>): MappingStructure {
        val seeds = input.first().split(": ")[1].split(" ").map { it.toLong() }

        fun extractSection(startIndex: Int): List<Triple<Long, Long, Long>> {
            val triples = mutableListOf<Triple<Long, Long, Long>>()
            var i = startIndex + 1
            while (i < input.size && input[i].isNotBlank()) {
                val (a, b, c) = input[i].split(" ").filter { it.isNotBlank() }.map { it.toLong() }
                triples.add(Triple(a, b, c))
                i++
            }
            return triples
        }

        val seedToSoilIndex = input.indexOfFirst { it.contains("seed-to-soil map") }
        val soilToFertilizerIndex = input.indexOfFirst { it.contains("soil-to-fertilizer map") }
        val fertilizerToWaterIndex = input.indexOfFirst { it.contains("fertilizer-to-water map") }
        val waterToLightIndex = input.indexOfFirst { it.contains("water-to-light map") }
        val lightToTemperatureIndex = input.indexOfFirst { it.contains("light-to-temperature map") }
        val temperatureToHumidityIndex = input.indexOfFirst { it.contains("temperature-to-humidity map") }
        val humidityToLocationIndex = input.indexOfFirst { it.contains("humidity-to-location map") }

        return MappingStructure(
            seeds = seeds,
            seedToSoil = extractSection(seedToSoilIndex),
            soilToFertilizer = extractSection(soilToFertilizerIndex),
            fertilizerToWater = extractSection(fertilizerToWaterIndex),
            waterToLight = extractSection(waterToLightIndex),
            lightToTemperature = extractSection(lightToTemperatureIndex),
            temperatureToHumidity = extractSection(temperatureToHumidityIndex),
            humidityToLocation = extractSection(humidityToLocationIndex),
        )
    }

    fun calculateMapping(mappingDefinitionList: List<Triple<Long, Long, Long>>, seed: Long): Long {
        for (triple in mappingDefinitionList) {
            if (seed >= triple.second && seed <= (triple.second + triple.third)) {
                return triple.first + (seed - triple.second)
            }
        }
        return seed
    }

    fun getLocationFromSeed(
        mappingStructure: MappingStructure,
        seed: Long
    ): Long {
        val seedToSoil: Long = calculateMapping(mappingStructure.seedToSoil, seed)
        val soilToFertilizer: Long = calculateMapping(mappingStructure.soilToFertilizer, seedToSoil)
        val fertilizerToWater: Long = calculateMapping(mappingStructure.fertilizerToWater, soilToFertilizer)
        val waterToLight: Long = calculateMapping(mappingStructure.waterToLight, fertilizerToWater)
        val lightToTemperature: Long = calculateMapping(mappingStructure.lightToTemperature, waterToLight)
        val temperatureToHumidity: Long = calculateMapping(mappingStructure.temperatureToHumidity, lightToTemperature)
        val humidityToLocation: Long = calculateMapping(mappingStructure.humidityToLocation, temperatureToHumidity)
        return humidityToLocation
    }

    fun getLowestLocationNumberFromRanges(mappingStructure: MappingStructure, seeds: List<LongRange>): Long {
        var lowestLocationNumber = Long.MAX_VALUE
        var i = 0
//        var previousValue = -1L
        for (seedRange in seeds) {
            println("Processing seedRange no.: $i")
            for (seed in seedRange) {
                // test if we get a different value from the first map, otherwise it makes no sense to repeat the same search
//                val currentValue = calculateMapping(mappingStructure.seedToSoil, seed)

//                if (currentValue != previousValue) {
                    val currentLocation = getLocationFromSeed(mappingStructure, seed)
                    if (currentLocation < lowestLocationNumber) {
                        lowestLocationNumber = currentLocation
                    }
//                    previousValue = currentValue
//                }
            }
            i++
        }
        return lowestLocationNumber
    }

    fun getLowestLocationNumber(mappingStructure: MappingStructure, seeds: List<Long>): Long {
        var lowestLocationNumber = getLocationFromSeed(
            mappingStructure, seeds.first()
        )
        for (seed in seeds) {
            val currentLocation =
                getLocationFromSeed(mappingStructure, seed)
            if (currentLocation < lowestLocationNumber) {
                lowestLocationNumber = currentLocation
            }
        }
        return lowestLocationNumber
    }

    fun generateSeedRanges(seeds: List<Long>): List<LongRange> {
        val result = mutableListOf<LongRange>()

        for (i in seeds.indices step 2) {
            val start = seeds[i]
            val count = seeds[i + 1]

            result.add(start until (start + count))
        }

        return result
    }


    fun getNextRanges(
        seedRanges: List<LongRange>,
        level: Int,
        mapRanges: Array<List<Triple<Long, Long, Long>>>,
    ): List<LongRange> {
        val result = mutableListOf<LongRange>()

        val notMapped = mapRanges[level].fold(seedRanges) { remaining, (destStart, seedStart, length) ->
            val nextRemaining = mutableListOf<LongRange>()
            val destOffset = destStart - seedStart
            remaining.forEach { seedRange ->
                val intersect = max(seedStart, seedRange.first)..min(seedStart + length - 1, seedRange.last)
                if (intersect.isEmpty()) {
                    nextRemaining.addLast(seedRange)
                } else {
                    result.addLast((destOffset + intersect.first)..(destOffset + intersect.last))
                    nextRemaining.add(seedRange.first..<intersect.first) // Left of intersection
                    nextRemaining.add((intersect.last + 1)..seedRange.last) // Right of intersection
                }
            }
            nextRemaining.filter { range -> !range.isEmpty() }
        }

        result.addAll(notMapped)
        return result
    }

    fun getMinLocation(seedRanges: List<LongRange>, inputParts: List<List<String>>): String {
        val mapRanges: Array<List<Triple<Long, Long, Long>>> = Array(inputParts.size - 1) { mutableListOf() }
        (1..<inputParts.size).forEach { i ->
            (1..<inputParts[i].size).forEach { j ->
                val (destStart, seedStart, length) = inputParts[i][j].split(" ").map { str -> str.toLong() }
                mapRanges[i - 1].addLast(Triple(destStart, seedStart, length))
            }
        }

        return mapRanges.indices.fold(seedRanges) { currentRanges, level ->
            getNextRanges(currentRanges, level, mapRanges)
        }.minOf { range -> range.first }.toString()
    }


    fun part1(input: List<String>): Long {
        val mappingStructure = parseMappingData(input)
        println(mappingStructure)

        val lowestLocationNumber = getLowestLocationNumber(mappingStructure, mappingStructure.seeds)

        return lowestLocationNumber
    }

    fun part2(input: List<String>): String {

        val inputParts = input.joinToString("\n")
            .split("\n\n")
            .map { block -> block.split("\n") }
        val seedRanges = inputParts[0][0].substringAfter("seeds: ")
            .split(" ")
            .map { str -> str.toLong() }
            .chunked(2)
            .map { (rangeStart, rangeLength) -> rangeStart..<(rangeStart + rangeLength) }

        return getMinLocation(seedRanges, inputParts)


        /*val mappingStructure = parseMappingData(input)

        val listOfSeedsRanges = generateSeedRanges(mappingStructure.seeds)
        println("What is the number seed ranges?: " + listOfSeedsRanges.size)
        val lowestLocationNumber = getLowestLocationNumberFromRanges(mappingStructure, listOfSeedsRanges)

        return lowestLocationNumber*/
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_5_input_test")
    check(part1(testInput) == 35.toLong())
    check(part2(testInput) == "46")

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_5_input")
    println(part1(input))
    // The result was: 20358600
    println(part2(input))

}
