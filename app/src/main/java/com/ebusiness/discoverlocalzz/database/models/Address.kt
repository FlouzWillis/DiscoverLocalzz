package com.ebusiness.discoverlocalzz.database.models

import android.content.res.Resources
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ebusiness.discoverlocalzz.R

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
    @ColumnInfo(name = "city") val city: String,
    ) {
    /**
     * Die eindeutige ID der Adresse.
     */
    @ColumnInfo(name = "address_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    /**
     * Gibt eine formatierte Zeichenfolge der Adresse zurück.
     *
     * @param resources Die Ressourcen.
     * @return Die formatierte Adresse.
     */
    fun toString(resources: Resources): String =
        resources.getString(
            R.string.address_format,
            street,
            number,
            zipCode,
            city,
        )
}
