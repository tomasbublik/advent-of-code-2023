package cz.tomasbublik

fun main() {

    data class GridElement(val value: String)

    data class NumberWithCoordinates(val number: String, val i: Int, val j: Int)

    fun createGrid(input: List<String>): List<List<GridElement>> {
        return input.map { line ->
            line.map { char -> GridElement(char.toString()) }
        }
    }

    fun extractNumbers(grid: List<List<GridElement>>): List<NumberWithCoordinates> {
        val numbers = mutableListOf<NumberWithCoordinates>()

        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j].value[0].isDigit() && (j == 0 || !grid[i][j - 1].value[0].isDigit())) {
                    var k = j
                    val numberBuilder = StringBuilder()

                    while (k < grid[i].size && grid[i][k].value[0].isDigit()) {
                        numberBuilder.append(grid[i][k].value)
                        k++
                    }

                    if (numberBuilder.isNotEmpty()) {
                        numbers.add(NumberWithCoordinates(numberBuilder.toString(), i, j))
                    }
                }
            }
        }
        return numbers
    }

    fun isAdjacentToSpecialChar(grid: List<List<GridElement>>, i: Int, j: Int, length: Int): Boolean {
        val directions = listOf(-1, 0, 1)
        for (di in directions) {
            for (dj in directions) {
                if (di == 0 && dj == 0) continue // Skip the number itself
                for (k in 0 until length) {
                    val ni = i + di
                    val nj = (j + k) + dj
                    if (ni in grid.indices && nj in grid[ni].indices) {
                        val value = grid[ni][nj].value
                        if (value != "." && !value[0].isDigit()) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun sumOfAdjacentNumbers(grid: List<List<GridElement>>, numbers: List<NumberWithCoordinates>): Int {
        val filtered = numbers.filter { (number, i, j) ->
            isAdjacentToSpecialChar(grid, i, j, number.length)
        }
        return filtered.sumOf { it.number.toInt() }
    }

    fun part1(input: List<String>): Int {
        val grid = createGrid(input)
        val extractedNumbers = extractNumbers(grid)
        val sum = sumOfAdjacentNumbers(grid, extractedNumbers)
        println("Sum of numbers adjacent to special characters: $sum")

        return sum
    }

    fun findNumbersAdjacentToStars(
        grid: List<List<GridElement>>,
        numbers: List<NumberWithCoordinates>
    ): Map<String, List<String>> {
        val starPositions = mutableListOf<Pair<Int, Int>>()
        val numbersAdjacentToStars = mutableMapOf<String, MutableList<String>>()

        // Find all stars positions
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                if (grid[i][j].value == "*") {
                    starPositions.add(Pair(i, j))
                }
            }
        }

        // For each asterisk, find adjacent numbers
        starPositions.forEach { (starI, starJ) ->
            val adjacentNumbers = mutableListOf<String>()
            numbers.forEach { (number, i, j) ->
                // Is at least on -1 same or +1 line
                if (i == starI || i == starI - 1 || i == starI + 1) {
                    if ((starJ >= (j - 1)) && (starJ <= (j + number.length))) {
                        adjacentNumbers.add(number)
                    }
                }
            }
            numbersAdjacentToStars["($starI, $starJ)"] = adjacentNumbers
        }

        return numbersAdjacentToStars
    }

    fun part2(input: List<String>): Int {
        val grid = createGrid(input)

        val numbersAdjacentToStars = findNumbersAdjacentToStars(grid, extractNumbers(grid))
        numbersAdjacentToStars.forEach { (starPosition, numbers) ->
            println("Hvězdička na pozici $starPosition sousedí s čísly: $numbers")
        }

        return numbersAdjacentToStars.values
            .filter { it.size > 1 }
            .sumOf { numbers ->
                numbers.map { it.toInt() }
                    .reduce { acc, n -> acc * n }
            }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_3_input_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_3_input")
    println(part1(input))
    println(part2(input))
}
