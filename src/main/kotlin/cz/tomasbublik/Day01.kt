package cz.tomasbublik

fun main() {

    fun getNumberFromWord(word: String): Int {
        val digits = word.filter { it.isDigit() }
        return if (digits.length >= 2) {
            ("" + digits.first() + digits.last()).toInt()
        } else if (digits.isNotEmpty()) {
            ("" + digits.first() + digits.first()).toInt()
        } else {
            0 // When there are no digits
        }
    }

    fun wordToDigit(word: String): Int? {
        return when (word) {
            "one" -> 1
            "two" -> 2
            "three" -> 3
            "four" -> 4
            "five" -> 5
            "six" -> 6
            "seven" -> 7
            "eight" -> 8
            "nine" -> 9
            else -> null
        }
    }

    fun containsDigit(str: String): Boolean {
        return str.any { it.isDigit() }
    }

    fun findNumberWord(str: String): Int? {
        val numberWordsPattern = "(one|two|three|four|five|six|seven|eight|nine|ten)".toRegex(RegexOption.IGNORE_CASE)
        val matchResult = numberWordsPattern.find(str)
//        println("In String " + str + " we've found this number: " + matchResult?.let { wordToDigit(it.value) })
//        println(" Replacing "+str+ "by this value: " )
        return matchResult?.let { wordToDigit(it.value) }
    }

    fun modifyWordForward(word: String): String {
        var modifiedWord = word
        var foundDigit = false
        var i = 0
        while (i < word.length && !foundDigit) {
            for (j in minOf(i + 3, word.length)..word.length) {
                val substring = word.substring(i, j)
                if (containsDigit(substring)) {
                    foundDigit = true
                    break
                }
                val digit: Int? = findNumberWord(substring)
                if (digit != null) {
                    modifiedWord = modifiedWord.replaceRange(i, j, digit.toString())
                    foundDigit = true
                    break
                }
            }
            i++
        }

        return modifiedWord
    }

    fun modifyWordBackward(word: String): String {
        var modifiedWord = word
        var foundDigit = false
        var i = word.length
        while (i >= 0 && !foundDigit) {
            for (j in maxOf(i - 3, 0) downTo 0) {
                val substring = word.substring(j, i)
                if (containsDigit(substring)) {
                    foundDigit = true
                    break
                }
                val digit: Int? = findNumberWord(substring)
                if (digit != null) {
                    modifiedWord = modifiedWord.replaceRange(j, i, digit.toString())
                    foundDigit = true
                    break
                }
            }
            i--
        }

        return modifiedWord
    }

    fun part1(input: List<String>): Int {
        val listOfNumbers: ArrayList<Int> = ArrayList()
        for (word in input) {
            val number = getNumberFromWord(word)
            listOfNumbers.add(number)
        }

        return listOfNumbers.sum()
    }

    fun part2(input: List<String>): Int {
        val listOfNumbers: ArrayList<Int> = ArrayList()
        val listOfModifiedNumbers: ArrayList<String> = ArrayList()
        for (word in input) {
            val number = modifyWordBackward(modifyWordForward(word))
            listOfModifiedNumbers.add(number)
        }
        for (word in listOfModifiedNumbers) {
            val number = getNumberFromWord(word)
            listOfNumbers.add(number)
        }
        return listOfNumbers.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/day_1_input_test")
//    check(part1(testInput) == 142)
    check(part2(testInput) == 281)

    val input = readFileAsLinesUsingUseLines("src/main/resources/day_1_input")
    println(part1(input))
    println(part2(input))
}
