package com.ebusiness.discoverlocalzz.database.models

import android.content.Context
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.ebusiness.discoverlocalzz.database.LocationListItem
import com.ebusiness.discoverlocalzz.helpers.Base64

/**
 * Die Klasse LocationWithReviews repräsentiert ein Ereignis zusammen mit seinen Bewertungen
 * in der Room-Datenbank.
 *
 * @property location Das Ereignis.
 * @property reviews Eine Liste von Bewertungen für das Ereignis.
 */
@Entity
data class LocationWithReviews(
    @Embedded val location: Location,
    @Relation(
        parentColumn = "location_id",
        entityColumn = "location_id",
    )
    val reviews: List<Review>,
) {
    /**
     * Wandelt das Ereignis mit Bewertungen in ein LocationListItem-Objekt um.
     *
     * @param context Der Kontext des Aufrufers.
     * @return Ein LocationListItem-Objekt, das aus dem Ereignis und seinen Bewertungen erstellt wurde.
     */
    fun toListItem(context: Context): LocationListItem {
        val averageRating = reviews.map { it.stars }.average().toFloat()
        return LocationListItem(
            averageRating,
            location.title,
            location.getSummary(context.resources),
            Base64.decodeImage(context, location.image),
        )
    }

    fun getAverageRating(): Float {
        return reviews.map { it.stars }.average().toFloat()
    }
}
