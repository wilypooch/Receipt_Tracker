package com.example.receipttracker.data

enum class AppCurrency(
    val code: String,
    val symbol: String,
    val displayName: String,
) {
    GBP("GBP", "£", "British Pound (GBP)"),
    AUD("AUD", "\$A", "Australian Dollar (AUD)"),
    CAD("CAD", "Can$", "Canadian Dollar (CAD)"),
    CHF("CHF", "CHF", "Swiss Franc (CHF)"),
    EUR("EUR", "€", "Euro (EUR)"),
    JPY("JPY", "¥", "Japanese Yen (JPY)"),
    NZD("NZD", "\$NZ", "New Zealand Dollar (NZD)"),
    USD("USD", "$", "US Dollar (USD)"),
    ;

    companion object {
        fun currencyFromCode(code: String): AppCurrency =
            entries.find { it.code == code } ?: GBP

        fun symbolFromCode(code: String): String =
            currencyFromCode(code).symbol
    }

}