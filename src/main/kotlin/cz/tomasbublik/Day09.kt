package cz.tomasbublik

fun main() {

    fun nextValueOfHistory(history: List<Int>): Int {
        val lastNumbers = ArrayList<Int>()
        lastNumbers.add(history.last())

        var differences = history.zipWithNext { a, b -> b - a }

        while (differences.any { it != 0 }) {
            lastNumbers.add(differences.last())
            differences = differences.zipWithNext { a, b -> b - a }
        }

        var offset = 0
        for (lastNumber in lastNumbers.reversed()) {
            offset += lastNumber
        }

        return offset
    }

    fun sumOfExtrapolatedValues(histories: List<List<Int>>): Int {
        return histories.sumOf { nextValueOfHistory(it) }
    }

    fun part1(input: List<String>): Int {
        val histories = input.map { line ->
            line.trim().split("\\s+".toRegex()).map { it.toInt() }
        }

        return sumOfExtrapolatedValues(histories)
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_9_input_test")
    check(part1(testInput) == 114)
//    check(part2(testInput) == 45000)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_9_input")
    // returns 1798691765
    println(part1(input))
    println(part2(input))
}
