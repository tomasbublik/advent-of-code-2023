package cz.tomasbublik

data class CamelCardHand(val hand: String, val bid: Int)

private const val REPLACEMENT = "23456789TQKA"
private const val J = 'J'

fun main() {

    fun getHandValue(hand: String, part2: Boolean = false): Int {
        return hand.map { card ->
            when (card) {
                'A' -> 14
                'K' -> 13
                'Q' -> 12
                J -> if (part2) 1 else 11
                'T' -> 10
                else -> card.digitToInt()
            }
        }.fold(0) { acc, cardVal -> 15 * acc + cardVal }
    }

    fun calculateHandRank(hand: String): Int {
        val occurrences = mutableMapOf<Char, Int>()
        hand.forEach { card ->
            occurrences[card] = occurrences.getOrDefault(card, 0) + 1
        }
        if (occurrences.any { entry -> entry.value == 5 }) {
            return 6 // Five of a kind
        } else if (occurrences.any { entry -> entry.value == 4 }) {
            return 5 // Four of a kind
        } else if (occurrences.any { entry -> entry.value == 3 } && occurrences.any { entry -> entry.value == 2 }) {
            return 4 // Full house
        } else if (occurrences.any { entry -> entry.value == 3 }) {
            return 3 // Three of a kind
        } else if (occurrences.count { entry -> entry.value == 2 } == 2) {
            return 2 // Two pairs
        } else if (occurrences.any { entry -> entry.value == 2 }) {
            return 1 // One pair
        }
        return 0 // High card
    }

    fun getHandType(hand: String): Int {

        return calculateHandRank(hand)
    }

    fun getHandType2(hand: String): Int {
        if (J in hand) {
            return REPLACEMENT.maxOf { replacement ->
                getHandType2(hand.replaceFirst(J, replacement))
            }
        }

        return calculateHandRank(hand)
    }

    fun getTotalWinnings(input: List<String>): Int {
        val handsAndBids = input.map { line -> line.split(" ") }.map { (a, b) -> Pair(a, b.toInt()) }
        val comparator = Comparator<Pair<String, Int>> { handBid1, handBid2 ->
            val hand1 = handBid1.first
            val hand2 = handBid2.first
            if (getHandType(hand1) != getHandType(hand2)) {
                return@Comparator getHandType(hand1) - getHandType(hand2)
            }
            return@Comparator getHandValue(hand1) - getHandValue(hand2)
        }
        return handsAndBids.sortedWith(comparator).withIndex().sumOf { indexedValue ->
            (indexedValue.index + 1) * (indexedValue.value.second)
        }
    }

    fun getTotalWinnings2(input: List<String>): Int {
        val handsAndBids = input.map { line -> line.split(" ") }.map { (a, b) -> Pair(a, b.toInt()) }
        val comparator = Comparator<Pair<String, Int>> { handBid1, handBid2 ->
            val hand1 = handBid1.first
            val hand2 = handBid2.first
            if (getHandType2(hand1) != getHandType2(hand2)) {
                return@Comparator getHandType2(hand1) - getHandType2(hand2)
            }
            return@Comparator getHandValue(hand1, true) - getHandValue(hand2, true)
        }
        return handsAndBids.sortedWith(comparator).withIndex().sumOf { indexedValue ->
            (indexedValue.index + 1) * (indexedValue.value.second)
        }
    }

    fun parseCamelCardHands(input: List<String>): List<CamelCardHand> {
        return input.map { line ->
            val (hand, bid) = line.split(" ")
            CamelCardHand(hand, bid.toInt())
        }
    }

    fun part1(input: List<String>): Int {
        val camelCardHands = parseCamelCardHands(input)
        camelCardHands.forEach { println(it) }

        return getTotalWinnings(input)
    }

    fun part2(input: List<String>): Int {
        return getTotalWinnings2(input)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_7_input_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_7_input")
    println(part1(input))
    println(part2(input))
}
