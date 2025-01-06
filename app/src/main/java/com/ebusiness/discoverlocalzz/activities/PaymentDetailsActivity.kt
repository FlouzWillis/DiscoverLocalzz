package com.ebusiness.discoverlocalzz.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.PaymentDetails
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

/**
 * Aktivität für die Zahlungsdetails.
 */
class PaymentDetailsActivity : BaseActivity() {
    private lateinit var cardNumber: TextInputLayout
    private lateinit var month: TextInputLayout
    private lateinit var year: TextInputLayout
    private lateinit var cvc: TextInputLayout

    /**
     * Initialisiert die Zahlungsdetails-Aktivität.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_details)

        cardNumber = findViewById(R.id.card_number)
        month = findViewById(R.id.month)
        year = findViewById(R.id.year)
        cvc = findViewById(R.id.cvc)

        loadPaymentDetails()

        (month.editText as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                Array(MONTHS_PER_YEAR) { String.format(Locale.US, "%02d", it + 1) },
            ),
        )
        (year.editText as? AutoCompleteTextView)?.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                Array(YEARS_PER_CENTURY) { String.format(Locale.US, "%02d", it) },
            ),
        )

        findViewById<Button>(R.id.delete).setOnClickListener {
            onDeleteClicked()
        }

        findViewById<FloatingActionButton>(R.id.floating_action_button).setOnClickListener {
            if (validate()) {
                Preferences.setPaymentDetails(
                    this,
                    PaymentDetails(
                        cardNumber.editText?.text.toString(),
                        month.editText?.text.toString(),
                        year.editText?.text.toString(),
                        cvc.editText?.text.toString(),
                    ),
                )
                finish()
            }
        }
    }

    private fun loadPaymentDetails() {
        val paymentDetails = Preferences.getPaymentDetails(this)
        cardNumber.editText?.setText(paymentDetails.cardNumber)
        month.editText?.setText(paymentDetails.month)
        year.editText?.setText(paymentDetails.year)
        cvc.editText?.setText(paymentDetails.cvc)
    }

    private fun onDeleteClicked() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_payment_details)
            .setMessage(R.string.delete_payment_details_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                Preferences.setPaymentDetails(
                    this,
                    PaymentDetails("", "", "", ""),
                )
                finish()
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .show()
    }

    private fun validate(): Boolean {
        val cardNumberValue = cardNumber.editText?.text.toString()
        val monthValue = month.editText?.text.toString()
        val yearValue = year.editText?.text.toString()
        val cvcValue = cvc.editText?.text.toString()
        var isValid = true

        listOf(cardNumber, month, year, cvc).forEach {
            it.error = null
        }
        if (cardNumberValue.toIntOrNull() == null) {
            cardNumber.error = resources.getString(R.string.invalid)
            isValid = false
        }
        if (monthValue.toIntOrNull() == null || monthValue.length != 2) {
            month.error = resources.getString(R.string.invalid)
            isValid = false
        }
        if (yearValue.toIntOrNull() == null || yearValue.length != 2) {
            year.error = resources.getString(R.string.invalid)
            isValid = false
        }
        if (cvcValue.toIntOrNull() == null || cvcValue.length != CVC_LENGTH) {
            cvc.error = resources.getString(R.string.invalid)
            isValid = false
        }
        return isValid
    }

    /**
     * Überschreibt den Zurück-Button um den User-Flow zu gewährleisten.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val MONTHS_PER_YEAR = 12
        private const val YEARS_PER_CENTURY = 100
        private const val CVC_LENGTH = 3
    }
}
