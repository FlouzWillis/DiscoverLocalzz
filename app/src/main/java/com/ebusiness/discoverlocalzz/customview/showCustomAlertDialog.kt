package com.ebusiness.discoverlocalzz.customview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.ebusiness.discoverlocalzz.R

@SuppressLint("MissingInflatedId")
fun showCustomAlertDialog(
    context: Context,
    title: String,
    description: String,
    checkBoxTexts: List<String>,
    onConfirm: (selectedItems: List<String>) -> Unit,
    onClear: () -> Unit,
    preSelectedItems: List<String> = emptyList()
) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_filter_list, null)

    val selectedItems = preSelectedItems.toMutableList()

    val titleTextView: TextView = dialogView.findViewById(R.id.filter_title)
    val descriptionTextView: TextView = dialogView.findViewById(R.id.filter_description)
    val checkBoxContainer: LinearLayout = dialogView.findViewById(R.id.checkbox_container)
    val cancelButton: Button = dialogView.findViewById(R.id.cancel_button)
    val confirmButton: Button = dialogView.findViewById(R.id.confirm_button)

    titleTextView.text = title
    descriptionTextView.text = description

    val checkBoxes = mutableListOf<CheckBox>()
    checkBoxTexts.forEach { text ->
        val checkBox = CheckBox(context).apply {
            this.text = text
        }
        checkBox.isChecked = selectedItems.contains(text)
        checkBoxContainer.addView(checkBox)
        checkBoxes.add(checkBox)
    }

    val alertDialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .create()

    cancelButton.setOnClickListener {
        onClear()
        alertDialog.dismiss()
    }

    confirmButton.setOnClickListener {
        val selectedItems = checkBoxes.filter { it.isChecked }.map { it.text.toString() }
        onConfirm(selectedItems)
        alertDialog.dismiss()
    }

    alertDialog.show()
}
