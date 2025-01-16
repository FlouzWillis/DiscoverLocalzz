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
import com.ebusiness.discoverlocalzz.activities.CouponActivity
import com.ebusiness.discoverlocalzz.adapters.EmptyAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.models.CouponWithLocation
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragment für die Anzeige und Filterung von Benutzercoupons.
 */
class CouponsFragment : Fragment(), RecyclerViewHelperInterface {
    private var coupons: List<CouponWithLocation> = listOf()
    private lateinit var titleFilter: Chip
    private lateinit var couponFilter: Chip
    private lateinit var recyclerView: RecyclerView
    private var previousFilter: Byte = -1
    private var reversed = false

    /**
     * Erstellt die Ansicht für das Couponsfragment, initialisiert Filter und lädt die Couponliste.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val root = inflater.inflate(R.layout.fragment_coupons, container, false)

        MainActivity.setupSearchView(root)
        root.findViewById<SearchView>(R.id.searchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    onSearchClicked()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

        }

        titleFilter = root.findViewById(R.id.title_filter)
        couponFilter = root.findViewById(R.id.coupon_filter)

        recyclerView = root.findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = LoadingAdapter()
        CoroutineScope(Dispatchers.Main).launch {
            coupons =
                AppDatabase.getInstance(requireContext()).couponDao()
                    .getAll(Preferences.getUserId(requireContext())).filter { it.coupon.expiryDate > System.currentTimeMillis() }
            titleFilter.setOnClickListener {
                selectFilter(TITLE_FILTER)
            }
            couponFilter.setOnClickListener {
                selectFilter(COUPON_FILTER)
            }
            selectFilter(TITLE_FILTER)
        }

        return root
    }

    /**
     * Behandelt Klickereignisse auf Couponelemente und leitet zum Detailbereich des ausgewählten Coupons weiter.
     */
    override fun onItemClicked(position: Int) {
        if (coupons.size > position) {
            requireContext().startActivity(
                Intent(requireContext(), CouponActivity::class.java).apply {
                    putExtra(CouponActivity.COUPON_INTENT_EXTRA, coupons[position].coupon.id)
                },
            )
        }
    }

    private fun update(newCoupons: List<CouponWithLocation>) {
        CoroutineScope(Dispatchers.Main).launch {
            coupons = newCoupons
            recyclerView.adapter =
                if (coupons.isNotEmpty()) {
                    SimpleListAdapter(
                        coupons.map { it.toListItem(resources, AppDatabase.getInstance(requireContext()).addressDao()) },
                        this@CouponsFragment,
                    )
                } else {
                    EmptyAdapter()
                }
        }
    }

    private fun selectFilter(filter: Byte) {
        titleFilter.chipIcon = null
        couponFilter.chipIcon = null
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
                        coupons.sortedBy { it.location.title }
                    } else {
                        coupons.sortedByDescending { it.location.title }
                    },
                )
            }

            COUPON_FILTER -> {
                couponFilter.setChipIconResource(icon)
                update(
                    if (reversed) {
                        coupons.sortedBy { it.coupon.expiryDate }
                    } else {
                        coupons.sortedByDescending { it.coupon.expiryDate }
                    },
                )
            }
        }
        previousFilter = filter
    }

    fun onSearchClicked() {

    }

    companion object {
        private const val TITLE_FILTER: Byte = 1
        private const val COUPON_FILTER: Byte = 2
    }
}
