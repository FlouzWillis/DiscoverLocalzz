package com.ebusiness.discoverlocalzz.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.adapters.ErrorAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.database.models.CouponWithLocationWithAddress
import com.ebusiness.discoverlocalzz.helpers.External
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Aktivität zur Anzeige von Couponinformationen und Interaktionsmöglichkeiten.
 */
class CouponActivity : BaseActivity(), RecyclerViewHelperInterface {
    private var coupon: CouponWithLocationWithAddress? = null
    private lateinit var recyclerView: RecyclerView

    /**
     * Initialisiert die Couponsaktivität und lädt Coupondetails in eine Liste.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    goBack()
                }
            },
        )

        recyclerView = findViewById(R.id.list)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LoadingAdapter()

        if (!intent.hasExtra(COUPON_INTENT_EXTRA)) {
            recyclerView.adapter = ErrorAdapter()
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            coupon =
                AppDatabase.getInstance(this@CouponActivity).couponDao()
                    .getWithLocationWithAddress(
                        intent.getLongExtra(COUPON_INTENT_EXTRA, -1),
                        Preferences.getUserId(this@CouponActivity),
                    )
            if (coupon != null) {
                recyclerView.adapter =
                    SimpleListAdapter(
                        listOf(
                            SimpleListItem("", resources.getString(R.string.coupon_info)),
                            SimpleListItem(
                                coupon?.location?.location?.title ?: error(COUPON_IS_NULL),
                                resources.getString(R.string.title_location),
                                R.drawable.ic_circle_local_activity,
                            ),
                            SimpleListItem(
                                coupon?.coupon?.getExpiredDateAsString() ?: error(COUPON_IS_NULL),
                                resources.getString(R.string.available_until),
                                R.drawable.ic_circle_calendar_today,
                            ),
                            SimpleListItem(
                                coupon?.location?.address?.toString(resources) ?: error(COUPON_IS_NULL),
                                resources.getString(R.string.title_address),
                                R.drawable.ic_circle_location_on,
                            ),
                        ),
                        this@CouponActivity,
                    )
            } else {
                recyclerView.adapter = ErrorAdapter()
            }
        }
    }

    private fun goBack() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                action = "com.ebusiness.discoverlocalzz.SHOW_COUPONS"
            },
        )
        finish()
    }

    /**
     * Reagiert auf Klickereignisse in der Couponliste, insbesondere bei Auswahl der Stornierungsoption. TODO
     */
    override fun onItemClicked(position: Int) {
        when (position) {
            LOCATION_ITEM -> External.openMaps(this, coupon?.location?.address ?: error(COUPON_IS_NULL))
        }
    }

    companion object {
        /**
         * Konstante für den Schlüssel, der verwendet wird, um Coupon-Daten als Intent-Extra
         * zwischen Aktivitäten zu übertragen.
         */
        const val COUPON_INTENT_EXTRA: String = "coupon_intent_extra"

        private const val LOCATION_ITEM = 3
        private const val COUPON_IS_NULL = "Coupon is null."
    }
}
