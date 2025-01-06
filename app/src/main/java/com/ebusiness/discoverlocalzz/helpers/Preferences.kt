package com.ebusiness.discoverlocalzz.helpers

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ebusiness.discoverlocalzz.database.PaymentDetails

/**
 * Hilfsklasse zur Verwaltung von Benutzereinstellungen.
 */
object Preferences {
    private const val ACCOUNT_ID = "accountId"
    private const val PAYMENT_DETAILS_CARD_NUMBER = "paymentDetailscardNumber"
    private const val PAYMENT_DETAILS_MONTH = "paymentDetailsMonth"
    private const val PAYMENT_DETAILS_YEAR = "paymentDetailsYear"
    private const val PAYMENT_DETAILS_CVC = "paymentDetailsCvc"
    private const val ENCRYPTED_PREFERENCES_NAME = "encrypted_preferences"

    /**
     * Konstante, die den Standardwert für ein nicht vorhandenes Benutzerkonto repräsentiert.
     */
    const val NO_ACCOUNT: Long = -1L

    /**
     * Überprüft, ob ein Benutzer angemeldet ist.
     *
     * @param context Der Kontext der Anwendung.
     * @return True, wenn ein Benutzer angemeldet ist, andernfalls False.
     */
    fun isLoggedIn(context: Context): Boolean = getUserId(context) != NO_ACCOUNT

    /**
     * Legt fest, dass ein Benutzer angemeldet ist.
     *
     * @param context Der Kontext der Anwendung.
     * @param accountId Die ID des angemeldeten Benutzers.
     */
    fun setLoggedIn(
        context: Context,
        accountId: Long,
    ) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putLong(ACCOUNT_ID, accountId)
            .apply()
    }

    /**
     * Ruft die ID des angemeldeten Benutzers ab.
     *
     * @param context Der Kontext der Anwendung.
     * @return Die ID des angemeldeten Benutzers oder [NO_ACCOUNT], wenn kein Benutzer angemeldet ist.
     */
    fun getUserId(context: Context): Long =
        PreferenceManager
            .getDefaultSharedPreferences(context)
            .getLong(ACCOUNT_ID, NO_ACCOUNT)

    private fun getEncryptedPreferences(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFERENCES_NAME,
            MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    /**
     * Speichert die Zahlungsinformationen.
     *
     * @param context Der Kontext der Anwendung.
     * @param paymentDetails Die Zahlungsinformationen.
     */
    fun setPaymentDetails(
        context: Context,
        paymentDetails: PaymentDetails,
    ) {
        getEncryptedPreferences(context)
            .edit()
            .putString(PAYMENT_DETAILS_CARD_NUMBER, paymentDetails.cardNumber)
            .putString(PAYMENT_DETAILS_MONTH, paymentDetails.month)
            .putString(PAYMENT_DETAILS_YEAR, paymentDetails.year)
            .putString(PAYMENT_DETAILS_CVC, paymentDetails.cvc)
            .apply()
    }

    /**
     * Ruft die gespeicherten Zahlungsinformationen ab.
     *
     * @param context Der Kontext der Anwendung.
     * @return Die gespeicherten Zahlungsinformationen.
     */
    fun getPaymentDetails(context: Context): PaymentDetails {
        val preferences = getEncryptedPreferences(context)
        return PaymentDetails(
            preferences.getString(PAYMENT_DETAILS_CARD_NUMBER, "") ?: "",
            preferences.getString(PAYMENT_DETAILS_MONTH, "") ?: "",
            preferences.getString(PAYMENT_DETAILS_YEAR, "") ?: "",
            preferences.getString(PAYMENT_DETAILS_CVC, "") ?: "",
        )
    }

    /**
     * Überprüft ob Zahlungsinformationen gespeichert sind.
     *
     * @param context Der Kontext der Anwendung.
     * @return Ob Zahlungsinformationen gespeichert sind.
     */
    fun hasPaymentDetails(context: Context): Boolean =
        getEncryptedPreferences(context)
            .getString(PAYMENT_DETAILS_CARD_NUMBER, "")
            ?.isNotBlank()
            ?: false
}
