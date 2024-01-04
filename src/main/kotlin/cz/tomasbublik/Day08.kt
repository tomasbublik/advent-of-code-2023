package cz.tomasbublik


fun main() {

    fun parseInput(input: List<String>): Pair<BooleanArray, Map<String, Pair<String, String>>> {
        val instructions = input[0].map { c -> c == 'R' }.toBooleanArray()
        val desertMap = input.subList(2, input.size).associate { line ->
            line.substring(0..2) to Pair(line.substring(7..9), line.substring(12..14))
        }
        return Pair(instructions, desertMap)
    }

    fun isPart1ZZZ(part2: Boolean, current: String) = (!part2 && current != "ZZZ")

    fun isPart2Z(part2: Boolean, current: String) = (part2 && !current.endsWith("Z"))

    fun followInstructions(
        node: String, movements: BooleanArray,
        desertMap: Map<String, Pair<String, String>>,
        part2: Boolean,
    ): Long {
        var steps = 0L
        var current = node
        while (isPart1ZZZ(part2, current) || isPart2Z(part2, current)) {
            current = if (movements[(steps.mod(movements.size))]) { // Possibility of overflow?
                desertMap[current]!!.second
            } else {
                desertMap[current]!!.first
            }
            steps++
        }
        return steps
    }

    fun part1(input: List<String>): Long {
        val (movements, desertMap) = parseInput(input)
        return followInstructions("AAA", movements, desertMap, false)
    }

    fun filterInstructions(
        desertMap: Map<String, Pair<String, String>>,
        movements: BooleanArray
    ) = desertMap.keys.filter { node -> node.endsWith("A") }.map { node ->
        followInstructions(node, movements, desertMap, true)
    }

    fun leastCommonMultiple(one: Long, second: Long): Long {
        val biggerValue = maxOf(one, second)
        val limit = one * second
        var leastCommonMultiple = biggerValue
        while (leastCommonMultiple <= limit) {
            if (leastCommonMultiple.mod(one) == 0L && leastCommonMultiple.mod(second) == 0L) {
                return leastCommonMultiple
            }
            leastCommonMultiple += biggerValue
        }
        return leastCommonMultiple
    }

    fun part2(input: List<String>): Long {
        val (movements, desertMap) = parseInput(input)
        return filterInstructions(desertMap, movements).reduce(::leastCommonMultiple)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_8_input_test")
    check(part1(testInput) == 6L)
    check(part2(testInput) == 6L)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_8_input")
    println(part1(input))
    println(part2(input))
}
