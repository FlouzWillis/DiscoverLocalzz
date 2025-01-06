package com.ebusiness.discoverlocalzz.helpers

import android.widget.ImageView
import com.ebusiness.discoverlocalzz.R
import kotlin.math.min

/**
 * Hilfsklasse zur Anzeige von Sternbewertungen.
 */
object StarView {
    private const val HALF_STAR_THRESHOLD = 0.5

    /**
     * Füllt die Sternsymbole entsprechend der Bewertung.
     *
     * @param rating Die Bewertung im Bereich von 0 bis 5.
     * @param stars Eine Liste von ImageView-Elementen, die die Sternsymbole darstellen.
     */
    fun fillStars(
        rating: Float,
        stars: List<ImageView>,
    ) {
        val filledStars = min(rating.toInt(), stars.size)
        val hasHalfStar = rating % 1 >= HALF_STAR_THRESHOLD
        for (star in stars) star.setImageResource(R.drawable.ic_small_star)
        for (star in stars.subList(0, filledStars)) star.setImageResource(
            R.drawable.ic_small_star_filled,
        )
        if (hasHalfStar && filledStars < stars.size) {
            stars[filledStars].setImageResource(
                R.drawable.ic_small_star_half,
            )
        }
    }
}
