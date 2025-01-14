package com.ebusiness.discoverlocalzz.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Die Klasse LocationWithAddressOrganizerReviews repräsentiert ein Ereignis zusammen mit seiner Adresse,
 * dem Veranstalter und den Bewertungen in der Room-Datenbank.
 *
 * @property location Das Ereignis.
 * @property address Die Adresse des Ereignisses.
 * @property organizer Der Veranstalter des Ereignisses.
 * @property reviews Eine Liste von Bewertungen für das Ereignis.
 */
@Entity
data class LocationWithAddressOrganizerReviews(
    @Embedded val location: Location,
    @Relation(
        parentColumn = "address_id",
        entityColumn = "address_id",
        entity = Address::class,
    )
    val address: Address,
    @Relation(
        parentColumn = "organizer_id",
        entityColumn = "organizer_id",
    )
    val organizer: Organizer,
    @Relation(
        parentColumn = "location_id",
        entityColumn = "location_id",
    )
    val reviews: List<Review>,
)
