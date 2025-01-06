package com.ebusiness.discoverlocalzz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.data.CategoryListItem

/**
 * Adapter für eine RecyclerView, die Kategorien mit zugehörigen Events anzeigt.
 */
class CategoryListAdapter(
    private val items: List<CategoryListItem>,
) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    /**
     * Erstellt einen neuen ViewHolder für Kategorieelemente.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_category, parent, false),
        )

    /**
     * Bindet Daten an einen ViewHolder, um eine Kategorie mit ihren Events darzustellen.
     */
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.title.text = items[position].title
        holder.list
        holder.list.layoutManager =
            LinearLayoutManager(
                holder.list.context,
                RecyclerView.HORIZONTAL,
                false,
            )
        holder.list.adapter =
            EventListAdapter(
                items[position].list,
                items[position].helperInterface,
            )
    }

    /**
     * Gibt die Anzahl der Kategorieelemente zurück.
     */
    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder-Klasse für Kategorieelemente.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /**
         * TextView zur Anzeige des Kategorietitels.
         */
        val title: TextView = view.findViewById(R.id.title)

        /**
         * RecyclerView zur Anzeige einer Liste von Elementen in der Kategorie.
         */
        val list: RecyclerView = view.findViewById(R.id.list)
    }
}
