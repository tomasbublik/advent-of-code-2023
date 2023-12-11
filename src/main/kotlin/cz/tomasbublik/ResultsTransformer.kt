import cz.tomasbublik.readFileAsLinesUsingUseLines
import java.awt.Toolkit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun epochToIso8601(time: Long): String? {
    val format = "yyyy-MM-dd HH:mm:ss z"
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(time * 1000))
}

fun main() {

    // test if implementation meets criteria from the description, like:
    // val testInput = readFileAsJson("src/main/resources/2022_results.json")
    val testInput = readFileAsLinesUsingUseLines("src/main/resources/2023_results.json")
    for (line in testInput) {
        if (line.contains("get_star_ts")) {
            val split = line.split(":")
            val trimmed = split[1].trim()
            val testTimestamp =
                if (trimmed.contains(",")) {
                    trimmed.substring(0, split[1].length - 2).toLong()
                } else {
                    trimmed.toLong()
                }

            val startTimestamp = epochToIso8601(testTimestamp)

            println(split[0] + ": \"" + startTimestamp + "\",")
        } else {
            println(line)
        }
    }
}
