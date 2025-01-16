package com.ebusiness.discoverlocalzz.activities

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.adapters.ErrorAdapter
import com.ebusiness.discoverlocalzz.adapters.LoadingAdapter
import com.ebusiness.discoverlocalzz.adapters.SimpleListAdapter
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import com.ebusiness.discoverlocalzz.database.models.UserWithAccount
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.ebusiness.discoverlocalzz.interfaces.RecyclerViewHelperInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

/**
 * Aktivität für die Nutzerdaten.
 */
class DataActivity : BaseActivity(), RecyclerViewHelperInterface {
    /**
     * Initialisiert die Nutzerdaten-Aktivität und konfiguriert Location-Handler für die Löschoption.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LoadingAdapter()

        CoroutineScope(Dispatchers.Main).launch {
            val account =
                AppDatabase.getInstance(this@DataActivity)
                    .userDao()
                    .get(Preferences.getUserId(this@DataActivity))

            if (account != null) {
                onAccountLoaded(recyclerView, account)
            } else {
                recyclerView.adapter = ErrorAdapter()
            }
        }

        findViewById<Button>(R.id.delete).setOnClickListener {
            onDeleteClicked()
        }
    }

    private fun onAccountLoaded(
        recyclerView: RecyclerView,
        account: UserWithAccount,
    ) {
        recyclerView.adapter =
            SimpleListAdapter(
                listOf(
                    SimpleListItem(
                        account.user.name,
                        resources.getString(R.string.name),
                        R.drawable.ic_circle_person,
                    ),
                    SimpleListItem(
                        account.user.surname,
                        resources.getString(R.string.surname),
                        R.drawable.ic_circle_person,
                    ),
                    SimpleListItem(
                        DateFormat.getDateFormat(this).format(account.user.birthdate),
                        resources.getString(R.string.birthdate),
                        R.drawable.ic_circle_calendar_today,
                    ),
                    SimpleListItem(
                        account.account.eMail,
                        resources.getString(R.string.mail),
                        R.drawable.ic_circle_mail,
                    ),
                    SimpleListItem(
                        account.account.phone,
                        resources.getString(R.string.phone),
                        R.drawable.ic_circle_call,
                    ),
                    SimpleListItem(
                        resources.getString(R.string.password_placeholder),
                        resources.getString(R.string.password),
                        R.drawable.ic_circle_key,
                    ),
                    SimpleListItem(
                        "",
                        resources.getString(R.string.edit_data_hint),
                    ),
                ),
                this,
            )
    }

    private fun onDeleteClicked() {
        val view =
            layoutInflater.inflate(
                R.layout.dialog_delete_account,
                findViewById(R.id.root),
                false,
            )
        MaterialAlertDialogBuilder(this).setTitle(R.string.delete_account)
            .setMessage(R.string.delete_account_summary)
            .setView(view)
            .setPositiveButton(R.string.delete) { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    val input = view.findViewById<TextInputLayout>(R.id.password).editText?.text.toString()
                    val accountDao = AppDatabase.getInstance(this@DataActivity).accountDao()
                    val passwordHash =
                        accountDao
                            .get(Preferences.getUserId(this@DataActivity))
                            ?.passwordHash
                            ?: error("Invalid account id.")
                    if (BCrypt.checkpw(input, passwordHash)) {
                        accountDao.delete(Preferences.getUserId(this@DataActivity))
                        Preferences.setLoggedIn(this@DataActivity, Preferences.NO_ACCOUNT)
                        startActivity(Intent(this@DataActivity, LoginActivity::class.java))
                        Toast.makeText(
                            this@DataActivity,
                            R.string.account_deleted,
                            Toast.LENGTH_LONG,
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@DataActivity,
                            R.string.wrong_password,
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .show()
    }

    /**
     * Reagiert auf Klickereignisse in der Optionsliste.
     */
    override fun onItemClicked(position: Int) {

    }
}
