package com.ivan.work.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private lateinit var btn0: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private lateinit var btnMultiply: Button
    private lateinit var btnDivide: Button
    private lateinit var btnDecimal: Button
    private lateinit var btnClear: Button
    private lateinit var btnEqual: Button
    private val numbersStack = Stack<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)
        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)
        btnMultiply = findViewById(R.id.btnMultiply)
        btnDivide = findViewById(R.id.btnDivide)
        btnDecimal = findViewById(R.id.btnDecimal)
        btnClear = findViewById(R.id.btnClear)
        btnEqual = findViewById(R.id.btnEquals)

        btn0.setOnClickListener { appendToResult("0") }
        btn1.setOnClickListener { appendToResult("1") }
        btn2.setOnClickListener { appendToResult("2") }
        btn3.setOnClickListener { appendToResult("3") }
        btn4.setOnClickListener { appendToResult("4") }
        btn5.setOnClickListener { appendToResult("5") }
        btn6.setOnClickListener { appendToResult("6") }
        btn7.setOnClickListener { appendToResult("7") }
        btn8.setOnClickListener { appendToResult("8") }
        btn9.setOnClickListener { appendToResult("9") }
        btnPlus.setOnClickListener { appendToResult("+") }
        btnMinus.setOnClickListener { appendToResult("-") }
        btnMultiply.setOnClickListener { appendToResult("*") }
        btnDivide.setOnClickListener { appendToResult("/") }
        btnDecimal.setOnClickListener { appendToResult(".") }
        btnClear.setOnClickListener { tvResult.text = "0" }
        btnEqual.setOnClickListener { evaluateExpression() }
    }

    private fun appendToResult(text: String) {
        if (tvResult.text == "0") {
            tvResult.text = text
        } else {
            tvResult.append(text)
        }

        // Check if the text is a digit or a decimal point
        if (text.all { it.isDigit() } || text == ".") {
            // Parse the number and push it to the numbers stack
            val expression = tvResult.text.toString()
            var number = 0.0
            var i = expression.lastIndex
            while (i >= 0 && (expression[i].isDigit() || expression[i] == '.')) {
                if (expression[i] == '.') {
                    // The decimal point is encountered, parse the fractional part of the number
                    number /= 10.0
                } else {
                    number += (expression[i] - '0') * Math.pow(10.0, expression.lastIndex - i.toDouble())
                }
                i--
            }
            numbersStack.push(number)
        }
    }


    private fun evaluateExpression() {
        val expression = tvResult.text.toString()

        // Create stacks for numbers and operators
        val numbersStack = Stack<Double>()
        val operatorsStack = Stack<Char>()

        // Define a function to get the precedence of an operator
        fun getPrecedence(operator: Char): Int {
            return when (operator) {
                '*', '/' -> 2
                '+', '-' -> 1
                else -> throw IllegalArgumentException("Invalid operator")
            }
        }

        // Convert the expression to postfix notation using the shunting yard algorithm
        var i = 0
        while (i < expression.length) {
            val c = expression[i]
            if (c.isDigit() || c == '.') {
                // If the character is a digit or decimal point, parse the number and push it to the numbers stack
                var numberStr = ""
                while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                    numberStr += expression[i]
                    i++
                }
                val number = numberStr.toDouble()
                numbersStack.push(number)
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                // If the character is an operator, pop operators from the operators stack until the top operator has lower precedence,
                // then push the new operator to the operators stack
                val precedence = getPrecedence(c)
                while (!operatorsStack.isEmpty() && operatorsStack.peek() != '(' && getPrecedence(operatorsStack.peek()) >= precedence) {
                    val operator = operatorsStack.pop()
                    val secondNumber = numbersStack.pop()
                    val firstNumber = numbersStack.pop()
                    val result = when (operator) {
                        '+' -> firstNumber + secondNumber
                        '-' -> firstNumber - secondNumber
                        '*' -> firstNumber * secondNumber
                        '/' -> firstNumber / secondNumber
                        else -> throw IllegalArgumentException("Invalid operator")
                    }
                    numbersStack.push(result)
                }
                operatorsStack.push(c)
                i++
            } else if (c == '(') {
                // If the character is a left parenthesis, push it to the operators stack
                operatorsStack.push(c)
                i++
            } else if (c == ')') {
                // If the character is a right parenthesis, pop operators from the operators stack until the matching left parenthesis is found,
                // then evaluate the subexpression and push the result to the numbers stack
                while (!operatorsStack.isEmpty() && operatorsStack.peek() != '(') {
                    val operator = operatorsStack.pop()
                    val secondNumber = numbersStack.pop()
                    val firstNumber = numbersStack.pop()
                    val result = when (operator) {
                        '+' -> firstNumber + secondNumber
                        '-' -> firstNumber - secondNumber
                        '*' -> firstNumber * secondNumber
                        '/' -> firstNumber / secondNumber
                        else -> throw IllegalArgumentException("Invalid operator")
                    }
                    numbersStack.push(result)
                }
                operatorsStack.pop()
                i++
            } else {
                throw IllegalArgumentException("Invalid character")
            }
        }

        // Pop any remaining operators from the operators stack and evaluate the corresponding subexpressions
        while (!operatorsStack.isEmpty()) {
            val operator = operatorsStack.pop()
            // Pop the last two numbers from the numbers stack
            val secondNumber = numbersStack.pop()
            val firstNumber = numbersStack.pop()
            // Evaluate the subexpression and push the result to the numbers stack
            val result = when (operator) {
                '+' -> firstNumber + secondNumber
                '-' -> firstNumber - secondNumber
                '*' -> firstNumber * secondNumber
                '/' -> firstNumber / secondNumber
                else -> throw IllegalArgumentException("Invalid operator")
            }
            numbersStack.push(result)
        }

        // The remaining number on the numbers stack is the final result
        val finalResult = numbersStack.pop()

        // Update the text view with the final result
        tvResult.text = finalResult.toString()

    }

}
