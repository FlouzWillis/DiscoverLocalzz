package com.ebusiness.discoverlocalzz.database.models

import android.content.Context
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.ebusiness.discoverlocalzz.database.EventListItem
import com.ebusiness.discoverlocalzz.helpers.Base64

/**
 * Die Klasse EventWithReviews repräsentiert ein Ereignis zusammen mit seinen Bewertungen
 * in der Room-Datenbank.
 *
 * @property event Das Ereignis.
 * @property reviews Eine Liste von Bewertungen für das Ereignis.
 */
@Entity
data class EventWithReviews(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "event_id",
        entityColumn = "event_id",
    )
    val reviews: List<Review>,
) {
    /**
     * Wandelt das Ereignis mit Bewertungen in ein EventListItem-Objekt um.
     *
     * @param context Der Kontext des Aufrufers.
     * @return Ein EventListItem-Objekt, das aus dem Ereignis und seinen Bewertungen erstellt wurde.
     */
    fun toListItem(context: Context): EventListItem {
        val averageRating = reviews.map { it.stars }.average().toFloat()
        return EventListItem(
            averageRating,
            event.title,
            event.getSummary(context.resources),
            Base64.decodeImage(context, event.image),
        )
    }

    fun getAverageRating(): Float {
        return reviews.map { it.stars }.average().toFloat()
    }
}
