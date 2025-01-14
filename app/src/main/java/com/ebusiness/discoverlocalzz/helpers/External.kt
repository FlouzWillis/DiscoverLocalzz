package com.ebusiness.discoverlocalzz.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.models.Address
import com.ebusiness.discoverlocalzz.database.models.Location

/**
 * Objekt zur Interaktion mit externen Anwendungen wie dem Kalender und Karten.
 * Stellt Funktionen bereit, um Ereignisse im Kalender des Benutzers zu öffnen oder
 * eine Adresse in Google Maps anzuzeigen. Dies dient dazu, die Benutzererfahrung
 * durch nahtlose Integration mit anderen häufig genutzten Apps zu verbessern.
 */
object External {

    /**
     * Öffnet Google Maps und sucht nach einer angegebenen Adresse.
     *
     * @param context Der Kontext, in dem die Aktion ausgeführt wird.
     * @param address Das AddressWithZipCode-Objekt, das Informationen über die Adresse enthält.
     */
    fun openMaps(
        context: Context,
        address: Address,
    ) {
        val mapIntent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=${Uri.encode(address.toString(context.resources))}"),
            )
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        }
    }
}
