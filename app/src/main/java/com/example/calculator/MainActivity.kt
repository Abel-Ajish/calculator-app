package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.log10
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private var currentInput = ""
    private var firstOperand = 0.0
    private var currentOperator = ""
    private var isNewOp = true

    // Currency Converter
    private lateinit var etCurrencyInput: EditText
    private lateinit var spinnerCurrency: Spinner
    private lateinit var tvCurrencyResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        setupCalculatorButtons()
        setupCurrencyConverter()
    }

    private fun setupCalculatorButtons() {
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        for (id in numberButtons) {
            findViewById<Button>(id).setOnClickListener { view ->
                val button = view as Button
                if (isNewOp) {
                    tvDisplay.text = ""
                    isNewOp = false
                }
                currentInput = tvDisplay.text.toString() + button.text
                tvDisplay.text = currentInput
            }
        }

        val opButtons = mapOf(
            R.id.btnAdd to "+",
            R.id.btnSubtract to "-",
            R.id.btnMultiply to "*",
            R.id.btnDivide to "/"
        )

        for ((id, op) in opButtons) {
            findViewById<Button>(id).setOnClickListener {
                if (tvDisplay.text.isNotEmpty()) {
                    firstOperand = tvDisplay.text.toString().toDoubleOrNull() ?: 0.0
                    currentOperator = op
                    isNewOp = true
                }
            }
        }

        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            if (tvDisplay.text.isNotEmpty() && currentOperator.isNotEmpty()) {
                val secondOperand = tvDisplay.text.toString().toDoubleOrNull() ?: 0.0
                var result = 0.0
                when (currentOperator) {
                    "+" -> result = firstOperand + secondOperand
                    "-" -> result = firstOperand - secondOperand
                    "*" -> result = firstOperand * secondOperand
                    "/" -> {
                        if (secondOperand != 0.0) result = firstOperand / secondOperand
                        else Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show()
                    }
                }
                tvDisplay.text = result.toString()
                isNewOp = true
                currentOperator = ""
            }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            tvDisplay.text = "0"
            currentInput = ""
            firstOperand = 0.0
            currentOperator = ""
            isNewOp = true
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            val currentText = tvDisplay.text.toString()
            if (currentText.isNotEmpty() && currentText != "0") {
                tvDisplay.text = currentText.dropLast(1)
                if (tvDisplay.text.isEmpty()) {
                    tvDisplay.text = "0"
                    isNewOp = true
                }
            }
        }

        findViewById<Button>(R.id.btnLog).setOnClickListener {
            val value = tvDisplay.text.toString().toDoubleOrNull()
            if (value != null && value > 0) {
                val result = log10(value)
                tvDisplay.text = result.toString()
                isNewOp = true
            } else {
                Toast.makeText(this, "Invalid input for Log", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnAntilog).setOnClickListener {
            val value = tvDisplay.text.toString().toDoubleOrNull()
            if (value != null) {
                val result = 10.0.pow(value)
                tvDisplay.text = result.toString()
                isNewOp = true
            }
        }
    }

    private fun setupCurrencyConverter() {
        etCurrencyInput = findViewById(R.id.etCurrencyInput)
        spinnerCurrency = findViewById(R.id.spinnerCurrency)
        tvCurrencyResult = findViewById(R.id.tvCurrencyResult)

        val currencies = listOf("USD to EUR", "USD to INR", "EUR to USD", "INR to USD")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter

        findViewById<Button>(R.id.btnConvert).setOnClickListener {
            val amount = etCurrencyInput.text.toString().toDoubleOrNull()
            if (amount != null) {
                val selectedConversion = spinnerCurrency.selectedItem.toString()
                var result = 0.0
                // Hardcoded exchange rates (approximate)
                when (selectedConversion) {
                    "USD to EUR" -> result = amount * 0.92
                    "USD to INR" -> result = amount * 83.0
                    "EUR to USD" -> result = amount * 1.09
                    "INR to USD" -> result = amount * 0.012
                }
                tvCurrencyResult.text = "Result: %.2f".format(result)
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
