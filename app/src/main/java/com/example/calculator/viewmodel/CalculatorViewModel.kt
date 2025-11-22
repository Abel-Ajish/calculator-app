package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: String? = null,
    val result: String = "0"
)

data class CurrencyState(
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val amount: String = "1",
    val convertedAmount: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val currencies: List<String> = listOf("USD", "EUR", "GBP", "JPY", "INR", "CAD", "AUD", "CHF", "CNY", "SEK")
)

class CalculatorViewModel : ViewModel() {

    private val _calcState = MutableStateFlow(CalculatorState())
    val calcState: StateFlow<CalculatorState> = _calcState.asStateFlow()

    private val _currencyState = MutableStateFlow(CurrencyState())
    val currencyState: StateFlow<CurrencyState> = _currencyState.asStateFlow()

    // Calculator Logic
    fun onAction(action: String) {
        when (action) {
            "AC" -> _calcState.value = CalculatorState()
            "DEL" -> deleteLast()
            "+", "-", "*", "/" -> setOperation(action)
            "=" -> calculate()
            "%" -> calculatePercentage()
            "log" -> calculateScientific { x -> log10(x) }
            "ln" -> calculateScientific { x -> ln(x) }
            "sqrt" -> calculateScientific { x -> sqrt(x) }
            "sin" -> calculateScientific { x -> sin(Math.toRadians(x)) }
            "cos" -> calculateScientific { x -> cos(Math.toRadians(x)) }
            "tan" -> calculateScientific { x -> tan(Math.toRadians(x)) }
            "pi" -> appendInput(Math.PI.toString())
            else -> appendInput(action)
        }
    }

    private fun appendInput(value: String) {
        val state = _calcState.value
        if (state.operation == null) {
            if (state.number1.length < 15) {
                _calcState.value = state.copy(number1 = state.number1 + value, result = state.number1 + value)
            }
        } else {
            if (state.number2.length < 15) {
                _calcState.value = state.copy(number2 = state.number2 + value, result = state.number2 + value)
            }
        }
    }

    private fun setOperation(op: String) {
        val state = _calcState.value
        if (state.number1.isNotEmpty()) {
            _calcState.value = state.copy(operation = op)
        }
    }

    private fun calculate() {
        val state = _calcState.value
        val n1 = state.number1.toDoubleOrNull()
        val n2 = state.number2.toDoubleOrNull()

        if (n1 != null && n2 != null && state.operation != null) {
            val result = when (state.operation) {
                "+" -> n1 + n2
                "-" -> n1 - n2
                "*" -> n1 * n2
                "/" -> if (n2 != 0.0) n1 / n2 else Double.NaN
                else -> 0.0
            }
            _calcState.value = CalculatorState(number1 = result.toString().take(15), result = result.toString().take(15))
        }
    }

    private fun deleteLast() {
        val state = _calcState.value
        if (state.operation == null) {
            _calcState.value = state.copy(number1 = state.number1.dropLast(1), result = state.number1.dropLast(1))
        } else {
            _calcState.value = state.copy(number2 = state.number2.dropLast(1), result = state.number2.dropLast(1))
        }
    }
    
    private fun calculatePercentage() {
        val state = _calcState.value
        val n1 = state.number1.toDoubleOrNull()
        if (n1 != null) {
            val result = n1 / 100
             _calcState.value = CalculatorState(number1 = result.toString().take(15), result = result.toString().take(15))
        }
    }

    private fun calculateScientific(func: (Double) -> Double) {
        val state = _calcState.value
        val n1 = state.number1.toDoubleOrNull()
        if (n1 != null) {
            val result = func(n1)
            _calcState.value = CalculatorState(number1 = result.toString().take(15), result = result.toString().take(15))
        }
    }

    // Currency Logic
    fun onCurrencyAmountChange(amount: String) {
        _currencyState.value = _currencyState.value.copy(amount = amount)
    }

    fun onFromCurrencyChange(currency: String) {
        _currencyState.value = _currencyState.value.copy(fromCurrency = currency)
    }

    fun onToCurrencyChange(currency: String) {
        _currencyState.value = _currencyState.value.copy(toCurrency = currency)
    }

    fun convertCurrency() {
        val state = _currencyState.value
        val amount = state.amount.toDoubleOrNull()
        if (amount == null) return

        viewModelScope.launch {
            _currencyState.value = state.copy(isLoading = true, error = null)
            try {
                val response = RetrofitInstance.api.getLatestRates(state.fromCurrency)
                val rate = response.rates[state.toCurrency]
                if (rate != null) {
                    val result = amount * rate
                    _currencyState.value = state.copy(convertedAmount = "%.2f".format(result), isLoading = false)
                } else {
                    _currencyState.value = state.copy(error = "Rate not found", isLoading = false)
                }
            } catch (e: Exception) {
                _currencyState.value = state.copy(error = e.message ?: "Unknown error", isLoading = false)
            }
        }
    }
}
