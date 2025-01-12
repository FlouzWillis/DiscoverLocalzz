package com.ebusiness.discoverlocalzz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.EventListItem
import com.ebusiness.discoverlocalzz.helpers.StarView
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface

/**
 * Adapter für eine RecyclerView, der Veranstaltungselemente anzeigt.
 */
class EventListAdapter(
    private val items: List<EventListItem>,
    private val helperInterface: RecyclerViewHelperInterface,
) : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {
    /**
     * Erstellt einen neuen ViewHolder für Veranstaltungselemente.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_event, parent, false),
        )

    /**
     * Bindet Daten an einen ViewHolder, um ein Veranstaltungselement darzustellen.
     */
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.image.setImageDrawable(items[position].background)
        StarView.fillStars(items[position].rating, holder.stars)
        holder.title.text = items[position].title
        holder.summary.text = ""
        holder.itemView.setOnClickListener { helperInterface.onItemClicked(position) }
    }

    /**
     * Gibt die Anzahl der Veranstaltungselemente zurück.
     */
    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder-Klasse für Veranstaltungselemente.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /**
         * ImageView zur Anzeige des Bilds der Veranstaltung.
         */
        val image: ImageView = view.findViewById(R.id.image)

        /**
         * Liste von ImageView-Elementen, die Sterne zur Bewertung der Veranstaltung repräsentieren.
         */
        val stars: List<ImageView> =
            listOf(
                view.findViewById(R.id.first_star),
                view.findViewById(R.id.second_star),
                view.findViewById(R.id.third_star),
                view.findViewById(R.id.fourth_star),
                view.findViewById(R.id.fifth_star),
            )

        /**
         * TextView zur Anzeige des Titels der Veranstaltung.
         */
        val title: TextView = view.findViewById(R.id.title)

        /**
         * TextView zur Anzeige einer Zusammenfassung der Veranstaltung.
         */
        val summary: TextView = view.findViewById(R.id.summary)
    }
}
