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
import com.ebusiness.discoverlocalzz.data.AppDatabase
import com.ebusiness.discoverlocalzz.data.SimpleListItem
import com.ebusiness.discoverlocalzz.data.models.TicketWithEventWithAddress
import com.ebusiness.discoverlocalzz.helpers.External
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Aktivität zur Anzeige von Ticketinformationen und Interaktionsmöglichkeiten wie Stornierung.
 */
class TicketActivity : BaseActivity(), RecyclerViewHelperInterface {
    private var ticket: TicketWithEventWithAddress? = null
    private lateinit var recyclerView: RecyclerView

    /**
     * Initialisiert die Ticketaktivität und lädt Ticketdetails in eine Liste.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)

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

        if (!intent.hasExtra(TICKET_INTENT_EXTRA)) {
            recyclerView.adapter = ErrorAdapter()
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            ticket =
                AppDatabase.getInstance(this@TicketActivity).ticketDao()
                    .getWithEventWithAddress(
                        intent.getLongExtra(TICKET_INTENT_EXTRA, -1),
                        Preferences.getUserId(this@TicketActivity),
                    )
            if (ticket != null) {
                recyclerView.adapter =
                    SimpleListAdapter(
                        listOf(
                            SimpleListItem("", resources.getString(R.string.ticket_info)),
                            SimpleListItem(
                                ticket?.event?.event?.title ?: error(TICKET_IS_NULL),
                                resources.getString(R.string.what),
                                R.drawable.ic_circle_local_activity,
                            ),
                            SimpleListItem(
                                ticket?.event?.event?.getStartAsString(resources) ?: error(TICKET_IS_NULL),
                                resources.getString(R.string.`when`),
                                R.drawable.ic_circle_calendar_today,
                            ),
                            SimpleListItem(
                                ticket?.event?.address?.toString(resources) ?: error(TICKET_IS_NULL),
                                resources.getString(R.string.where),
                                R.drawable.ic_circle_location_on,
                            ),
                            SimpleListItem(resources.getString(R.string.ticket_cancel)),
                        ),
                        this@TicketActivity,
                    )
            } else {
                recyclerView.adapter = ErrorAdapter()
            }
        }
    }

    private fun goBack() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                action = "com.ebusiness.discoverlocalzz.SHOW_TICKETS"
            },
        )
        finish()
    }

    /**
     * Reagiert auf Klickereignisse in der Ticketliste, insbesondere bei Auswahl der Stornierungsoption.
     */
    override fun onItemClicked(position: Int) {
        when (position) {
            CANCELLATION_ITEM ->
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.cancellation)
                    .setMessage(R.string.cancellation_message)
                    .setView(layoutInflater.inflate(R.layout.dialog_cancel, recyclerView, false))
                    .setPositiveButton(R.string.cancellation) { _, _ ->
                        CoroutineScope(Dispatchers.Main).launch {
                            // Der Grund der Stornierung wird noch nicht gespeichert.
                            AppDatabase.getInstance(this@TicketActivity)
                                .ticketDao()
                                .delete(ticket?.ticket?.id ?: error(TICKET_IS_NULL))
                            goBack()
                        }
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .show()
            LOCATION_ITEM -> External.openMaps(this, ticket?.event?.address ?: error(TICKET_IS_NULL))
            DATE_ITEM -> External.openCalendar(this, ticket?.event?.event ?: error(TICKET_IS_NULL))
        }
    }

    companion object {
        /**
         * Konstante für den Schlüssel, der verwendet wird, um Ticket-Daten als Intent-Extra
         * zwischen Aktivitäten zu übertragen.
         */
        const val TICKET_INTENT_EXTRA: String = "ticket_intent_extra"

        private const val CANCELLATION_ITEM = 4
        private const val LOCATION_ITEM = 3
        private const val DATE_ITEM = 2
        private const val TICKET_IS_NULL = "Ticket is null."
    }
}
