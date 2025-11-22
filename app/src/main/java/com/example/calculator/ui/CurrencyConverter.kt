package com.example.calculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.viewmodel.CurrencyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverter(
    state: CurrencyState,
    onAmountChange: (String) -> Unit,
    onFromCurrencyChange: (String) -> Unit,
    onToCurrencyChange: (String) -> Unit,
    onConvert: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Currency Converter", fontSize = 24.sp, modifier = Modifier.padding(bottom = 32.dp))

        OutlinedTextField(
            value = state.amount,
            onValueChange = onAmountChange,
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CurrencyDropdown(
                selected = state.fromCurrency,
                options = state.currencies,
                onSelect = onFromCurrencyChange,
                label = "From"
            )
            CurrencyDropdown(
                selected = state.toCurrency,
                options = state.currencies,
                onSelect = onToCurrencyChange,
                label = "To"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onConvert,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Convert")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (state.convertedAmount.isNotEmpty()) {
            Text(
                text = "${state.amount} ${state.fromCurrency} = ${state.convertedAmount} ${state.toCurrency}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (state.error != null) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(150.dp)
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
