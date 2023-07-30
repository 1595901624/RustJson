package com.rust.json.quick.rustjson

/**
 * Capitalize the first letter of a string
 */
fun String.toCapitalizeFirstLetter(): String {
    return this.substring(0, 1).uppercase() + this.substring(1)
}

/**
 * Judge whether a string is camel case
 */
fun String.isSnakeCase(): Boolean {
    if (this.any { it.isUpperCase() || it.isWhitespace() }) {
        return false
    }

    return !this.any { !it.isLetterOrDigit() && it != '_' }
}

/**
 * Convert CamelCase to snake_case
 */
fun String.convertCamelToSnakeCase(): String {
    val converted = StringBuilder()
    for (ch in this) {
        if (ch.isUpperCase()) {
            converted.append('_')
            converted.append(ch.lowercaseChar())
        } else {
            converted.append(ch)
        }
    }
    return converted.toString()
}