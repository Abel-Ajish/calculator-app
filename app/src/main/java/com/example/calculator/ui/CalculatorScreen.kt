package com.example.calculator.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.calculator.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val calcState by viewModel.calcState.collectAsState()
    val currencyState by viewModel.currencyState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Standard", "Scientific", "Currency")

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(title) },
                        icon = { }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> StandardCalculator(
                    state = calcState,
                    onAction = viewModel::onAction
                )
                1 -> ScientificCalculator(
                    state = calcState,
                    onAction = viewModel::onAction
                )
                2 -> CurrencyConverter(
                    state = currencyState,
                    onAmountChange = viewModel::onCurrencyAmountChange,
                    onFromCurrencyChange = viewModel::onFromCurrencyChange,
                    onToCurrencyChange = viewModel::onToCurrencyChange,
                    onConvert = viewModel::convertCurrency
                )
            }
        }
    }
}
