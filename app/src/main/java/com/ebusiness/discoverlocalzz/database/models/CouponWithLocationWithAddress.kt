package com.ebusiness.discoverlocalzz.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Die Klasse TicketWithLocationWithAddress stellt eine Beziehung zwischen einem Ticket, einem Location
 * und einer Adresse in der Room-Datenbank dar.
 *
 * @property coupon Das Ticket, das mit dem Location und der Adresse verknüpft ist.
 * @property location Das Location, mit dem das Ticket verknüpft ist, und die dazugehörige Adresse.
 */
@Entity
data class CouponWithLocationWithAddress(
    @Embedded val coupon: Coupon,
    @Relation(
        parentColumn = "location_id",
        entityColumn = "location_id",
        entity = Location::class,
    )
    val location: LocationWithAddress,
)
