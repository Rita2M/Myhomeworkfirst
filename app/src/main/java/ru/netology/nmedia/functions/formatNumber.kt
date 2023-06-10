package ru.netology.nmedia.functions

fun formatNumber(number: Int): String {
    return when {
        number < 1000 -> number.toString()
        number < 10000 -> {
            val integer = number / 1000 // 1
            val fractional = (number % 1000) / 100
            if (fractional > 0) {
                "$integer.$fractional" + "K"
            } else {
                "$integer" + "K"
            }
        }

        number < 1000000 -> (number / 1000).toString() + "K"
        else -> {
            val integer = number / 1000000
            val fractional = (number % 1000000) / 100000
            if (fractional > 0) {
                "$integer.$fractional" + "M"
            } else "$integer" + "M"
        }

    }
}
