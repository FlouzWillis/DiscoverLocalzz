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
import com.ebusiness.discoverlocalzz.database.models.Event

/**
 * Objekt zur Interaktion mit externen Anwendungen wie dem Kalender und Karten.
 * Stellt Funktionen bereit, um Ereignisse im Kalender des Benutzers zu öffnen oder
 * eine Adresse in Google Maps anzuzeigen. Dies dient dazu, die Benutzererfahrung
 * durch nahtlose Integration mit anderen häufig genutzten Apps zu verbessern.
 */
object External {
    /**
     * Öffnet den Kalender des Benutzers und fügt ein neues Ereignis hinzu.
     *
     * @param context Der Kontext, in dem die Aktion ausgeführt wird.
     * @param event Das Event-Objekt, das Informationen über das Ereignis enthält.
     * @throws PackageManager.NameNotFoundException wenn der Kalender nicht gefunden wird.
     */
    fun openCalendar(
        context: Context,
        event: Event,
    ) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, event.title)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.start)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.end),
            )
        } catch (exception: PackageManager.NameNotFoundException) {
            Log.w(this::class.simpleName, exception)
            Toast.makeText(
                context,
                R.string.calendar_error,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

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
