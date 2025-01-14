package com.ebusiness.discoverlocalzz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Die Klasse Ticket repräsentiert ein Ticket in der Room-Datenbank.
 *
 * @property locationId Die ID des zugehörigen Locations.
 * @property userId Die ID des Benutzers, der das Ticket gekauft hat.
 * @property expiryDate Der Zeitpunkt, zu dem das Ticket gekauft wurde.
 * @property isDeleted Ob das Ticket zur Löschung markiert wurde.
 */
@Entity(tableName = "coupon")
class Coupon(
    @ColumnInfo(name = "location_id") val locationId: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "expiry_date") val expiryDate: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    /**
     * Die eindeutige ID des Tickets in der Datenbank.
     */
    @ColumnInfo(name = "coupon_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * Gibt den Kaufzeitpunkt des Tickets als formatierten String zurück.
     *
     * @return Der formatierte Kaufzeitpunkt.
     */
    fun getExpiredDateAsString(): String =
        SimpleDateFormat(
            "d. MMM yyyy",
            Locale.getDefault(),
        ).format(expiryDate)
}
