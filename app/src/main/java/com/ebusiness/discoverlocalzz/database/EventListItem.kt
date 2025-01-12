package com.ebusiness.discoverlocalzz.database

import android.graphics.drawable.Drawable

/**
 * Datenklasse, die ein Veranstaltungsliste-Element mit einer Bewertung, einem Titel, einer
 * Zusammenfassung und einem Hintergrund-Drawable repräsentiert.
 *
 * @param rating Die Bewertung der Veranstaltung.
 * @param title Der Titel der Veranstaltung.
 * @param summary Die Zusammenfassung oder Beschreibung der Veranstaltung.
 * @param background Das mit der Veranstaltung verbundene Hintergrund-Drawable.
 */
data class EventListItem(
    val rating: Float,
    val title: String,
    val summary: String,
    val background: Drawable,
)
