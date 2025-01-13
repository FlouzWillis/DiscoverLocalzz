package com.ebusiness.discoverlocalzz.database.models

import android.content.res.Resources
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.dao.AddressDao
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Die Klasse Event repräsentiert ein Ereignis in der Room-Datenbank.
 *
 * @property organizerId Die ID des Organisators des Ereignisses.
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
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "start") val start: Long,
    @ColumnInfo(name = "end") val end: Long,
    @ColumnInfo(name = "address_id") val addressId: Long,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "city") val city: String
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
            R.string.summary_format,
            getStartDate()
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
            getStartDate()
        )

    suspend fun getAddress(addressDao: AddressDao, resources: Resources): String {
        val address = addressDao.getAddressById(addressId)

        return resources.getString(
            R.string.address_format,
            address?.street ?: "",
            address?.number,
            address?.zipCode ?: "",
            address?.city ?: ""
        )
    }
}
