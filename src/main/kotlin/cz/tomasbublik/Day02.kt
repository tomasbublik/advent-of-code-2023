package cz.tomasbublik

fun main() {

    data class Drawning(val red: Int, val green: Int, val blue: Int)
    data class GameRound(val number: Int, val drawnings: List<Drawning>)

    val gameRegex = "Game (\\d+): (.+?)(?=Game|$)".toRegex(RegexOption.IGNORE_CASE)
    val drawningRegex = "(\\d+) (red|green|blue)".toRegex()

    fun extractGames(
        input: List<String>,
        gameRegex: Regex,
        drawningRegex: Regex
    ): ArrayList<GameRound> {
        val games: ArrayList<GameRound> = ArrayList()

        for (line in input) {
            val gameRound = gameRegex.findAll(line).map { matchResult ->
                val gameNumber = matchResult.groupValues[1].toInt()
                val gameContent = matchResult.groupValues[2]

                val drawningList = gameContent.split(";").map { drawningString ->
                    val counts = mutableMapOf("red" to 0, "green" to 0, "blue" to 0)
                    drawningRegex.findAll(drawningString).forEach {
                        val (count, color) = it.destructured
                        counts[color] = counts[color]!! + count.toInt()
                    }
                    Drawning(counts["red"]!!, counts["green"]!!, counts["blue"]!!)
                }
                GameRound(gameNumber, drawningList)
            }

            games.addAll(gameRound)
        }
        return games
    }

    fun part1(input: List<String>): Int {
        val games: ArrayList<GameRound> = extractGames(input, gameRegex, drawningRegex)

        return games.filter { gameRound ->
            gameRound.drawnings.all { drawing ->
                drawing.red <= 12 && drawing.green <= 13 && drawing.blue <= 14
            }
        }.sumOf { it.number }
    }

    fun part2(input: List<String>): Int {
        val games: ArrayList<GameRound> = extractGames(input, gameRegex, drawningRegex)

        games.forEach { game ->
            val maxRed = game.drawnings.maxOfOrNull { it.red } ?: 0
            val maxGreen = game.drawnings.maxOfOrNull { it.green } ?: 0
            val maxBlue = game.drawnings.maxOfOrNull { it.blue } ?: 0

            println("Game ${game.number}: Max Red = $maxRed, Max Green = $maxGreen, Max Blue = $maxBlue")
        }
        return games.sumOf { game ->
            val maxRed = game.drawnings.maxOfOrNull { it.red } ?: 1
            val maxGreen = game.drawnings.maxOfOrNull { it.green } ?: 1
            val maxBlue = game.drawnings.maxOfOrNull { it.blue } ?: 1

            maxRed * maxGreen * maxBlue
        }

    }


    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_2_input_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_2_input")
    println(part1(input))
    println(part2(input))
}
