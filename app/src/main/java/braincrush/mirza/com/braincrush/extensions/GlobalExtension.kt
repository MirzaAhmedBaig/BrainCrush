package braincrush.mirza.com.braincrush.extensions

import net.objecthunter.exp4j.ExpressionBuilder
import java.util.*

fun ClosedRange<Int>.random() =
        Random().nextInt((endInclusive + 1) - start) + start

fun getRoundScore(progress: Int): Int {
    when {
        progress < 200 -> {
            return 5
        }
        progress < 400 -> {
            return 4
        }
        progress < 600 -> {
            return 3
        }
        progress < 800 -> {
            return 2
        }
        else -> {
            return 1
        }
    }
}

fun getPercentScore(percent: Int): Int {
    when {
        percent < 20 -> {
            return 1
        }
        percent < 40 -> {
            return 2
        }
        percent < 60 -> {
            return 3
        }
        percent < 80 -> {
            return 4
        }
        else -> {
            return 5
        }
    }
}

fun ArrayList<Int>.doRandom(): ArrayList<Int> {
    (0 until size).forEach {
        val randomIndex1 = (Math.random() * size).toInt()
        val randomIndex2 = (Math.random() * size).toInt()
        val randomElement = this[randomIndex1]
        this[randomIndex1] = this[randomIndex2]
        this[randomIndex2] = randomElement
    }
    return this
}

fun getExpressionResult(exp: String): Double {
    var value = 0.001
    try {
        value = ExpressionBuilder(exp).build().evaluate()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return value
}

val chars = arrayOf('+', '-', '*')
fun getRandomOperator(): Char {
    return chars[(0 until chars.size).random()]
}

val allChars = arrayOf('+', '-', '*', '/')
fun getRandomOperatorAll(): Char {
    return chars[(0 until chars.size).random()]
}

fun getRandomDividerPair(): Pair<Int, Int> {
    var randomNumberOne = (2..98).random()
    while (randomNumberOne % 2 != 0) {
        randomNumberOne = (2..98).random()
    }

    var randomNumberTwo = (2..98).random()
    while (randomNumberTwo % 2 != 0) {
        randomNumberTwo = (2..98).random()
    }
    return Pair(randomNumberOne, randomNumberTwo)
}

fun getRandomNumber(): Int {
    return (0..99).random()
}

fun getSmallRandomNumber(): Int {
    return (0..20).random()
}

fun isOperator(char: Char): Boolean {
    return char == '+' || char == '-' || char == '/' || char == '*'
}