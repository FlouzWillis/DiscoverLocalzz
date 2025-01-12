package com.ebusiness.discoverlocalzz.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Die Klasse Interest repräsentiert ein Interesse in der Room-Datenbank.
 *
 * @property name Der Name der Interesse.
 * @property image Base64-kodiertes Bild der Interesse.
 */
@Entity(tableName = "interest")
class Interest(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image") val image: String,
) {
    /**
     * Die eindeutige ID der Interesse.
     */
    @ColumnInfo(name = "interest_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
