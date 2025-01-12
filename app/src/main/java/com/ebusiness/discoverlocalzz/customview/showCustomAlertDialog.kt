package com.ebusiness.discoverlocalzz.customview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.ebusiness.discoverlocalzz.R

@SuppressLint("MissingInflatedId")
fun showCustomAlertDialog(
    context: Context,
    title: String,
    description: String,
    optionsList: List<String>,
    onConfirm: (selectedItems: List<String>) -> Unit,
    onClear: () -> Unit,
    preSelectedItems: List<String> = emptyList(),
    useRadioButton: Boolean = false
) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_filter_list, null)

    val selectedItems = preSelectedItems.toMutableList()

    val titleTextView: TextView = dialogView.findViewById(R.id.filter_title)
    val descriptionTextView: TextView = dialogView.findViewById(R.id.filter_description)
    val checkBoxContainer: LinearLayout = dialogView.findViewById(R.id.checkbox_container)
    val cancelButton: Button = dialogView.findViewById(R.id.cancel_button)
    val confirmButton: Button = dialogView.findViewById(R.id.confirm_button)
    val radioButtons = mutableListOf<RadioButton>()
    val checkBoxes = mutableListOf<CheckBox>()

    titleTextView.text = title
    descriptionTextView.text = description

    if (useRadioButton) {
        val radioGroup = RadioGroup(context).apply {
            orientation = RadioGroup.VERTICAL
        }

        optionsList.forEach { text ->
            val radioButton = RadioButton(context).apply {
                this.text = text
                this.id = View.generateViewId()
                this.isChecked = selectedItems.contains(text)
            }
            radioGroup.addView(radioButton)
            radioButtons.add(radioButton)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(checkedId)

            selectedItems.clear()
            if (selectedRadioButton != null) {
                selectedItems.add(selectedRadioButton.text.toString())
            }

            radioButtons.forEach { radioButton ->
                radioButton.isChecked = radioButton.id == checkedId
            }
        }

        checkBoxContainer.addView(radioGroup)
    }
    else {
        optionsList.forEach { text ->
            val checkBox = CheckBox(context).apply {
                this.text = text
            }
            checkBox.isChecked = selectedItems.contains(text)
            checkBoxContainer.addView(checkBox)
            checkBoxes.add(checkBox)
        }
    }

    val alertDialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .create()

    cancelButton.setOnClickListener {
        onClear()
        alertDialog.dismiss()
    }

    confirmButton.setOnClickListener {
        val selectedItems = if (useRadioButton) {
            radioButtons.filter { it.isChecked }.map { it.text.toString() }
        } else {
            checkBoxes.filter { it.isChecked }.map { it.text.toString() }
        }
        onConfirm(selectedItems)
        alertDialog.dismiss()
    }

    alertDialog.show()
}
