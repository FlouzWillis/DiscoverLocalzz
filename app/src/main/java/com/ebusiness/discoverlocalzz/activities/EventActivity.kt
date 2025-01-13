package com.ebusiness.discoverlocalzz.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.adapters.ErrorAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.customview.showReviewDialog
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.database.models.EventWithAddressOrganizerReviews
import com.ebusiness.discoverlocalzz.database.models.Review
import com.ebusiness.discoverlocalzz.helpers.Base64
import com.ebusiness.discoverlocalzz.helpers.External
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.helpers.StarView
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Aktivität für die Darstellung von Eventdetails und Interaktionsmöglichkeiten wie Teilen und Buchen.
 */
class EventActivity : BaseActivity(), RecyclerViewHelperInterface {
    private var event: EventWithAddressOrganizerReviews? = null
    /**
     * Initialisiert die Eventaktivität und lädt Eventdetails und interaktive Funktionen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        setupLayout()

        findViewById<FloatingActionButton>(R.id.share).setOnClickListener {
            startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                    },
                    resources.getString(R.string.share),
                ),
            )
        }

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LoadingAdapter()

        if (!intent.hasExtra(EVENT_INTENT_EXTRA)) {
            recyclerView.adapter = ErrorAdapter()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            event =
                AppDatabase.getInstance(this@EventActivity).eventDao()
                    .getWithAddressOrganizerReviews(intent.getLongExtra(EVENT_INTENT_EXTRA, -1))

            if (event != null) {
                showEvent(
                    event ?: error(EVENT_IS_NULL),
                    findViewById(R.id.frame),
                    listOf(
                        findViewById(R.id.first_star),
                        findViewById(R.id.second_star),
                        findViewById(R.id.third_star),
                        findViewById(R.id.fourth_star),
                        findViewById(R.id.fifth_star),
                    ),
                    recyclerView,
                )
            } else {
                recyclerView.adapter = ErrorAdapter()
            }
        }

        findViewById<FloatingActionButton>(R.id.add_review).setOnClickListener {
            event?.let { event ->
                showReviewDialog(
                    this@EventActivity,
                    onConfirm = { rating, reviewText ->
                        CoroutineScope(Dispatchers.Main).launch {
                            val reviewDao = AppDatabase.getInstance(this@EventActivity).reviewDao()
                            val review = Review(
                                event.event.id,
                                event.organizer.id,
                                reviewText,
                                rating,
                                getCurrentDate()
                            )
                            reviewDao.saveReviewForEvent(review)
                        }
                    },
                    onClear = {

                    })
            }
        }
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun setupLayout() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )

        var resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            val rotation = ContextCompat.getDisplayOrDefault(this).rotation
            val layoutParams = findViewById<View>(R.id.root).layoutParams as MarginLayoutParams

            when (rotation) {
                Surface.ROTATION_90 ->
                    layoutParams.setMargins(
                        0,
                        0,
                        resources.getDimensionPixelSize(resourceId),
                        0,
                    )
                Surface.ROTATION_270 ->
                    layoutParams.setMargins(
                        resources.getDimensionPixelSize(resourceId),
                        0,
                        0,
                        0,
                    )
                else ->
                    layoutParams.setMargins(
                        0,
                        0,
                        0,
                        resources.getDimensionPixelSize(resourceId),
                    )
            }
        }

        findViewById<MaterialToolbar>(R.id.top_app_bar).apply {
            resourceId =
                resources.getIdentifier(
                    "status_bar_height",
                    "dimen",
                    "android",
                )
            if (resourceId > 0) {
                (layoutParams as MarginLayoutParams).setMargins(
                    0,
                    resources.getDimensionPixelSize(resourceId),
                    0,
                    0,
                )
            }

            setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun showEvent(
        event: EventWithAddressOrganizerReviews,
        frame: View,
        stars: List<ImageView>,
        recyclerView: RecyclerView,
    ) {
        frame.findViewById<ImageView>(R.id.image).setImageDrawable(
            Base64.decodeImage(this@EventActivity, event.event.image),
        )
        StarView.fillStars(event.reviews.map { it.stars }.average().toFloat(), stars)
        frame.findViewById<TextView>(R.id.title).text = event.event.title
        recyclerView.adapter =
            SimpleListAdapter(
                listOf(
                    SimpleListItem(
                        resources.getString(R.string.description),
                        event.event.description,
                        R.drawable.ic_circle_local_activity,
                    ),
                    SimpleListItem(
                        event.address.toString(resources),
                        resources.getString(R.string.where),
                        R.drawable.ic_circle_location_on,
                    ),
                    SimpleListItem(
                        resources.getString(R.string.ratings_title),
                        event.reviews.map { it.stars }.average().toFloat().toString(),
                        R.drawable.ic_circle_star_filled,
                    ),
                ),
                this,
            )
    }

    /**
     * Reagiert auf Klickereignisse in der Eventliste.
     * Öffnet Google Maps, wenn auf das Standortelement geklickt wird.
     */
    override fun onItemClicked(position: Int) {
        when (position) {
            LOCATION_ITEM -> External.openMaps(this, event?.address ?: error(EVENT_IS_NULL))
            REVIEWS_ITEM -> openReviewsView()
        }
    }

    private fun openReviewsView() {
        this.startActivity(
            Intent(this, ReviewsActivity::class.java).apply {
                putExtra(
                    EventActivity.EVENT_INTENT_EXTRA,
                    event?.event?.id,
                )
            },
        )
    }

    companion object {
        /**
         * Konstante für den Schlüssel, der verwendet wird, um Event-Daten als Intent-Extra zwischen
         * Aktivitäten zu übertragen.
         */
        const val EVENT_INTENT_EXTRA: String = "event_intent_extra"
        private const val REVIEWS_ITEM = 2
        private const val LOCATION_ITEM = 1
        private const val EVENT_IS_NULL = "Event is null."
    }

    private fun getCurrentDate(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val currentDateMillis = calendar.timeInMillis
        return currentDateMillis
    }
}
