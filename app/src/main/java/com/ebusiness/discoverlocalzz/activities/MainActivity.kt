package com.ebusiness.discoverlocalzz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.helpers.Preferences
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Hauptaktivität, die die Navigation innerhalb der App steuert.
 */
class MainActivity : BaseActivity() {
    /**
     * Initialisiert die Hauptaktivität und konfiguriert die Navigationselemente.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController =
            (
                    supportFragmentManager.findFragmentById(
                        R.id.nav_host_fragment_activity_main,
                    ) as NavHostFragment
                    ).navController

        findViewById<BottomNavigationView>(R.id.nav_view).setupWithNavController(navController)

        when (intent.action) {
            "com.ebusiness.discoverlocalzz.SHOW_DISCOVER" -> navigate(navController, R.id.navigation_discover)
            "com.ebusiness.discoverlocalzz.SHOW_MAP" -> navigate(navController, R.id.navigation_map)
            "com.ebusiness.discoverlocalzz.SHOW_COUPONS" -> navigate(navController, R.id.navigation_coupons)
        }
    }

    private fun navigate(
        navController: NavController,
        id: Int,
    ) {
        navController.graph.setStartDestination(id)
        navController.navigate(id)
    }

    companion object {
        private fun onAccountClicked(context: Context) {
            context.startActivity(
                Intent(
                    context,
                    if (Preferences.isLoggedIn(context)) {
                        AccountActivity::class.java
                    } else {
                        LoginActivity::class.java
                    },
                ),
            )
        }

        fun setupSearchView(view: View) {
            val profileButton = view.findViewById<ImageView>(R.id.profileButton)
            val profileResource = if (Preferences.isLoggedIn(view.context)) {
                R.drawable.ic_account_circle
            } else {
                R.drawable.ic_account_circle_off
            }
            profileButton.setImageResource(profileResource)
            profileButton.setOnClickListener {
                onAccountClicked(view.context)
            }
        }
    }
}
