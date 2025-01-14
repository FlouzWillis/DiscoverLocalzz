package com.ebusiness.discoverlocalzz.database.models

import android.content.res.Resources
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.database.dao.AddressDao

/**
 * Die Klasse TicketWithLocation stellt eine Beziehung zwischen einem Ticket und einem Location in der Room-Datenbank dar.
 *
 * @property coupon Das Ticket, das mit dem Location verknüpft ist.
 * @property location Das Location, mit dem das Ticket verknüpft ist.
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
     * Diese Methode konvertiert das TicketWithLocation-Objekt in ein SimpleListItem-Objekt für die
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
     * Diese Methode konvertiert das TicketWithLocation-Objekt in ein SimpleListItem-Objekt für
     * die Anzeige in einer RecyclerView.
     *
     * @param resources Die Ressourcen.
     * @return Ein SimpleListItem-Objekt, das die Informationen der Transaktion enthält.
     */
    fun toRedeemedCouponsListItem(resources: Resources): SimpleListItem =
        SimpleListItem(
            "",
            location.title,
            R.drawable.ic_square_credit_card,
            coupon.getExpiredDateAsString(),
        )
}
