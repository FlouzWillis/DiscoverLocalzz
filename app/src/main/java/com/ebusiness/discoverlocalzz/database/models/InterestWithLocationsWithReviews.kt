package com.ebusiness.discoverlocalzz.database.models

import android.content.Context
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import com.ebusiness.discoverlocalzz.database.CategoryListItem
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface

/**
 * Die Klasse InterestWithLocationsWithReviews repräsentiert eine Interessenkategorie zusammen mit den dazugehörigen
 * Veranstaltungen und Bewertungen in der Room-Datenbank.
 *
 * @property interest Das Interesse, das diese Kategorie repräsentiert.
 * @property locations Eine Liste von Veranstaltungen mit Bewertungen, die diesem Interesse zugeordnet sind.
 */
@Entity
data class InterestWithLocationsWithReviews(
    @Embedded val interest: Interest,
    @Relation(
        parentColumn = "interest_id",
        entityColumn = "location_id",
        associateBy = Junction(LocationInterest::class),
        entity = Location::class,
    )
    var locations: MutableList<LocationWithReviews>,
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
            locations.map { it.toListItem(context) },
            helperInterface,
        )
}
