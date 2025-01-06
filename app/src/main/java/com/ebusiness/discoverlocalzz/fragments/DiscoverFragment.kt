package com.ebusiness.discoverlocalzz.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.ebusiness.discoverlocalzz.customview.showCustomAlertDialog
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
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val root = inflater.inflate(R.layout.fragment_discover, container, false)

        MainActivity.setupSearchView(root)
        root.findViewById<SearchView>(R.id.searchView)
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    onSearchClicked()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

        val categoryFilterButton = root.findViewById<Button>(R.id.category_filter_button)
        categoryFilterButton.setOnClickListener {
            onClickCategoryFilterButton()
        }

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

            if (getSelectedCategories().isNotEmpty()) {
                interests =
                    interests.filter { getSelectedCategories().contains(it.interest.name) }
            }

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

    private fun getSelectedCategories(): Set<String> {
        val sharedPreferences =
            requireContext().getSharedPreferences("FilterPreferences", MODE_PRIVATE)
        return sharedPreferences.getStringSet("SelectedCategories", emptySet()) ?: emptySet()
    }

    private fun onClickCategoryFilterButton() {
        CoroutineScope(Dispatchers.Main).launch {
            val allInterest = mutableListOf<String>()
            val interests =
                AppDatabase.getInstance(requireContext()).interestDao().getAllInterests()
            interests.forEach { interest ->
                allInterest.add(interest.name)
            }

            val reorderCategories = reorderCategories(
                AppDatabase.getInstance(requireContext()).interestDao().getAll(),
                AppDatabase.getInstance(requireContext()).accountInterestDao()
                    .getUserInterests(Preferences.getUserId(requireContext())),
            )

            showCustomAlertDialog(
                context = requireContext(),
                title = "Kategorie",
                description = "Kategorie wählen",
                checkBoxTexts = allInterest,
                onConfirm = { selectedItems ->

                    val sharedPreferences =
                        requireContext().getSharedPreferences("FilterPreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putStringSet("SelectedCategories", selectedItems.toSet())
                    editor.apply()

                    val filteredInterests =
                        reorderCategories.filter { selectedItems.contains(it.interest.name) }

                    val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
                    recyclerView?.adapter = if (filteredInterests.isNotEmpty()) {
                        CategoryListAdapter(
                            filteredInterests.mapIndexed { index, event ->
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
                        CategoryListAdapter(
                            reorderCategories.mapIndexed { index, event ->
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
                    }
                },
                onClear = {
                    val sharedPreferences =
                        requireContext().getSharedPreferences("FilterPreferences", MODE_PRIVATE)
                    sharedPreferences.edit().remove("SelectedCategories").apply()

                    val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
                    recyclerView?.adapter = if (reorderCategories.isNotEmpty()) {
                        CategoryListAdapter(
                            reorderCategories.mapIndexed { index, event ->
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
                },
                preSelectedItems = getSelectedCategories().toList()
            )
        }
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
