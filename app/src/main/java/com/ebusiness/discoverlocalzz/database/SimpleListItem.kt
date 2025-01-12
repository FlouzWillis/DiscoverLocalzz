package com.ebusiness.discoverlocalzz.database

/**
 * Datenklasse, die ein einfaches Listen-Element mit einem Titel, einer Zusammenfassung und einem Symbol repräsentiert.
 *
 * @param title Der Titel des Listen-Elements.
 * @param summary Die Zusammenfassung oder Beschreibung des Listen-Elements.
 * @param icon Die Ressourcen-ID des mit dem Listen-Element verbundenen Symbols.
 * @param label Die Detailüberschrift des Listen-Elements.
 */
data class SimpleListItem(
    val title: String = "",
    val summary: String = "",
    val icon: Int = android.R.color.transparent,
    val label: String = "",
)
