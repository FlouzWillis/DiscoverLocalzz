package com.ebusiness.discoverlocalzz.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Aktivität für generelle Optionen.
 */
class AccountActivity : BaseActivity(), RecyclerViewHelperInterface {
    /**
     * Initialisiert die Account-Aktivität und konfiguriert Location-Handler für die Abmeldeoption.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter =
            SimpleListAdapter(
                listOf(
                    SimpleListItem(resources.getString(R.string.my_data), "", R.drawable.ic_circle_person),
                    SimpleListItem(resources.getString(R.string.my_interests), "", R.drawable.ic_circle_interests),
                    SimpleListItem(resources.getString(R.string.my_reviews), "", R.drawable.ic_circle_star_filled),
                    SimpleListItem(resources.getString(R.string.payment_details), "", R.drawable.ic_circle_credit_card),
                    SimpleListItem(
                        resources.getString(R.string.coupons_history),
                        "",
                        R.drawable.ic_circle_local_activity,
                    ),
                    SimpleListItem(resources.getString(R.string.imprint), "", R.drawable.ic_circle_link),
                    SimpleListItem(resources.getString(R.string.privacy_policy), "", R.drawable.ic_circle_link),
                ),
                this,
            )

        findViewById<Button>(R.id.logout).setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle(R.string.logout)
                .setMessage(R.string.logout_summary)
                .setPositiveButton(R.string.logout) { _, _ ->
                    Preferences.setLoggedIn(this, Preferences.NO_ACCOUNT)
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .show()
        }
    }

    /**
     * Reagiert auf Klickereignisse in der Optionsliste.
     */
    override fun onItemClicked(position: Int) {
        startActivity(
            Intent(
                this,
                when (position) {
                    MY_DATA_ITEM -> DataActivity::class.java
                    MY_INTERESTS_ITEM -> InterestsActivity::class.java
                    PAYMENT_DETAILS_ITEM -> PaymentDetailsActivity::class.java
                    REDEEMED_COUPONS_ITEM -> RedeemedCouponsActivity::class.java
                    MY_REVIEWS_ITEM -> MyReviewsActivity::class.java
                    IMPRINT_ITEM -> ImprintActivity::class.java
                    PRIVACY_POLICY_ITEM -> PrivacyPolicyActivity::class.java
                    else -> return
                },
            ),
        )
    }

    companion object {
        private const val MY_DATA_ITEM = 0
        private const val MY_INTERESTS_ITEM = 1
        private const val MY_REVIEWS_ITEM = 2
        private const val PAYMENT_DETAILS_ITEM = 3
        private const val REDEEMED_COUPONS_ITEM = 4
        private const val IMPRINT_ITEM = 5
        private const val PRIVACY_POLICY_ITEM = 6
    }
}
