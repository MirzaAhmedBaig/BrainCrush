package braincrush.mirza.com.braincrush.utils

import braincrush.mirza.com.braincrush.extensions.getExpressionResult
import braincrush.mirza.com.braincrush.extensions.getRandomOperator
import braincrush.mirza.com.braincrush.extensions.random

class RandomExpressionGenerator {
    private val operatorsArray = arrayOf('*', '+', '-')
    private var operatorsArrayValidator = arrayOf('+', '-', '/', '+', '-', '/', '*')
    private var inputArrayList = ArrayList<String>()
    private var expressionList = ArrayList<String>()
    private var numbersString = ""
    private var expression = ""
    private var result = 0
    private var isAdvanced = false

    constructor(isAdvanced: Boolean) {
        this.isAdvanced = isAdvanced
        init()
    }

    private fun init() {
        numbersString = ""
        expression = ""
        operatorsArrayValidator = if (isAdvanced) arrayOf('+', '-', '+', '-', '*', '*') else arrayOf('+', '-', '+', '-', '*')
        fillInputArray()
        getRandomizedNumbersFromInputArray()
        generateRandomizedExpression()
    }

    private fun fillInputArray() {
        inputArrayList = ArrayList()
        (0..5).forEach {
            val random = if (isAdvanced) (5..9).random().toString() else (1..5).random().toString()
            inputArrayList.add(random)
            numbersString += random

        }
    }

    private fun getRandomizedNumbersFromInputArray() {
        expressionList = ArrayList()
        while (numbersString.isNotEmpty()) {
            expressionList.add(getRandomNumberString())
        }

        expression.forEach {
            System.out.print("\t $it")
        }
    }

    private fun generateRandomizedExpression() {
        var isDivideDone = false
        expressionList.forEachIndexed { index, it ->
            if (index != expressionList.lastIndex) {

                if (!isDivideDone) {
                    when {
                        it.toInt() % expressionList[index + 1].toInt() == 0 -> {
                            isDivideDone = true
                            expression = expression + expressionList[index] + "/"

                        }
                        expressionList[index + 1].toInt() % it.toInt() == 0 -> {
                            val nextNumber = expressionList[index + 1]
                            expressionList[index + 1] = it
                            expressionList[index] = nextNumber
                            isDivideDone = true
                            expression = expression + expressionList[index] + "/"

                        }
                        else -> expression = expression + expressionList[index] + getOptimisticOperator().toString()
                    }
                } else {
                    expression = expression + expressionList[index] + getOptimisticOperator().toString()
                }
            }

        }
        expression += expressionList[expressionList.lastIndex]
        generateResult(expression)
    }

    fun getRandomizedArray(): ArrayList<String> {
        return expressionList
    }

    private fun getOptimisticOperator(): Char {
        var operator = getRandomOperator()
        return if (operatorsArrayValidator.contains(operator)) {
            operatorsArrayValidator[operatorsArrayValidator.indexOf(operator)] = '.'
            operator
        } else {
            while (!operatorsArrayValidator.contains(operator)) {
                operator = getRandomOperator()
            }
            operator
        }

    }

    private fun generateResult(exp: String) {
        result = getExpressionResult(exp).toInt()
    }

    private fun getRandomNumberString(): String {
        val randomCount = Math.min((1..2).random(), numbersString.lastIndex)
        var numberString = ""
        if (randomCount > 0) {
            (0 until randomCount).forEach {
                val random = (0..numbersString.lastIndex).random()
                numberString += numbersString.toCharArray()[random]
                numbersString = numbersString.removeRange(random, random + 1)
            }
        } else {
            numberString += numbersString
            numbersString = ""
        }
        return numberString
    }

    fun getResult(): Int {
        return result
    }

    fun getInputNumbersArray(): ArrayList<String> {
        return inputArrayList
    }

    fun getExpression(): String {
        return expression
    }

    fun getAllOperators(): Array<Char> {
        return operatorsArray
    }

    fun setIsAdavanced(isAdvanced: Boolean) {
        this.isAdvanced = isAdvanced
        init()
    }
}