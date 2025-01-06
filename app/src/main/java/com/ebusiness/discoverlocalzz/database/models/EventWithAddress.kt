package com.ebusiness.discoverlocalzz.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Die Klasse EventWithAddress repräsentiert ein Ereignis zusammen mit seiner Adresse in der Room-Datenbank.
 *
 * @property event Das Ereignis.
 * @property address Die Adresse des Ereignisses.
 */
@Entity
data class EventWithAddress(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "address_id",
        entityColumn = "address_id",
        entity = Address::class,
    )
    val address: AddressWithZipCode,
)
