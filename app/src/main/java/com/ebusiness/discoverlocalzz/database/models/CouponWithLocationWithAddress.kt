package com.ebusiness.discoverlocalzz.database.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Die Klasse CouponWithLocationWithAddress stellt eine Beziehung zwischen einem Coupon, einer Location
 * und einer Adresse in der Room-Datenbank dar.
 *
 * @property coupon Der Coupon, der mit der Location und der Adresse verknüpft ist.
 * @property location Die Location, mit der der Coupon verknüpft ist, und die dazugehörige Adresse.
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
