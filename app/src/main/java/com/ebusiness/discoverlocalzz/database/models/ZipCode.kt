package com.ebusiness.discoverlocalzz.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Die Klasse ZipCode repräsentiert Postleitzahlen in der Room-Datenbank.
 *
 * @property zipCode Die Postleitzahl.
 * @property city Die Stadt, die mit der Postleitzahl verknüpft ist.
 */
@Entity(tableName = "zip_code")
data class ZipCode(
    @ColumnInfo(name = "zip_code") @PrimaryKey val zipCode: String,
    @ColumnInfo(name = "city") val city: String,
)
