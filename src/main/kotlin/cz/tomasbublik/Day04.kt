package cz.tomasbublik

fun main() {

    data class Card(val number: Int, val winningNumbers: List<Int>, val myNumbers: List<Int>)
    data class CardSummary(val cardIndex: Int, val numberOfMatchingNumbers: Int, val numberOfInstances: Int)


    fun processInput(input: List<String>): List<Card> {
        return input.map { line ->
            val (cardInfo, numbers) = line.split(":").map { it.trim() }
            val cardNumber = cardInfo.replace("Card", "").trim().toInt()
            val (winningNumbersStr, myNumbersStr) = numbers.split("|").map { it.trim() }
            val winningNumbers = winningNumbersStr.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
            val myNumbers = myNumbersStr.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
            Card(cardNumber, winningNumbers, myNumbers)
        }
    }

    fun calculateScore(card: Card): Int {
        val matchedNumbers = card.winningNumbers.intersect(card.myNumbers.toSet()).size
        return if (matchedNumbers > 0) 1.shl(matchedNumbers - 1) else 0
    }

    fun part1(input: List<String>): Int {
        val cards = processInput(input)

        val totalScores = cards.sumOf { calculateScore(it) }

        return totalScores
    }

    fun transformCards(cards: List<Card>): List<CardSummary> {
        return cards.mapIndexed { index, card ->
            val matchingNumbers = card.winningNumbers.intersect(card.myNumbers.toSet()).size
            CardSummary(cardIndex = index + 1, numberOfMatchingNumbers = matchingNumbers, numberOfInstances = 1)
        }
    }

    fun updateCardInstances(cardSummaries: MutableList<CardSummary>) {
        for (i in cardSummaries.indices) {
            val currentCard = cardSummaries[i]
            for (k in 1..currentCard.numberOfInstances) {
                if (currentCard.numberOfMatchingNumbers != 0) {
                    for (j in 1..currentCard.numberOfMatchingNumbers) {
                        val nextIndex = i + j
                        if (nextIndex < cardSummaries.size) {
                            val nextCard = cardSummaries[nextIndex]
                            cardSummaries[nextIndex] = nextCard.copy(numberOfInstances = nextCard.numberOfInstances + 1)
                        }
                    }
                }
            }
        }
    }

    fun part2(input: List<String>): Int {
        val cards = processInput(input)
        val cardSummaries = transformCards(cards)
        val cardSummariesMutable = cardSummaries.toMutableList()
        updateCardInstances(cardSummariesMutable)
        val totalNumberOfInstances = cardSummariesMutable.sumOf { it.numberOfInstances }
        return totalNumberOfInstances
    }

// test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_4_input_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_4_input")
    println(part1(input))
    println(part2(input))
}
