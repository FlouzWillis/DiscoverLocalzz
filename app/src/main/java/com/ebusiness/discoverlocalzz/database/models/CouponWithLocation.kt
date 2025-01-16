package com.ebusiness.discoverlocalzz.database.models

import android.content.res.Resources
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.database.dao.AddressDao

/**
 * Die Klasse CouponWithLocation stellt eine Beziehung zwischen einem Coupon und einer Location in der Room-Datenbank dar.
 *
 * @property coupon Der Coupon, das mit der Location verknüpft ist.
 * @property location Die Location, mit dem der Coupon verknüpft ist.
 */
@Entity
data class CouponWithLocation(
    @Embedded val coupon: Coupon,
    @Relation(
        parentColumn = "location_id",
        entityColumn = "location_id",
    )
    val location: Location,
) {
    /**
     * Diese Methode konvertiert das CouponWithLocation-Objekt in ein SimpleListItem-Objekt für die
     * Anzeige in einer RecyclerView.
     *
     * @param resources Die Ressourcen.
     * @return Ein SimpleListItem-Objekt, das die Informationen des Locations enthält.
     */
    suspend fun toListItem(resources: Resources, addressDao: AddressDao): SimpleListItem =
        SimpleListItem(
            location.title,
            location.getAddress(addressDao, resources),
            R.drawable.ic_circle_local_activity,
            "10%"
        )

    /**
     * Diese Methode konvertiert das CouponWithLocation-Objekt in ein SimpleListItem-Objekt für
     * die Anzeige in einer RecyclerView.
     *
     * @param resources Die Ressourcen.
     * @return Ein SimpleListItem-Objekt, das die Informationen der Transaktion enthält.
     */
    fun toRedeemedCouponsListItem(resources: Resources): SimpleListItem =
        SimpleListItem(
            "",
            location.title,
            R.drawable.ic_circle_local_activity,
            coupon.getExpiredDateAsString(),
        )
}
