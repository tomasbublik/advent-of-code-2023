package cz.tomasbublik

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to cz.tomasbublik.md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun readFileAsLinesUsingUseLines(fileName: String): List<String> = File(fileName).useLines { it.toList() }
fun readFileAsJson(fileName: String): Results? {

    val json: String = File(fileName).readText(Charsets.UTF_8)

    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val jsonAdapter: JsonAdapter<Results> = moshi.adapter(Results::class.java)

    val results = jsonAdapter.fromJson(json)

    println(results)

    return results
}

data class Results(val event: String, val members: List<MemberId>) {
}

data class MemberId(val member: Member) {}
data class Member(
    val name: String,
    val completion_day_level: List<CompletionLevel>,
    val global_score: Int,
    val stars: Int,
    val id: String,
    val local_score: Int,
    val last_star_ts: String
) {}

data class CompletionLevel(val days: List<Day>)

data class Day(val stars: List<Star>)

data class Star(val get_star_ts: String, val star_index: String)