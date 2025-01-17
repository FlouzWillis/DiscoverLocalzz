package com.ebusiness.discoverlocalzz.activities

import android.annotation.SuppressLint
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

class MyReviewsActivity : BaseActivity(), RecyclerViewHelperInterface {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reviews)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LoadingAdapter()
        CoroutineScope(Dispatchers.Main).launch {
            val reviews = AppDatabase.getInstance(this@MyReviewsActivity)
                .reviewDao().getReviewsFromUser(Preferences.getUserId(this@MyReviewsActivity))

            recyclerView.adapter = if (reviews != null) {
                SimpleListAdapter(
                    reviews.map { it.toListItem(this@MyReviewsActivity) },
                    this@MyReviewsActivity
                )
            } else {
                EmptyAdapter()
            }
        }
    }

    override fun onItemClicked(position: Int) {
    }
}