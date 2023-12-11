package cz.tomasbublik


data class Node(val left: String, val right: String)

fun main() {

    fun parseInput(input: List<String>): Pair<BooleanArray, Map<String, Pair<String, String>>> {
        val movements = input[0].map { c -> c == 'R' }.toBooleanArray()
        val desertMap = input.subList(2, input.size).associate { line ->
            line.substring(0..2) to Pair(line.substring(7..9), line.substring(12..14))
        }
        return Pair(movements, desertMap)
    }

    fun numSteps(
        node: String, movements: BooleanArray,
        desertMap: Map<String, Pair<String, String>>,
        part2: Boolean,
    ): Long {
        var steps = 0L
        var current = node
        while ((!part2 && current != "ZZZ") || (part2 && !current.endsWith("Z"))) {
            current = if (movements[(steps.mod(movements.size))]) {
                desertMap[current]!!.second
            } else {
                desertMap[current]!!.first
            }
            steps += 1
        }
        return steps
    }

    fun parseNodes(input: List<String>): Pair<Map<String, Node>, String> {
        val nodeMap = mutableMapOf<String, Node>()
        var firstNodeName = ""
        input.drop(1).forEachIndexed { index, line ->
            if (!line.contains("=")) return@forEachIndexed  // Přeskočíme instrukční řádky
            val (nodeName, nodeValues) = line.split("=").map { it.trim() }
            val (left, right) = nodeValues.removeSurrounding("(", ")").split(", ").map { it.trim() }
            if (index == 1) {
                firstNodeName = nodeName
            }
            nodeMap[nodeName] = Node(left, right)
        }
        return Pair(nodeMap, firstNodeName)
    }

    fun followInstructions(nodeMap: Pair<Map<String, Node>, String>, instructions: String): Int {
        var currentNodeName = nodeMap.second
        val nodeValue = nodeMap.first
        var instructionIndex = 0
        var stepNumber = 0
        while (currentNodeName != "ZZZ") {
            stepNumber++
            val currentNode = nodeValue[currentNodeName] ?: return -1
            val currentIndex = instructionIndex

            val direction = instructions[currentIndex]
            currentNodeName = if (direction == 'L') currentNode.left else currentNode.right
            instructionIndex++
            if (instructionIndex == instructions.length) instructionIndex = 0
        }
        return stepNumber
    }


    fun part1(input: List<String>): Long {
        val parsedNodes = parseNodes(input)
        val instructions = input.first().trim()
//        val numberOfStepsToZZZ = followInstructions(parsedNodes, instructions)
//        return numberOfStepsToZZZ
        val (movements, desertMap) = parseInput(input)
        return numSteps("AAA", movements, desertMap, false)
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_8_input_test")
    check(part1(testInput) == 6L)
//    check(part2(testInput) == 45000)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_8_input")
    //result is: 16897
    println(part1(input))
    println(part2(input))
}
