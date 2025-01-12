package com.ebusiness.discoverlocalzz.fragments

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
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
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.models.AccountInterest
import com.ebusiness.discoverlocalzz.database.models.Event
import com.ebusiness.discoverlocalzz.database.models.EventWithReviews
import com.ebusiness.discoverlocalzz.database.models.InterestWithEventsWithReviews
import com.ebusiness.discoverlocalzz.database.models.Review
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

/**
 * Fragment zur Entdeckung und Anzeige von Veranstaltungen basierend auf Benutzerinteressen.
 */
class DiscoverFragment : Fragment() {
    private var interests: List<InterestWithEventsWithReviews> = listOf()
    private lateinit var filterSharedPreferences: SharedPreferences
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
        filterSharedPreferences = requireContext().getSharedPreferences("FilterPreferences", MODE_PRIVATE)
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

        val locationFilterButton = root.findViewById<Button>(R.id.location_filter_button)
        locationFilterButton.setOnClickListener {
            onClickLocationFilterButton()
        }

        val ratingFilterButton = root.findViewById<Button>(R.id.rating_filter_button)
        ratingFilterButton.setOnClickListener {
            onClickRatingFilterButton()
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
        return filterSharedPreferences.getStringSet("SelectedCategories", emptySet()) ?: emptySet()
    }

    private fun getSelectedLocations(): Set<String> {
        return filterSharedPreferences.getStringSet("SelectedCities", emptySet()) ?: emptySet()
    }

    private fun getSelectedRating(): String? {
        return filterSharedPreferences.getString("SelectedRating", null)
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
                optionsList = allInterest,
                onConfirm = { selectedItems ->

                    filterSharedPreferences.edit().putStringSet("SelectedCategories", selectedItems.toSet()).apply()
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
                    filterSharedPreferences.edit().remove("SelectedCategories").apply()

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

    private fun onClickLocationFilterButton() {
        CoroutineScope(Dispatchers.Main).launch {
            val events = AppDatabase.getInstance(requireContext()).eventDao().getAll()
            val cityCountMap = mutableMapOf<String, Int>()

            events.forEach { location ->
                val city = location.event.city
                if (city.isNotBlank()) {
                    val currentCount = cityCountMap[city] ?: 0
                    cityCountMap[city] = currentCount + 1
                }
            }

            val allCitiesWithCount = cityCountMap.map { (city, count) ->
                "$city ($count)"
            }

            val reorderCategories = reorderCategories(
                AppDatabase.getInstance(requireContext()).interestDao().getAll(),
                AppDatabase.getInstance(requireContext()).accountInterestDao()
                    .getUserInterests(Preferences.getUserId(requireContext())),
            )

            showCustomAlertDialog(
                context = requireContext(),
                title = "Orte",
                description = "Ort wählen",
                optionsList = allCitiesWithCount,
                onConfirm = { selectedItems ->

                    filterSharedPreferences.edit().putStringSet("SelectedCities", selectedItems.toSet())
                        .apply()

                    val filteredEvents = mutableListOf<EventWithReviews>()

                    reorderCategories.forEach { category ->
                        category.events.forEach { event ->
                            if (selectedItems.contains(event.event.city)) {
                                filteredEvents.add(event)
                            }
                        }
                    }

                    val allCategories = reorderCategories

                    allCategories.forEach { catergory ->
                        filteredEvents.forEach { event ->

                            reorderCategories.forEach {
                                if (it.events.contains(event) && catergory == it) {
                                    catergory.events.add(event)
                                }
                            }
                        }
                    }

                    val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
                    recyclerView?.adapter = if (allCategories.isNotEmpty()) {
                        CategoryListAdapter(
                            allCategories.mapIndexed { index, event ->
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
                    } else if (allCategories.none { it.events.isEmpty() }) {
                        EmptyAdapter()
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

                    filterSharedPreferences.edit().remove("SelectedCities").apply()

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
                preSelectedItems = getSelectedLocations().toList()
            )
        }
    }

    private fun onClickRatingFilterButton() {

        CoroutineScope(Dispatchers.Main).launch {
            val reorderCategories = reorderCategories(
                AppDatabase.getInstance(requireContext()).interestDao().getAll(),
                AppDatabase.getInstance(requireContext()).accountInterestDao()
                    .getUserInterests(Preferences.getUserId(requireContext())),
            )

            showCustomAlertDialog(
                requireContext(),
                requireContext().resources.getString(R.string.ratings_title),
                "",
                listOf("3+", "4+", "5"),
                onConfirm = { selectedItems ->
                    filterSharedPreferences.edit().putString("SelectedRating", selectedItems.first())
                        .apply()
                    val selectedRating = selectedItems.first()
                    val filteredInterestsWithEvents: List<InterestWithEventsWithReviews> = when (selectedRating) {
                        "3+" -> {
                            reorderCategories.mapNotNull { interestWithEventsWithReviews ->
                                val filteredEvents = interestWithEventsWithReviews.events.filter { eventWithReviews ->
                                    val averageRating = eventWithReviews.getAverageRating()
                                    averageRating >= 3.0f
                                }

                                if (filteredEvents.isNotEmpty()) {
                                    interestWithEventsWithReviews.copy(events = filteredEvents.toMutableList())
                                } else {
                                    null
                                }
                            }
                        }
                        "4+" -> {
                            reorderCategories.mapNotNull { interestWithEventsWithReviews ->
                                val filteredEvents = interestWithEventsWithReviews.events.filter { eventWithReviews ->
                                    val averageRating = eventWithReviews.getAverageRating()
                                    averageRating >= 4.0f
                                }

                                if (filteredEvents.isNotEmpty()) {
                                    interestWithEventsWithReviews.copy(events = filteredEvents.toMutableList())
                                } else {
                                    null
                                }
                            }
                        }
                        "5" -> {
                            reorderCategories.mapNotNull { interestWithEventsWithReviews ->
                                val filteredEvents = interestWithEventsWithReviews.events.filter { eventWithReviews ->
                                    val averageRating = eventWithReviews.getAverageRating()
                                    averageRating == 5.0f
                                }

                                if (filteredEvents.isNotEmpty()) {
                                    interestWithEventsWithReviews.copy(events = filteredEvents.toMutableList())
                                } else {
                                    null
                                }
                            }
                        }
                        else -> {
                            emptyList()
                        }
                    }

                    val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
                    recyclerView?.adapter = if (filteredInterestsWithEvents.isNotEmpty()) {
                        CategoryListAdapter(
                            filteredInterestsWithEvents.mapIndexed { index, event ->
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
                onClear = {
                    filterSharedPreferences.edit().remove("SelectedRating").apply()

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
                preSelectedItems = if (getSelectedRating() != null) listOf(getSelectedRating()!!) else emptyList() ,
                useRadioButton = true
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
