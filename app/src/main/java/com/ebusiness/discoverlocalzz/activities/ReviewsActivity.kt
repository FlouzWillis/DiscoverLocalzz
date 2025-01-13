package com.ebusiness.discoverlocalzz.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.activities.EventActivity.Companion.EVENT_INTENT_EXTRA
import com.ebusiness.discoverlocalzz.adapters.ErrorAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.database.models.EventWithAddressOrganizerReviews
import com.ebusiness.discoverlocalzz.database.models.Review
import com.ebusiness.discoverlocalzz.databinding.ActivityReviewsBinding
import com.ebusiness.discoverlocalzz.helpers.Base64
import com.ebusiness.discoverlocalzz.helpers.StarView
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewsActivity : BaseActivity(), RecyclerViewHelperInterface {
    private var event: EventWithAddressOrganizerReviews? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        setupLayout()

        val recyclerView = findViewById<RecyclerView>(R.id.review_list)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LoadingAdapter()

        if (!intent.hasExtra(EVENT_INTENT_EXTRA)) {
            recyclerView.adapter = ErrorAdapter()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            event = AppDatabase.getInstance(this@ReviewsActivity).eventDao()
                .getWithAddressOrganizerReviews(intent.getLongExtra(EVENT_INTENT_EXTRA, -1))

            if (event != null) {
                showReviews(
                    event!!,
                    findViewById(R.id.frame),
                    recyclerView)
            } else {
                recyclerView.adapter = ErrorAdapter()
            }
        }
    }

    @SuppressLint("DiscouragedApi", "InternalInsetResource")
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

    @SuppressLint("NotifyDataSetChanged")
    private fun showReviews(
        event: EventWithAddressOrganizerReviews,
        frame: View,
        recyclerView: RecyclerView
    ) {
        val stars = listOf<ImageView>(
            findViewById(R.id.first_star),
            findViewById(R.id.second_star),
            findViewById(R.id.third_star),
            findViewById(R.id.fourth_star),
            findViewById(R.id.fifth_star),
        )

        frame.findViewById<ImageView>(R.id.image).setImageDrawable(
            Base64.decodeImage(this, event.event.image),
        )
        StarView.fillStars(event.reviews.map { it.stars }.average().toFloat(), stars)
        frame.findViewById<TextView>(R.id.title).text = event.event.title

        val reviewList = mutableListOf<SimpleListItem>()

        event.reviews.forEach { review ->
            reviewList.add(SimpleListItem(
                review.stars.toString(),
                review.message,
                R.drawable.ic_circle_star_filled,
                getStringDate(review.date) ?: "",
            ))
        }

        recyclerView.adapter = SimpleListAdapter(
            reviewList,
            this
        )
        recyclerView.adapter?.notifyDataSetChanged()

    }

    override fun onItemClicked(position: Int) {

    }

    private fun getStringDate(date: Long): String? {
        val dateObj = Date(date)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(dateObj)
    }
}