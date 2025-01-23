package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : ComponentActivity() {
    private lateinit var inputDisplay: TextView
    private lateinit var resultDisplay: TextView
    private var currentInput = StringBuilder()
    private var currentResult = ""
    private var lastOperator = ""
    private var isNewOperation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle edge-to-edge rendering
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize TextViews
        inputDisplay = findViewById(R.id.input_display)
        resultDisplay = findViewById(R.id.result_display)

        setupNumberButtons()
        setupOperatorButtons()
        setupSpecialButtons()
    }

    private fun setupNumberButtons() {
        val numberButtons = listOf(
            findViewById<Button>(R.id.button_0),
            findViewById<Button>(R.id.button_1),
            findViewById<Button>(R.id.button_2),
            findViewById<Button>(R.id.button_3),
            findViewById<Button>(R.id.button_4),
            findViewById<Button>(R.id.button_5),
            findViewById<Button>(R.id.button_6),
            findViewById<Button>(R.id.button_7),
            findViewById<Button>(R.id.button_8),
            findViewById<Button>(R.id.button_9)
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                if (isNewOperation) {
                    currentInput.clear()
                    isNewOperation = false
                }
                currentInput.append(button.text)
                updateDisplay()
            }
        }
    }

    private fun setupOperatorButtons() {
        // Find all operator buttons in the layout
        val buttonPlus = findViewById<Button>(R.id.plus)
        val buttonMinus = findViewById<Button>(R.id.minus)
        val buttonMultiply = findViewById<Button>(R.id.multiply)
        val buttonDivide = findViewById<Button>(R.id.divide)
        val buttonPercent = findViewById<Button>(R.id.percent)

        val operatorClickListener = { operator: String ->
            if (currentInput.isNotEmpty()) {
                lastOperator = operator
                if (currentResult.isEmpty()) {
                    currentResult = currentInput.toString()
                } else {
                    calculateResult()
                }
                isNewOperation = true
                updateDisplay()
            }
        }

        buttonPlus?.setOnClickListener { operatorClickListener("+") }
        buttonMinus?.setOnClickListener { operatorClickListener("-") }
        buttonMultiply?.setOnClickListener { operatorClickListener("×") }
        buttonDivide?.setOnClickListener { operatorClickListener("/") }
        buttonPercent?.setOnClickListener { operatorClickListener("%") }
    }

    private fun setupSpecialButtons() {
        // Clear button - Look for button with "C" text
        findViewById<Button>(R.id.clear)?.setOnClickListener {
            currentInput.clear()
            currentResult = ""
            lastOperator = ""
            isNewOperation = true
            updateDisplay()
        }

        // Decimal button - Look for button with "." text
        findViewById<Button>(R.id.decimal)?.setOnClickListener {
            if (!currentInput.contains(".")) {
                if (currentInput.isEmpty() || isNewOperation) {
                    currentInput.clear()
                    currentInput.append("0")
                    isNewOperation = false
                }
                currentInput.append(".")
                updateDisplay()
            }
        }

        // Equals button
        val equalsButton = findViewById<Button>(R.id.equals)
        equalsButton?.setOnClickListener {
            if (currentInput.isNotEmpty() && currentResult.isNotEmpty()) {
                calculateResult()
                isNewOperation = true
            }
        }
    }

    private fun calculateResult() {
        try {
            val input = currentInput.toString().toDouble()
            val result = when (lastOperator) {
                "+" -> currentResult.toDouble() + input
                "-" -> currentResult.toDouble() - input
                "×" -> currentResult.toDouble() * input
                "/" -> currentResult.toDouble() / input
                "%" -> currentResult.toDouble() % input
                else -> input
            }

            currentResult = formatResult(result)
            currentInput.clear()
            currentInput.append(currentResult)
            updateDisplay()
        } catch (e: Exception) {
            currentInput.clear()
            currentResult = "Error"
            updateDisplay()
        }
    }

    private fun formatResult(result: Double): String {
        return if (result % 1 == 0.0) {
            result.toLong().toString()
        } else {
            String.format("%.2f", result)
        }
    }

    private fun updateDisplay() {
        inputDisplay.text = if (currentInput.isEmpty()) "0" else currentInput.toString()
        resultDisplay.text = if (currentResult.isEmpty()) "0" else currentResult
    }
}