package com.example.receipttracker.data

enum class ReceiptType(val displayName: String) {
    CarHire("Car Hire"),
    Flight("Flight"),
    FoodAndDrink("Food And Drink"),
    Fuel("Fuel"),
    Hotel("Hotel"),
    Parking("Parking"),
    Rail("Rail"),
    Taxi("Taxi"),
    Other("Other")
    ;

    companion object {
        fun receiptTypeFromString(input: String): ReceiptType =
            ReceiptType.entries.find { it.name == input || it.displayName == input } ?: FoodAndDrink

        fun displayNameFromType(input: String): String =
            receiptTypeFromString(input).displayName
    }
}