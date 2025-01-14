package com.ebusiness.discoverlocalzz.customview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.ebusiness.discoverlocalzz.R

@SuppressLint("MissingInflatedId")
fun showAddLocationDialog(context: Context) {

    val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_location, null)
    val cancelButton = view.findViewById<Button>(R.id.cancel_button)
    val addButton = view.findViewById<Button>(R.id.add_button)

    val alertDialog = AlertDialog.Builder(context)
        .setView(view)
        .create()

    cancelButton.setOnClickListener {
        alertDialog.dismiss()
    }

    addButton.setOnClickListener {

        alertDialog.dismiss()
    }

    alertDialog.show()
}