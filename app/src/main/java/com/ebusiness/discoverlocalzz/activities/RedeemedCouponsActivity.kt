package com.ebusiness.discoverlocalzz.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.adapters.EmptyAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Aktivität für den Zahlungsverlauf.
 */
class RedeemedCouponsActivity : BaseActivity(), RecyclerViewHelperInterface {
    /**
     * Initialisiert die Zahlungsverlaufs-Aktivität.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redeemed_coupons)

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LoadingAdapter()
        CoroutineScope(Dispatchers.Main).launch {
            val coupons =
                AppDatabase.getInstance(this@RedeemedCouponsActivity)
                    .couponDao()
                    .getAll(Preferences.getUserId(this@RedeemedCouponsActivity))

            val currentDate = System.currentTimeMillis()

            val expiredCoupons = coupons.filter {
                it.coupon.expiryDate < currentDate
            }

            recyclerView.adapter =
                if (expiredCoupons.isNotEmpty()) {
                    SimpleListAdapter(
                        expiredCoupons.map { it.toRedeemedCouponsListItem(resources) },
                        this@RedeemedCouponsActivity,
                    )
                } else {
                    EmptyAdapter()
                }
        }
    }

    /**
     * Behandelt Klickereignisse auf Listenelemente.
     */
    override fun onItemClicked(position: Int) {
        // Do nothing.
    }
}
