package cz.tomasbublik

fun main() {

    fun calculateTotal(data: List<Int>, times: Long): Long {
        var total = 0L
        for ((i, a) in data.withIndex()) {
            if (a == 0) continue
            var m = 0L
            for (j in i + 1..data.lastIndex) {
                val b = data[j]
                m += if (b == 0) times else 1
                total += m * a * b
            }
        }
        return total
    }

    fun calculateGalaxies(times: Long, input: List<String>): Long =
        calculateTotal(input.map { it.count('#'::equals) }, times) + calculateTotal(0.until(input.maxOfOrNull { it.length }
            ?: 0).map { x ->
            input.count { x < it.length && it[x] == '#' }
        }, times)


    fun part1(input: List<String>): Long {
        return calculateGalaxies(2, input)
    }

    fun part2(input: List<String>): Long {
        return calculateGalaxies(1_000_000, input)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_11_input_test")
    check(part1(testInput) == 374L)
    check(part2(testInput) == 82000210L) // Really?

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_11_input")
    println(part1(input))
    println(part2(input))
}
