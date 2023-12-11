package cz.tomasbublik


data class Race(val time: Int, val recordDistance: Int)

fun main() {

    fun calculateBestRaceResult(races: List<Race>): List<Int> {
        return races.map { race ->
            var bestDistancesNumber = 0
            for (holdTime in 0 until race.time) {
                val travelTime = race.time - holdTime
                val speed = holdTime
                val distance = speed * travelTime
                if (distance > race.recordDistance) {
                    bestDistancesNumber++
                }
            }
            bestDistancesNumber
        }
    }

    fun parseRaces(input: String): List<Race> {
        val lines = input.lines()
        val times = lines[0].split(":")[1].trim().split("\\s+".toRegex()).map { it.toInt() }
        val distances = lines[1].split(":")[1].trim().split("\\s+".toRegex()).map { it.toInt() }

        return times.zip(distances) { time, distance ->
            Race(time, distance)
        }
    }

    fun multiplyList(numbers: List<Int>): Int {
        // Kontrola, zda je seznam prázdný
        if (numbers.isEmpty()) return 0

        // Použití reduce pro násobení všech hodnot
        return numbers.reduce { acc, i -> acc * i }
    }


    fun part1(input: List<String>): Int {
        val races = parseRaces(input.joinToString(separator = "\n"))
        println(races)

        val results = calculateBestRaceResult(races)
        println("Výsledky závodů: $results")

        return multiplyList(results)
    }

    fun part2(input: List<String>): Int {
        val races = input.map { line ->
            line.substringAfter(":").replace(" ", "").toLong()
        }
        return (1..<races[0]).count { buttonTime ->
            (buttonTime * (races[0] - buttonTime)) > races[1]
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_6_input_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_6_input")
    println(part1(input))
    println(part2(input))
}
