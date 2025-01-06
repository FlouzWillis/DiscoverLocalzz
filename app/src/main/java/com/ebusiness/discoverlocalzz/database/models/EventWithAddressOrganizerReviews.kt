package com.ebusiness.discoverlocalzz.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Die Klasse EventWithAddressOrganizerReviews repräsentiert ein Ereignis zusammen mit seiner Adresse,
 * dem Veranstalter und den Bewertungen in der Room-Datenbank.
 *
 * @property event Das Ereignis.
 * @property address Die Adresse des Ereignisses.
 * @property organizer Der Veranstalter des Ereignisses.
 * @property reviews Eine Liste von Bewertungen für das Ereignis.
 */
@Entity
data class EventWithAddressOrganizerReviews(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "address_id",
        entityColumn = "address_id",
        entity = Address::class,
    )
    val address: AddressWithZipCode,
    @Relation(
        parentColumn = "organizer_id",
        entityColumn = "organizer_id",
    )
    val organizer: Organizer,
    @Relation(
        parentColumn = "event_id",
        entityColumn = "event_id",
    )
    val reviews: List<Review>,
)
