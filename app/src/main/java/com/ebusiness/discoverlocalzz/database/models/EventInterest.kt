package com.ebusiness.discoverlocalzz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Die Klasse EventInterest repräsentiert die Verbindung zwischen einem Ereignis und einem Interesse
 * in der Room-Datenbank.
 *
 * @property eventId Die ID des Ereignisses.
 * @property interestId Die ID der Interesse.
 */
@Entity(
    tableName = "event_interest",
    primaryKeys = ["event_id", "interest_id"],
)
data class EventInterest(
    @ColumnInfo(name = "event_id") val eventId: Long,
    @ColumnInfo(name = "interest_id") val interestId: Long,
)
