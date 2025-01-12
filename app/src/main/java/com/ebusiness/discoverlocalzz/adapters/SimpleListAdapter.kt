package com.ebusiness.discoverlocalzz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface

/**
 * Ein RecyclerView-Adapter, der einfache Listenelemente mit Titel, Zusammenfassung und Icon anzeigt.
 */
class SimpleListAdapter(
    /**
     * Die Listenelemente.
     */
    val items: List<SimpleListItem>,
    private val helperInterface: RecyclerViewHelperInterface,
) : RecyclerView.Adapter<SimpleListAdapter.ViewHolder>() {
    /**
     * Erstellt einen neuen ViewHolder für einfache Listenelemente.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_simple, parent, false),
        )

    /**
     * Bindet Daten an einen ViewHolder, um ein einfaches Listenelement darzustellen.
     */
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        if (items[position].label.isEmpty()) {
            holder.label.visibility = View.GONE
        } else {
            holder.label.visibility = View.VISIBLE
            holder.label.text = items[position].label
        }
        if (items[position].title.isEmpty()) {
            holder.title.visibility = View.GONE
        } else {
            holder.title.visibility = View.VISIBLE
            holder.title.text = items[position].title
        }
        if (items[position].summary.isEmpty()) {
            holder.summary.visibility = View.GONE
        } else {
//            holder.summary.visibility = View.VISIBLE
//            holder.summary.text = items[position].summary
        }
        holder.drawable.setImageResource(items[position].icon)
        holder.itemView.setOnClickListener { helperInterface.onItemClicked(position) }
    }

    /**
     * Gibt die Anzahl der Listenelemente zurück.
     */
    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder-Klasse für die Darstellung einfacher Listenelemente.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /**
         * ImageView zur Anzeige eines Bildes.
         */
        val drawable: ImageView = view.findViewById(R.id.drawable)

        /**
         * TextView zur Anzeige des Labels.
         */
        val label: TextView = view.findViewById(R.id.label)

        /**
         * TextView zur Anzeige des Titels.
         */
        val title: TextView = view.findViewById(R.id.title)

        /**
         * TextView zur Anzeige einer Zusammenfassung.
         */
        val summary: TextView = view.findViewById(R.id.summary)
    }
}
