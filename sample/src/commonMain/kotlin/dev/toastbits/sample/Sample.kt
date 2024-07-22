package dev.toastbits.sample

import dev.toastbits.kanakt.Kana

fun main() {
    val result = Kana.romanise("りゃーめんたべたいのう。")
    println(result.joinToString("") { it.reading ?: "" })
}
