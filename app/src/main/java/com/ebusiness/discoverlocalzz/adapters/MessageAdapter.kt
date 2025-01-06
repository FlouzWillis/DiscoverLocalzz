package com.ebusiness.discoverlocalzz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Ein abstrakter Adapter für die Anzeige einer einzelnen Nachricht oder eines Zustands in einer RecyclerView.
 */
abstract class MessageAdapter(private val layout: Int) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    /**
     * Erstellt einen neuen ViewHolder für die spezifizierte Layout-Ressource.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(layout, parent, false),
        )

    /**
     * Bindet den ViewHolder an den spezifischen Zustand, jedoch ohne zusätzliche Logik (Standardimplementierung).
     */
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        // Do nothing.
    }

    /**
     * Gibt an, dass immer nur ein Element in der RecyclerView vorhanden ist.
     */
    override fun getItemCount(): Int = 1

    /**
     * ViewHolder-Klasse, die das Layout für die Nachricht oder den Zustand enthält.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
