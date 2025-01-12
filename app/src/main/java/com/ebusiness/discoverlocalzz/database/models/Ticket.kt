package com.ebusiness.discoverlocalzz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Die Klasse Ticket repräsentiert ein Ticket in der Room-Datenbank.
 *
 * @property eventId Die ID des zugehörigen Events.
 * @property userId Die ID des Benutzers, der das Ticket gekauft hat.
 * @property purchasedAt Der Zeitpunkt, zu dem das Ticket gekauft wurde.
 * @property isDeleted Ob das Ticket zur Löschung markiert wurde.
 */
@Entity(tableName = "ticket")
class Ticket(
    @ColumnInfo(name = "event_id") val eventId: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "purchased_at") val purchasedAt: Long,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    /**
     * Die eindeutige ID des Tickets in der Datenbank.
     */
    @ColumnInfo(name = "ticket_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * Gibt den Kaufzeitpunkt des Tickets als formatierten String zurück.
     *
     * @return Der formatierte Kaufzeitpunkt.
     */
    fun getPurchasedAtAsString(): String =
        SimpleDateFormat(
            "d. MMM yyyy",
            Locale.getDefault(),
        ).format(purchasedAt)
}
