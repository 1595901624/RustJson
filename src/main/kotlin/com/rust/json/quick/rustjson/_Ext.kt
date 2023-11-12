package com.rust.json.quick.rustjson

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.rust.json.quick.rustjson.rust.RUST_KEYWORDS

/**
 * Capitalize the first letter of a string
 */
fun String.toCapitalizeFirstLetter(): String {
    return this.substring(0, 1).uppercase() + this.substring(1)
}

///**
// * Judge whether a string is camel case
// */
//fun String.isSnakeCase(): Boolean {
//    if (this.any { it.isUpperCase() || it.isWhitespace() }) {
//        return false
//    }
//
//    return !this.any { !it.isLetterOrDigit() && it != '_' }
//}

/**
 * Convert CamelCase to snake_case
 */
fun String.convertCamelToSnakeCase(): String {
    val converted = StringBuilder()
    this.forEachIndexed { index, c ->
        if (c.isUpperCase()) {
            if (index != 0) {
                converted.append('_')
            }
            converted.append(c.lowercaseChar())
        } else {
            converted.append(c)
        }
    }
    return converted.toString()
}

/**
 * format json
 */
fun String.formatJson(): String {
    runCatching {
        val gson = GsonBuilder().setPrettyPrinting().setLenient().create()
        val jsonElement: JsonElement = JsonParser.parseString(this)
        return gson.toJson(jsonElement)
    }
    return this
}

/**
 * is default rust keyword
 */
fun String.isDefaultRustKeyword(): Boolean {
    return RUST_KEYWORDS.contains(this)
}

/**
 * is digit
 */
fun String.isDigit(): Boolean {
    return this.all { it.isDigit() }
}