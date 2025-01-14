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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.activities.LocationActivity
import com.ebusiness.discoverlocalzz.activities.MainActivity
import com.ebusiness.discoverlocalzz.adapters.CategoryListAdapter
import com.ebusiness.discoverlocalzz.adapters.EmptyAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.customview.showCustomAlertDialog
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.models.AccountInterest
import com.ebusiness.discoverlocalzz.database.models.InterestWithLocationsWithReviews
import com.ebusiness.discoverlocalzz.database.models.LocationWithReviews
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragment zur Entdeckung und Anzeige von Veranstaltungen basierend auf Benutzerinteressen.
 */
class DiscoverFragment : Fragment() {
    private var interests: List<InterestWithLocationsWithReviews> = listOf()
    private lateinit var filterSharedPreferences: SharedPreferences
    /**
     * Erstellt die Ansicht für das Entdeckungsfragment und initialisiert Elemente wie Suchleiste und Locationliste.
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
        root.findViewById<SearchView>(R.id.searchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    onSearchClicked()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        updateInterestList(interests)
                    }
                    return false
                }
            })

            setOnCloseListener {
                this.setQuery("", false)
                updateInterestList(interests)
                true
            }
        }

        val categoryFilterButton = root.findViewById<Button>(R.id.category_filter_button)
        categoryFilterButton.setOnClickListener {
            onClickCategoryFilterButton()
        }

//        val locationFilterButton = root.findViewById<Button>(R.id.location_filter_button)
//        locationFilterButton.setOnClickListener {
//            onClickLocationFilterButton()
//        }

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
                        interests.mapIndexed { index, location ->
                            location.toListItem(
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
        allCategories: List<InterestWithLocationsWithReviews>,
        userInterests: List<AccountInterest>,
    ): List<InterestWithLocationsWithReviews> =
        allCategories.sortedBy { (interest, _) ->
            if (userInterests.any { it.interestId == interest.id }) {
                0
            } else {
                1
            }
        }

    fun onSearchClicked() {
        val searchView = requireView().findViewById<SearchView>(R.id.searchView)
        val searchQuery = searchView.query.toString()

        if (searchQuery.isEmpty()) {
            Toast.makeText(context, getString(R.string.name), Toast.LENGTH_SHORT).show()
            return
        }

        val filteredInterests = interests.mapNotNull { interestWithLocationsWithReviews ->
            val filteredLocations = interestWithLocationsWithReviews.locations.filter { locationWithReviews ->
                locationWithReviews.location.title.contains(searchQuery, ignoreCase = true)
            }

            if (filteredLocations.isNotEmpty()) {
                interestWithLocationsWithReviews.copy(locations = filteredLocations.toMutableList())
            } else {
                null
            }
        }

        updateInterestList(filteredInterests)
    }

    private fun updateInterestList(filteredInterests: List<InterestWithLocationsWithReviews>) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
        recyclerView?.adapter = if (filteredInterests.isNotEmpty()) {
            CategoryListAdapter(
                filteredInterests.mapIndexed { index, location ->
                    location.toListItem(
                        requireContext(),
                        object : RecyclerViewHelperInterface {
                            override fun onItemClicked(position: Int) {
                                onItemClicked(index, position)
                            }
                        }
                    )
                }
            )
        } else {
            EmptyAdapter()
        }
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
                            filteredInterests.mapIndexed { index, location ->
                                location.toListItem(
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
                            reorderCategories.mapIndexed { index, location ->
                                location.toListItem(
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
                            reorderCategories.mapIndexed { index, location ->
                                location.toListItem(
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

//    private fun onClickLocationFilterButton() {
//        CoroutineScope(Dispatchers.Main).launch {
//            val database = AppDatabase.getInstance(requireContext())
//            val locations = database.locationDao().getAll()
//            val cityCountMap = mutableMapOf<String, Int>()
//
//            locations.forEach { location ->
//                val city = location.address.city
//                if (city.isNotBlank()) {
//                    val currentCount = cityCountMap[city] ?: 0
//                    cityCountMap[city] = currentCount + 1
//                }
//            }
//
//            val allCitiesWithCount = cityCountMap.map { (city, count) ->
//                "$city ($count)"
//            }
//
//            val reorderCategories = reorderCategories(
//                AppDatabase.getInstance(requireContext()).interestDao().getAll(),
//                AppDatabase.getInstance(requireContext()).accountInterestDao()
//                    .getUserInterests(Preferences.getUserId(requireContext())),
//            )
//
//            showCustomAlertDialog(
//                context = requireContext(),
//                title = "Orte",
//                description = "Ort wählen",
//                optionsList = allCitiesWithCount,
//                onConfirm = { selectedItems ->
//
//                    filterSharedPreferences.edit()
//                        .putStringSet("SelectedCities", selectedItems.toSet())
//                        .apply()
//
//                    val filteredLocations = listOf<InterestWithLocationsWithReviews>()
//                    CoroutineScope(Dispatchers.Main).launch {
//                        reorderCategories.mapNotNull { interestWithLocationsWithReviews ->
//                            val filteredLocations = interestWithLocationsWithReviews.locations.filter { location ->
//                                database.addressDao().getAddressById(location.location.addressId)
//                                    ?.let {
//                                        if (selectedItems.contains(it.city)) {
//                                            filteredLocations.add(location)
//                                        }
//                                    }
//                            }
//
//                            if (filteredLocations.isNotEmpty()) {
//                                interestWithLocationsWithReviews.copy(locations = filteredLocations.toMutableList())
//                            } else {
//                                null
//                            }
//                        }
//                    }
//
//                    val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
//                    recyclerView?.adapter = if (reorderCategories.isNotEmpty()) {
//                        CategoryListAdapter(
//                            filteredLocations.mapIndexed { index, location ->
//                                location.toListItem(
//                                    requireContext(),
//                                    object : RecyclerViewHelperInterface {
//                                        override fun onItemClicked(position: Int) {
//                                            onItemClicked(index, position)
//                                        }
//                                    },
//                                )
//                            },
//                        )
//                    } else if (reorderCategories.none { it.locations.isEmpty() }) {
//                        EmptyAdapter()
//                    } else {
//                        CategoryListAdapter(
//                            reorderCategories.mapIndexed { index, location ->
//                                location.toListItem(
//                                    requireContext(),
//                                    object : RecyclerViewHelperInterface {
//                                        override fun onItemClicked(position: Int) {
//                                            onItemClicked(index, position)
//                                        }
//                                    },
//                                )
//                            },
//                        )
//                    }
//                },
//                onClear = {
//
//                    filterSharedPreferences.edit().remove("SelectedCities").apply()
//
//                    val recyclerView = view?.findViewById<RecyclerView>(R.id.list)
//                    recyclerView?.adapter = if (reorderCategories.isNotEmpty()) {
//                        CategoryListAdapter(
//                            reorderCategories.mapIndexed { index, location ->
//                                location.toListItem(
//                                    requireContext(),
//                                    object : RecyclerViewHelperInterface {
//                                        override fun onItemClicked(position: Int) {
//                                            onItemClicked(index, position)
//                                        }
//                                    },
//                                )
//                            },
//                        )
//                    } else {
//                        EmptyAdapter()
//                    }
//                },
//                preSelectedItems = getSelectedLocations().toList()
//            )
//        }
//    }

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
                    val filteredInterestsWithLocations: List<InterestWithLocationsWithReviews> = when (selectedRating) {
                        "3+" -> {
                            reorderCategories.mapNotNull { interestWithLocationsWithReviews ->
                                val filteredLocations = interestWithLocationsWithReviews.locations.filter { locationWithReviews ->
                                    val averageRating = locationWithReviews.getAverageRating()
                                    averageRating >= 3.0f
                                }

                                if (filteredLocations.isNotEmpty()) {
                                    interestWithLocationsWithReviews.copy(locations = filteredLocations.toMutableList())
                                } else {
                                    null
                                }
                            }
                        }
                        "4+" -> {
                            reorderCategories.mapNotNull { interestWithLocationsWithReviews ->
                                val filteredLocations = interestWithLocationsWithReviews.locations.filter { locationWithReviews ->
                                    val averageRating = locationWithReviews.getAverageRating()
                                    averageRating >= 4.0f
                                }

                                if (filteredLocations.isNotEmpty()) {
                                    interestWithLocationsWithReviews.copy(locations = filteredLocations.toMutableList())
                                } else {
                                    null
                                }
                            }
                        }
                        "5" -> {
                            reorderCategories.mapNotNull { interestWithLocationsWithReviews ->
                                val filteredLocations = interestWithLocationsWithReviews.locations.filter { locationWithReviews ->
                                    val averageRating = locationWithReviews.getAverageRating()
                                    averageRating == 5.0f
                                }

                                if (filteredLocations.isNotEmpty()) {
                                    interestWithLocationsWithReviews.copy(locations = filteredLocations.toMutableList())
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
                    recyclerView?.adapter = if (filteredInterestsWithLocations.isNotEmpty()) {
                        CategoryListAdapter(
                            filteredInterestsWithLocations.mapIndexed { index, location ->
                                location.toListItem(
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
                            reorderCategories.mapIndexed { index, location ->
                                location.toListItem(
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
     * Behandelt Klickereignisse auf Veranstaltungen und leitet zum entsprechenden LocationActivity weiter.
     */
    internal fun onItemClicked(
        categoryPosition: Int,
        locationPosition: Int,
    ) {
        if (interests.size > categoryPosition && interests[categoryPosition].locations.size > locationPosition) {
            requireContext().startActivity(
                Intent(requireContext(), LocationActivity::class.java).apply {
                    putExtra(
                        LocationActivity.LOCATION_INTENT_EXTRA,
                        interests[categoryPosition].locations[locationPosition].location.id,
                    )
                },
            )
        }
    }
}
