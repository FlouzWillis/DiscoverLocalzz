package com.ebusiness.discoverlocalzz.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ebusiness.discoverlocalzz.R
import com.google.android.material.elevation.SurfaceColors

/**
 * Abstrakte Basisklasse, für das allgemeine Initialisierungen für Aktivitäten, wie das Einstellen von
 * Themen und Farben.
 */
abstract class BaseActivity : AppCompatActivity() {
    /**
     * Initialisiert die Aktivität und passt das Thema sowie die Farben der Status- und Navigationsleiste an.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.uiMode.and(
                Configuration.UI_MODE_NIGHT_MASK,
            ) != Configuration.UI_MODE_NIGHT_YES
        ) {
            setTheme(R.style.LightStatusBarOverlay)
        }
        val color = SurfaceColors.SURFACE_2.getColor(this)
        window.statusBarColor = color
        window.navigationBarColor = color
    }
}