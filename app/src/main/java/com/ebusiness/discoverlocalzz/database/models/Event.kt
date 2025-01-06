package com.ebusiness.discoverlocalzz.data.models

import android.content.res.Resources
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ebusiness.discoverlocalzz.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Die Klasse Event repräsentiert ein Ereignis in der Room-Datenbank.
 *
 * @property organizerId Die ID des Organisators des Ereignisses.
 * @property price Der Preis des Ereignisses.
 * @property title Der Titel des Ereignisses.
 * @property start Der Startzeitpunkt des Ereignisses (in Millisekunden seit der Epoche).
 * @property end Der Endzeitpunkt des Ereignisses (in Millisekunden seit der Epoche).
 * @property addressId Die ID der Adresse des Ereignisses.
 * @property description Die Beschreibung des Ereignisses.
 * @property image Die Bild-Daten des Ereignisses in Base64-codierter Form.
 */
@Suppress("LongParameterList")
@Entity(tableName = "event")
class Event(
    @ColumnInfo(name = "organizer_id") val organizerId: Long,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "start") val start: Long,
    @ColumnInfo(name = "end") val end: Long,
    @ColumnInfo(name = "address_id") val addressId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image") val image: String,
) {
    /**
     * Die eindeutige ID des Ereignisses.
     */
    @ColumnInfo(name = "event_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    private fun getStartDate(): String =
        SimpleDateFormat(
            "d. MMM yyyy",
            Locale.getDefault(),
        ).format(start)

    private fun getStartTime(): String =
        SimpleDateFormat(
            "H:mm",
            Locale.getDefault(),
        ).format(start)

    /**
     * Gibt den Startzeitpunkt des Ereignisses als formatierten String zurück.
     *
     * @param resources Die Ressourcen.
     * @return Der formatierte Startzeitpunkt.
     */
    fun getStartAsString(resources: Resources): String =
        resources.getString(
            R.string.start_format,
            getStartDate(),
            getStartTime(),
        )

    /**
     * Gibt den Preis des Ereignisses als formatierten String zurück.
     *
     * @param resources Die Ressourcen.
     * @return Der formatierte Preis.
     */
    fun getPriceAsString(resources: Resources): String =
        resources.getString(R.string.currency_format, "%.2f".format(Locale.getDefault(), price))

    /**
     * Gibt den Preis des Ereignisses als formatierten String in Langform zurück.
     *
     * @param resources Die Ressourcen.
     * @return Der formatierte Preis.
     */
    fun getPriceAsLongString(resources: Resources): String =
        resources.getString(
            R.string.vat_format,
            getPriceAsString(resources),
        )

    /**
     * Gibt eine Zusammenfassung des Ereignisses als formatierten String zurück.
     *
     * @param resources Die Ressourcen.
     * @return Die formatierte Zusammenfassung.
     */
    fun getSummary(resources: Resources): String =
        resources.getString(
            R.string.summary_format,
            getStartDate(),
            getPriceAsString(resources),
        )
}
