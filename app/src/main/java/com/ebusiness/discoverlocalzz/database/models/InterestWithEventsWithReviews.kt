package com.ebusiness.discoverlocalzz.data.models

import android.content.Context
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import com.ebusiness.discoverlocalzz.data.CategoryListItem
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface

/**
 * Die Klasse InterestWithEventsWithReviews repräsentiert eine Interessenkategorie zusammen mit den dazugehörigen
 * Veranstaltungen und Bewertungen in der Room-Datenbank.
 *
 * @property interest Das Interesse, das diese Kategorie repräsentiert.
 * @property events Eine Liste von Veranstaltungen mit Bewertungen, die diesem Interesse zugeordnet sind.
 */
@Entity
data class InterestWithEventsWithReviews(
    @Embedded val interest: Interest,
    @Relation(
        parentColumn = "interest_id",
        entityColumn = "event_id",
        associateBy = Junction(EventInterest::class),
        entity = Event::class,
    )
    val events: List<EventWithReviews>,
) {
    /**
     * Konvertiert diese Interessenkategorie in ein [CategoryListItem] für die Anzeige in einer RecyclerView.
     *
     * @param context Der Android-Anwendungskontext.
     * @param helperInterface Ein [RecyclerViewHelperInterface] zur Behandlung von Klickereignissen.
     * @return Ein [CategoryListItem], das diese Interessenkategorie repräsentiert.
     */
    fun toListItem(
        context: Context,
        helperInterface: RecyclerViewHelperInterface,
    ): CategoryListItem =
        CategoryListItem(
            interest.name,
            events.map { it.toListItem(context) },
            helperInterface,
        )
}
