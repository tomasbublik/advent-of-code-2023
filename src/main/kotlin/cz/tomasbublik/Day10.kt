package cz.tomasbublik

import kotlin.math.absoluteValue

enum class DirectionEnum {
    NORTH, SOUTH, EAST, WEST
}

data class PipeType(val connections: Set<DirectionEnum>, val isStart: Boolean = false)


val pipeMapping = mapOf(
    '|' to PipeType(setOf(DirectionEnum.NORTH, DirectionEnum.SOUTH)),
    '-' to PipeType(setOf(DirectionEnum.EAST, DirectionEnum.WEST)),
    'L' to PipeType(setOf(DirectionEnum.NORTH, DirectionEnum.EAST)),
    'J' to PipeType(setOf(DirectionEnum.NORTH, DirectionEnum.WEST)),
    '7' to PipeType(setOf(DirectionEnum.SOUTH, DirectionEnum.WEST)),
    'F' to PipeType(setOf(DirectionEnum.SOUTH, DirectionEnum.EAST)),
    '.' to PipeType(emptySet()),
    'S' to PipeType(emptySet(), isStart = true) // Značí startovní pozici
)

val previousPositions = mutableListOf<Pair<Int, Int>>()
fun main() {

    fun parseMatrixFromFile(input: List<String>): List<List<PipeType>> {
        return input.map { line ->
            line.map { char -> pipeMapping[char] ?: throw IllegalArgumentException("Neznámý typ potrubí: $char") }
        }
    }

    fun getAvailableDirections(parsedMatrix: List<List<PipeType>>, i: Int, j: Int): List<Pair<Int, Int>> {
        val directions = mutableListOf<Pair<Int, Int>>()
        val currentPipe = parsedMatrix.getOrNull(i)?.getOrNull(j) ?: return emptyList()

        // Směr nahoru (Sever)
        if (i > 0 && DirectionEnum.SOUTH in parsedMatrix[i - 1][j].connections) {
            directions.add(Pair(i - 1, j))
        }
        // Směr dolů (Jih)
        if (i < parsedMatrix.size - 1 && DirectionEnum.NORTH in parsedMatrix[i + 1][j].connections) {
            directions.add(Pair(i + 1, j))
        }
        // Směr vlevo (Západ)
        if (j > 0 && DirectionEnum.EAST in parsedMatrix[i][j - 1].connections) {
            directions.add(Pair(i, j - 1))
        }
        // Směr vpravo (Východ)
        if (j < parsedMatrix[i].size - 1 && DirectionEnum.WEST in parsedMatrix[i][j + 1].connections) {
            directions.add(Pair(i, j + 1))
        }

        return directions
    }

    fun calculateFirstNextPosition(
        parsedMatrix: List<List<PipeType>>,
        currentPosition: Pair<Int, Int>
    ): Pair<Int, Int> {
        val availableDirections = getAvailableDirections(parsedMatrix, currentPosition.first, currentPosition.second)
        return availableDirections.first
    }

    fun isCompatible(nextPipe: PipeType, direction: DirectionEnum): Boolean {
        return when (direction) {
            DirectionEnum.NORTH -> DirectionEnum.SOUTH in nextPipe.connections || nextPipe.isStart
            DirectionEnum.SOUTH -> DirectionEnum.NORTH in nextPipe.connections || nextPipe.isStart
            DirectionEnum.EAST -> DirectionEnum.WEST in nextPipe.connections || nextPipe.isStart
            DirectionEnum.WEST -> DirectionEnum.EAST in nextPipe.connections || nextPipe.isStart
        }
    }

    fun getNextPosition(
        parsedMatrix: List<List<PipeType>>,
        i: Int,
        j: Int,
        previousI: Int,
        previousJ: Int
    ): Pair<Int, Int>? {
        val currentPipe = parsedMatrix.getOrNull(i)?.getOrNull(j) ?: return null

        // Sever
        if (i > 0 && (i - 1 != previousI || j != previousJ) && isCompatible(
                parsedMatrix[i - 1][j],
                DirectionEnum.NORTH
            ) && DirectionEnum.NORTH in currentPipe.connections
        ) {
            return Pair(i - 1, j)
        }
        // Jih
        if (i < parsedMatrix.size - 1 && (i + 1 != previousI || j != previousJ) && isCompatible(
                parsedMatrix[i + 1][j],
                DirectionEnum.SOUTH
            ) && DirectionEnum.SOUTH in currentPipe.connections
        ) {
            return Pair(i + 1, j)
        }
        // Východ
        if (j < parsedMatrix[i].size - 1 && (i != previousI || j + 1 != previousJ) && isCompatible(
                parsedMatrix[i][j + 1],
                DirectionEnum.EAST
            ) && DirectionEnum.EAST in currentPipe.connections
        ) {
            return Pair(i, j + 1)
        }
        // Západ
        if (j > 0 && (i != previousI || j - 1 != previousJ) && isCompatible(
                parsedMatrix[i][j - 1],
                DirectionEnum.WEST
            ) && DirectionEnum.WEST in currentPipe.connections
        ) {
            return Pair(i, j - 1)
        }

        return null
    }


    fun calculateNextPosition(
        parsedMatrix: List<List<PipeType>>,
        currentPosition: Pair<Int, Int>,
        pipeType: PipeType,
        previousPositions: List<Pair<Int, Int>>
    ): Pair<Int, Int> {
        if (pipeType.isStart) return calculateFirstNextPosition(parsedMatrix, currentPosition)
        val nextPosition = getNextPosition(
            parsedMatrix,
            currentPosition.first,
            currentPosition.second,
            previousPositions[previousPositions.size - 2].first,
            previousPositions[previousPositions.size - 2].second
        )
        return nextPosition!!
    }

    fun calculateNumberOfSteps(parsedMatrix: List<List<PipeType>>): Int {
        for ((i, line) in parsedMatrix.withIndex()) {
            for ((j, pipeType) in line.withIndex()) {
                if (!pipeType.isStart) continue
                val startingPosition = i to j
                //Místo toho si musíme ukládat pole předchozích pozic ne jen tu současnou, abychom se mohli vrátit
                // a budeme se dotazovat na last :-)

                previousPositions.add(startingPosition)
                var stepsNumber = 1
                var nextPosition = calculateNextPosition(parsedMatrix, startingPosition, pipeType, previousPositions)
                previousPositions.add(nextPosition)
                while (!parsedMatrix[nextPosition.first][nextPosition.second].isStart) {
                    nextPosition =
                        calculateNextPosition(
                            parsedMatrix,
                            nextPosition,
                            parsedMatrix[nextPosition.first][nextPosition.second],
                            previousPositions
                        )
                    previousPositions.add(nextPosition)
                    stepsNumber++
                }
                return stepsNumber / 2
            }
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        val parsedMatrix = parseMatrixFromFile(input)

        val numberOfSteps = calculateNumberOfSteps(parsedMatrix)

        return numberOfSteps
    }

    fun part2(input: List<String>): Int {
        return 1 + (previousPositions.asSequence().plus(previousPositions[0]).zipWithNext { (y0, x0), (y1, x1) ->
            x0 * y1 - x1 * y0
        }
            .sum().absoluteValue - previousPositions.size
                ) / 2
//        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_10_input_test")
    val testInput2 = readFileAsLinesUsingUseLines("src/main/resources/day_10_input_test2")
    check(part1(testInput) == 4)
    previousPositions.clear()
    check(part1(testInput2) == 8)
    check(part2(testInput) == 1)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_10_input")
    previousPositions.clear()
    println(part1(input))
    previousPositions.removeLast() // we remove the repeated first position
    println(part2(input))
}
