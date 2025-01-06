package com.ebusiness.discoverlocalzz.data.models

import android.content.res.Resources
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.ebusiness.discoverlocalzz.R

/**
 * Die Klasse AddressWithZipCode repräsentiert eine Adresse zusammen mit ihrer Postleitzahl in der Room-Datenbank.
 *
 * @property address Die Adresse.
 * @property zipCode Die Postleitzahl.
 */
@Entity
data class AddressWithZipCode(
    @Embedded val address: Address,
    @Relation(
        parentColumn = "zip_code",
        entityColumn = "zip_code",
    )
    val zipCode: ZipCode,
) {
    /**
     * Gibt eine formatierte Zeichenfolge der Adresse zurück.
     *
     * @param resources Die Ressourcen.
     * @return Die formatierte Adresse.
     */
    fun toString(resources: Resources): String =
        resources.getString(
            R.string.address_format,
            address.street,
            address.number,
            zipCode.zipCode,
            zipCode.city,
        )
}
