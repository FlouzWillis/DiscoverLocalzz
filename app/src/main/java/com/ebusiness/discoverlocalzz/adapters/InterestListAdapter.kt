package com.ebusiness.discoverlocalzz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.models.Interest
import com.ebusiness.discoverlocalzz.helpers.Base64
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface

/**
 * Adapter für die Darstellung von Interessen in einer RecyclerView.
 */
class InterestListAdapter(
    private val items: List<Interest>,
    private val helperInterface: RecyclerViewHelperInterface,
) : RecyclerView.Adapter<InterestListAdapter.ViewHolder>() {
    private val states = Array(items.size) { _ -> false }

    /**
     * Erstellt eine neue ViewHolder-Instanz für die Darstellung einer Interesse.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item_interest, parent, false),
        )

    /**
     * Bindet die Daten einer Interesse an eine ViewHolder-Instanz.
     */
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.gradient.background =
            ResourcesCompat.getDrawable(
                holder.gradient.resources,
                if (states[position]) R.drawable.gradient_primary else R.drawable.gradient_dim,
                holder.gradient.context.theme,
            )
        holder.title.text = items[position].name
        holder.image.setImageDrawable(Base64.decodeImage(holder.image.context, items[position].image))
        holder.itemView.setOnClickListener { helperInterface.onItemClicked(position) }
    }

    /**
     * Gibt die Anzahl der Interessen zurück.
     */
    override fun getItemCount(): Int = items.size

    /**
     * Ändert den Auswahlzustand am gegebenen Index.
     */
    fun toggle(position: Int) {
        states[position] = !states[position]
        notifyItemChanged(position)
    }

    /**
     * Selektiert die gegebenen Interessens-Ids.
     */
    fun select(interestIds: List<Long>) {
        for (interestId in interestIds) {
            val index = items.indexOfFirst { it.id == interestId }
            if (index > -1 && index < states.size) states[index] = true
        }
    }

    /**
     * Gibt die ausgewählten Interessen zurück.
     */
    fun getSelected(): List<Interest> = items.filterIndexed { index, _ -> states[index] }

    /**
     * ViewHolder für die Darstellung einer Interesse.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /**
         * ImageView für das Interessenbild.
         */
        val image: ImageView = view.findViewById(R.id.image)

        /**
         * View für den Farbverlauf.
         */
        val gradient: View = view.findViewById(R.id.gradient)

        /**
         * TextView für den Interessennamen.
         */
        val title: TextView = view.findViewById(R.id.title)
    }
}
