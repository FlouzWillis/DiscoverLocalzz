package com.ebusiness.discoverlocalzz.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Die Klasse LocationWithAddress repräsentiert ein Ereignis zusammen mit seiner Adresse in der Room-Datenbank.
 *
 * @property location Das Ereignis.
 * @property address Die Adresse des Ereignisses.
 */
@Entity
data class LocationWithAddress(
    @Embedded val location: Location,
    @Relation(
        parentColumn = "address_id",
        entityColumn = "address_id",
        entity = Address::class,
    )
    val address: Address,
)
