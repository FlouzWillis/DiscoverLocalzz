package com.ebusiness.discoverlocalzz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Die Klasse LocationInterest repräsentiert die Verbindung zwischen einem Ereignis und einem Interesse
 * in der Room-Datenbank.
 *
 * @property locationId Die ID des Ereignisses.
 * @property interestId Die ID der Interesse.
 */
@Entity(
    tableName = "location_interest",
    primaryKeys = ["location_id", "interest_id"],
)
data class LocationInterest(
    @ColumnInfo(name = "location_id") val locationId: Long,
    @ColumnInfo(name = "interest_id") val interestId: Long,
)
