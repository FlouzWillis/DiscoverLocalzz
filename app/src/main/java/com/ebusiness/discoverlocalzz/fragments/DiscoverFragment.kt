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
import com.ebusiness.discoverlocalzz.activities.EventActivity
import com.ebusiness.discoverlocalzz.activities.MainActivity
import com.ebusiness.discoverlocalzz.adapters.CategoryListAdapter
import com.ebusiness.discoverlocalzz.adapters.EmptyAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.data.AppDatabase
import com.ebusiness.discoverlocalzz.data.models.AccountInterest
import com.ebusiness.discoverlocalzz.data.models.InterestWithEventsWithReviews
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragment zur Entdeckung und Anzeige von Veranstaltungen basierend auf Benutzerinteressen.
 */
class DiscoverFragment : Fragment() {
    private var interests: List<InterestWithEventsWithReviews> = listOf()

    /**
     * Erstellt die Ansicht für das Entdeckungsfragment und initialisiert Elemente wie Suchleiste und Eventliste.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val root = inflater.inflate(R.layout.fragment_discover, container, false)

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

        val recyclerView = root.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = LoadingAdapter()
        CoroutineScope(Dispatchers.Main).launch {
            interests =
                reorderCategories(
                    AppDatabase.getInstance(requireContext()).interestDao().getAll(),
                    AppDatabase.getInstance(requireContext()).accountInterestDao()
                        .getUserInterests(Preferences.getUserId(requireContext())),
                )
            recyclerView.adapter =
                if (interests.isNotEmpty()) {
                    CategoryListAdapter(
                        interests.mapIndexed { index, event ->
                            event.toListItem(
                                requireContext(),
                                object : RecyclerViewHelperInterface {
                                    override fun onItemClicked(position: Int) {
                                        onItemClicked(index, position)
                                    }
                                },
                            )
                        },
                    )
                } else {
                    EmptyAdapter()
                }
        }

        return root
    }

    private fun reorderCategories(
        allCategories: List<InterestWithEventsWithReviews>,
        userInterests: List<AccountInterest>,
    ): List<InterestWithEventsWithReviews> =
        allCategories.sortedBy { (interest, _) ->
            if (userInterests.any { it.interestId == interest.id }) {
                0
            } else {
                1
            }
        }

    fun onSearchClicked() {

    }

    /**
     * Behandelt Klickereignisse auf Veranstaltungen und leitet zum entsprechenden EventActivity weiter.
     */
    internal fun onItemClicked(
        categoryPosition: Int,
        eventPosition: Int,
    ) {
        if (interests.size > categoryPosition && interests[categoryPosition].events.size > eventPosition) {
            requireContext().startActivity(
                Intent(requireContext(), EventActivity::class.java).apply {
                    putExtra(
                        EventActivity.EVENT_INTENT_EXTRA,
                        interests[categoryPosition].events[eventPosition].event.id,
                    )
                },
            )
        }
    }
}
