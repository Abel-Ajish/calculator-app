package com.example.calculator.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.viewmodel.CalculatorState

@Composable
fun ScientificCalculator(
    state: CalculatorState,
    onAction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = state.result.ifEmpty { "0" },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            textAlign = TextAlign.End,
            fontSize = 48.sp,
            lineHeight = 48.sp
        )

        val buttons = listOf(
            "AC", "DEL", "%", "/",
            "sin", "cos", "tan", "*",
            "log", "ln", "sqrt", "-",
            "7", "8", "9", "+",
            "4", "5", "6", "^",
            "1", "2", "3", "pi",
            "0", ".", "="
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(buttons) { btn ->
                CalculatorButton(
                    symbol = btn,
                    modifier = Modifier.aspectRatio(1f),
                    onClick = { onAction(btn) }
                )
            }
        }
    }
}
