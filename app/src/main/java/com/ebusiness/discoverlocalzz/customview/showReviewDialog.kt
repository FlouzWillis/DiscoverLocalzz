package com.ebusiness.discoverlocalzz.customview

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import com.ebusiness.discoverlocalzz.R

fun showReviewDialog(
    context: Context,
    onConfirm: (rating: Float, reviewText: String) -> Unit,
    onClear: () -> Unit,
    ) {

    val view = LayoutInflater.from(context).inflate(R.layout.dialog_review, null)

    val cancelButton = view.findViewById<Button>(R.id.cancel_button)
    val confirmButton = view.findViewById<Button>(R.id.confirm_button)
    val reviewEditText = view.findViewById<EditText>(R.id.reviewEditText)
    val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)


    val alertDialog = AlertDialog.Builder(context)
        .setView(view)
        .create()

    cancelButton.setOnClickListener {
        onClear()
        alertDialog.dismiss()
    }

    confirmButton.setOnClickListener {
        val rating = ratingBar.rating
        val reviewText = reviewEditText.text.toString().trim()
        onConfirm(rating, reviewText)
        alertDialog.dismiss()
    }

    alertDialog.show()
}