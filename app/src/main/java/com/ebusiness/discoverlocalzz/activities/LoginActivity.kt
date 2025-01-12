package com.ebusiness.discoverlocalzz.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

/**
 * Aktivität für Benutzeranmeldung mit verschiedenen Authentifizierungsoptionen.
 */
class LoginActivity : BaseActivity() {
    /**
     * Initialisiert die Login-Aktivität und konfiguriert Event-Handler für Anmeldeoptionen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        findViewById<Button>(R.id.guest).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<Button>(R.id.register).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<Button>(R.id.reset_password).setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.forgot_password)
                .setMessage(R.string.forgot_password_message)
                .setView(
                    layoutInflater.inflate(
                        R.layout.dialog_forgot_password,
                        findViewById(R.id.root),
                        false,
                    ),
                )
                .setPositiveButton(R.string.request) { _, _ ->
//                    OutOfScopeDialog.show(this)
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .show()
        }

        findViewById<FloatingActionButton>(R.id.continue_button).setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val emailView = findViewById<TextInputLayout>(R.id.e_mail_phone_number)
            val passwordView = findViewById<TextInputLayout>(R.id.password)
            CoroutineScope(Dispatchers.Main).launch {
                val userAccount =
                    AppDatabase.getInstance(this@LoginActivity).accountDao()
                        .get(emailView.editText?.text.toString())
                if (userAccount == null ||
                    !BCrypt.checkpw(
                        passwordView.editText?.text.toString(), userAccount.passwordHash,
                    )
                ) {
                    emailView.error =
                        resources.getString(R.string.login_error)
                } else {
                    Preferences.setLoggedIn(this@LoginActivity, userAccount.id)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
                progressBar.visibility = View.GONE
            }
        }
    }
}
