package com.ebusiness.discoverlocalzz.database

/**
 * Datenklasse, die Zahlungsdetails repräsentiert.
 *
 * @param cardNumber Die Kartennummer der Karte.
 * @param month Der Ablaufmonat der Karte.
 * @param year Das Ablaufjahr der Karte.
 * @param cvc Die CVC der Karte.
 */
data class PaymentDetails(
    val cardNumber: String,
    val month: String,
    val year: String,
    val cvc: String,
)
