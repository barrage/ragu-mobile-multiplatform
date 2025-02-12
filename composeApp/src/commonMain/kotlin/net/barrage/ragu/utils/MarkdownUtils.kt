package net.barrage.ragu.utils

fun fixUnderscores(markdown: String): String {
    return markdown.replace(Regex("(_{3,})")) { matchResult ->
        matchResult.value.replace("_", "ˍ") // Inserts zero-width space after each underscore
    }
}