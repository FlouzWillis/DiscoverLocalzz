package com.ebusiness.discoverlocalzz.database

import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface

/**
 * Datenklasse, die ein Kategorieliste-Element mit einem Titel, einer Liste von
 * Veranstaltungselementen und einem Hilfeschnittstellen-Interface repräsentiert.
 *
 * @param title Der Titel der Kategorie.
 * @param list Die Liste der Veranstaltungselemente in der Kategorie.
 * @param helperInterface Die mit der Kategorie verbundene RecyclerViewHelperInterface.
 */
data class CategoryListItem(
    val title: String,
    val list: List<LocationListItem>,
    val helperInterface: RecyclerViewHelperInterface,
)
