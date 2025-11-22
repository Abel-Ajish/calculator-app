package com.example.calculator.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("/latest")
    suspend fun getLatestRates(@Query("from") baseCurrency: String): CurrencyResponse
}

data class CurrencyResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
