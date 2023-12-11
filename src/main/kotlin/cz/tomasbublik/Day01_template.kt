package cz.tomasbublik


fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_1_input_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_1_input")
    println(part1(input))
    println(part2(input))
}

