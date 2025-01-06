package com.ebusiness.discoverlocalzz.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Die Klasse Organizer repräsentiert einen Veranstalter in der Room-Datenbank.
 *
 * @property name Der Name des Veranstalters.
 */
@Entity(tableName = "organizer")
class Organizer(
    @ColumnInfo(name = "name") val name: String,
) {
    /**
     * Die eindeutige ID des Veranstalters in der Datenbank.
     */
    @ColumnInfo(name = "organizer_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
