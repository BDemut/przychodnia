package com.example.demo.util

import java.lang.StringBuilder

fun String.toHtmlOption() = "<option>$this</option>"

fun List<String>.toHtmlOptions(): String {
    val builder = StringBuilder()
    forEach {
        builder.append(it.toHtmlOption())
    }
    return builder.toString()
}

fun List<String>.flatMap(): String {
    val builder = StringBuilder()
    forEach {
        builder.append("$it, ")
    }
    if (this.isNotEmpty())
        builder.delete(builder.length-2, builder.length)
    return builder.toString()
}