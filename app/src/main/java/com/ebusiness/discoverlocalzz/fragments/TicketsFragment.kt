package com.ebusiness.discoverlocalzz.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.activities.MainActivity
import com.ebusiness.discoverlocalzz.activities.TicketActivity
import com.ebusiness.discoverlocalzz.adapters.EmptyAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.data.AppDatabase
import com.ebusiness.discoverlocalzz.data.models.TicketWithEvent
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragment für die Anzeige und Filterung von Benutzertickets.
 */
class TicketsFragment : Fragment(), RecyclerViewHelperInterface {
    private var tickets: List<TicketWithEvent> = listOf()
    private lateinit var titleFilter: Chip
    private lateinit var priceFilter: Chip
    private lateinit var recyclerView: RecyclerView
    private var previousFilter: Byte = -1
    private var reversed = false

    /**
     * Erstellt die Ansicht für das Ticketsfragment, initialisiert Filter und lädt die Ticketliste.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val root = inflater.inflate(R.layout.fragment_tickets, container, false)

        MainActivity.setupSearchView(root)
        root.findViewById<SearchView>(R.id.searchView).setOnQueryTextListener (object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                onSearchClicked()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        titleFilter = root.findViewById(R.id.title_filter)
        priceFilter = root.findViewById(R.id.price_filter)

        recyclerView = root.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = LoadingAdapter()
        CoroutineScope(Dispatchers.Main).launch {
            tickets =
                AppDatabase.getInstance(requireContext()).ticketDao().getAllTickets()
//                    .getAll(Preferences.getUserId(requireContext()))
            titleFilter.setOnClickListener {
                selectFilter(TITLE_FILTER)
            }
            priceFilter.setOnClickListener {
                selectFilter(PRICE_FILTER)
            }
            selectFilter(TITLE_FILTER)
        }

        return root
    }

    /**
     * Behandelt Klickereignisse auf Ticketelemente und leitet zum Detailbereich des ausgewählten Tickets weiter.
     */
    override fun onItemClicked(position: Int) {
        if (tickets.size > position) {
            requireContext().startActivity(
                Intent(requireContext(), TicketActivity::class.java).apply {
                    putExtra(TicketActivity.TICKET_INTENT_EXTRA, tickets[position].ticket.id)
                },
            )
        }
    }

    private fun update(newTickets: List<TicketWithEvent>) {
        tickets = newTickets
        recyclerView.adapter =
            if (tickets.isNotEmpty()) {
                SimpleListAdapter(
                    tickets.map { it.toListItem(resources) },
                    this@TicketsFragment,
                )
            } else {
                EmptyAdapter()
            }
    }

    private fun selectFilter(filter: Byte) {
        titleFilter.chipIcon = null
        priceFilter.chipIcon = null
        reversed = if (previousFilter == filter) !reversed else false
        val icon =
            if (reversed) {
                R.drawable.ic_keyboard_arrow_up
            } else {
                R.drawable.ic_keyboard_arrow_down
            }
        when (filter) {

            TITLE_FILTER -> {
                titleFilter.setChipIconResource(icon)
                update(
                    if (reversed) {
                        tickets.sortedBy { it.event.title }
                    } else {
                        tickets.sortedByDescending { it.event.title }
                    },
                )
            }

            PRICE_FILTER -> {
                priceFilter.setChipIconResource(icon)
                update(
                    if (reversed) {
                        tickets.sortedBy { it.event.price }
                    } else {
                        tickets.sortedByDescending { it.event.price }
                    },
                )
            }
        }
        previousFilter = filter
    }

    fun onSearchClicked() {

    }

    companion object {
        private const val DATE_FILTER: Byte = 0
        private const val TITLE_FILTER: Byte = 1
        private const val PRICE_FILTER: Byte = 2
    }
}
