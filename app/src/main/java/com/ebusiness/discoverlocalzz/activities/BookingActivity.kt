package com.ebusiness.discoverlocalzz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.adapters.ErrorAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.database.models.Event
import com.ebusiness.discoverlocalzz.database.models.Coupon
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Aktivität für Buchungsprozesse, zeigt verschiedene Buchungsoptionen an.
 */
class BookingActivity : BaseActivity(), RecyclerViewHelperInterface {
    private var paymentMethodChanged = false
    private lateinit var recyclerView: RecyclerView

    /**
     * Initialisiert die Buchungsaktivität und konfiguriert die Anzeige von Eventdaten.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        recyclerView = findViewById(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LoadingAdapter()

        if (!intent.hasExtra(EventActivity.EVENT_INTENT_EXTRA)) {
            recyclerView.adapter = ErrorAdapter()
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            val event =
                AppDatabase.getInstance(this@BookingActivity).eventDao()
                    .get(intent.getLongExtra(EventActivity.EVENT_INTENT_EXTRA, -1))

            if (event != null) {
                showList(event, recyclerView)
                findViewById<ExtendedFloatingActionButton>(R.id.floating_action_button).setOnClickListener {
                    if (!Preferences.hasPaymentDetails(this@BookingActivity)) {
                        MaterialAlertDialogBuilder(this@BookingActivity)
                            .setTitle(R.string.booking_payment_missing)
                            .setMessage(R.string.booking_payment_missing_message)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                        @Suppress("LabeledExpression")
                        return@setOnClickListener
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        val coupon =
                            AppDatabase.getInstance(this@BookingActivity).couponDao().insert(
                                Coupon(
                                    event.id,
                                    Preferences.getUserId(this@BookingActivity),
                                    System.currentTimeMillis(),
                                ),
                            )
                        startActivity(
                            Intent(this@BookingActivity, CouponActivity::class.java).putExtra(
                                CouponActivity.COUPON_INTENT_EXTRA,
                                coupon,
                            ),
                        )
                    }
                }
            } else {
                recyclerView.adapter = ErrorAdapter()
            }
        }
    }

    /**
     * Aktualisiert Informationen über die Zahlungsmethode.
     */
    override fun onStart() {
        super.onStart()
        if (paymentMethodChanged) {
            val items = (recyclerView.adapter as SimpleListAdapter).items.toMutableList()
            items[PAYMENT_PROVIDER_ITEM] =
                SimpleListItem(
                    getPaymentDetailsLabel(this),
                    resources.getString(R.string.booking_payment),
                    R.drawable.ic_square_credit_card,
                )
            recyclerView.adapter = SimpleListAdapter(items, this)
            paymentMethodChanged = false
        }
    }

    private fun getPaymentDetailsLabel(context: Context): String {
        val paymentDetails = Preferences.getPaymentDetails(context)
        if (paymentDetails.cardNumber.isBlank()) {
            return resources.getString(R.string.booking_payment_missing)
        }
        return resources.getString(
            R.string.booking_payment_card_number,
            paymentDetails.cardNumber.takeLast(NUMBER_OF_CREDIT_CARD_DIGITS_SHOWN),
        )
    }

    private fun showList(
        event: Event,
        recyclerView: RecyclerView,
    ) {
        recyclerView.adapter =
            SimpleListAdapter(
                listOf(
                    SimpleListItem(
                        event.title,
                        resources.getString(R.string.booking_title),
                        R.drawable.ic_circle_tag,
                    ),
                    SimpleListItem(
                        getPaymentDetailsLabel(this),
                        resources.getString(R.string.booking_payment),
                        R.drawable.ic_square_credit_card,
                    ),
                    SimpleListItem("", resources.getString(R.string.booking_info)),
                ),
                this,
            )
    }

    /**
     * Reagiert auf Klickereignisse in der Buchungsliste, insbesondere bei Auswahl von Zahlungsanbietern.
     */
    override fun onItemClicked(position: Int) {
        if (position == PAYMENT_PROVIDER_ITEM) {
            paymentMethodChanged = true
            startActivity(Intent(this, PaymentDetailsActivity::class.java))
        }
    }

    companion object {
        private const val PAYMENT_PROVIDER_ITEM = 2
        private const val NUMBER_OF_CREDIT_CARD_DIGITS_SHOWN = 4
    }
}
