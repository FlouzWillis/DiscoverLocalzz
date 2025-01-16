package com.ebusiness.discoverlocalzz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Die Klasse Coupon repräsentiert einen Coupon in der Room-Datenbank.
 *
 * @property locationId Die ID des zugehörigen Locations.
 * @property userId Die ID des Benutzers, der den Coupon gekauft hat.
 * @property expiryDate Der Zeitpunkt, zu dem der Coupon gekauft wurde.
 * @property isDeleted Ob der Coupon zur Löschung markiert wurde.
 */
@Entity(tableName = "coupon")
class Coupon(
    @ColumnInfo(name = "location_id") val locationId: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "expiry_date") val expiryDate: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    /**
     * Die eindeutige ID des Coupons in der Datenbank.
     */
    @ColumnInfo(name = "coupon_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * Gibt den Restlaufzeit des Coupons als formatierten String zurück.
     *
     * @return Die formatierte Restlaufzeit.
     */
    fun getExpiredDateAsString(): String =
        SimpleDateFormat(
            "d. MMM yyyy",
            Locale.getDefault(),
        ).format(expiryDate)
}
