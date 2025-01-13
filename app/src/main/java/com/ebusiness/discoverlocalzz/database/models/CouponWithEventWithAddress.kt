package com.ebusiness.discoverlocalzz.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Die Klasse TicketWithEventWithAddress stellt eine Beziehung zwischen einem Ticket, einem Event
 * und einer Adresse in der Room-Datenbank dar.
 *
 * @property coupon Das Ticket, das mit dem Event und der Adresse verknüpft ist.
 * @property event Das Event, mit dem das Ticket verknüpft ist, und die dazugehörige Adresse.
 */
@Entity
data class CouponWithEventWithAddress(
    @Embedded val coupon: Coupon,
    @Relation(
        parentColumn = "event_id",
        entityColumn = "event_id",
        entity = Event::class,
    )
    val event: EventWithAddress,
)
