package com.ebusiness.discoverlocalzz.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Die Klasse Address repräsentiert eine Adresse in der Room-Datenbank.
 *
 * @property street Die Straße der Adresse.
 * @property zipCode Die Postleitzahl der Adresse.
 * @property number Die Hausnummer der Adresse.
 */
@Entity(tableName = "address")
class Address(
    @ColumnInfo(name = "street") val street: String,
    @ColumnInfo(name = "zip_code") val zipCode: String,
    @ColumnInfo(name = "number") val number: String,
) {
    /**
     * Die eindeutige ID der Adresse.
     */
    @ColumnInfo(name = "address_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
