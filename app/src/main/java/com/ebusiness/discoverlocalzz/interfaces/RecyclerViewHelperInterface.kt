package com.ebusiness.discoverlocalzz.interfaces

/**
 * Interface zur Bereitstellung einer Callback-Funktion für Klickereignisse in RecyclerView-Elementen.
 */
interface RecyclerViewHelperInterface {
    /**
     * Wird aufgerufen, wenn ein Element in einem RecyclerView angeklickt wird.
     *
     * @param position Die Position des angeklickten Elements im RecyclerView.
     */
    fun onItemClicked(position: Int)
}
