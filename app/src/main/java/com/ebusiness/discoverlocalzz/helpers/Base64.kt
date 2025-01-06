package com.ebusiness.discoverlocalzz.helpers

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.Gravity
import androidx.core.graphics.drawable.toDrawable
import java.io.InputStream

/**
 * Hilfsobjekt zum Dekodieren von Base64-codierten Bildern und Lesen von Dateien aus dem Assets-Verzeichnis.
 */

object Base64 {
    /**
     * Dekodiert eine Base64-codierte Zeichenkette in ein Drawable.
     *
     * @param context Der Anwendungskontext.
     * @param base64 Die Base64-codierte Zeichenkette des Bildes.
     * @return Ein Drawable des dekodierten Bildes.
     */
    fun decodeImage(
        context: Context,
        base64: String,
    ): Drawable {
        val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        val bitmap =
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                ?.toDrawable(context.resources)
                ?: error("Invalid Base64.")
        bitmap.gravity = Gravity.CENTER
        return bitmap
    }

    /**
     * Liest den Inhalt einer Datei aus dem Assets-Verzeichnis und gibt ihn als Base64-codierte Zeichenkette zurück.
     *
     * @param context Der Anwendungskontext.
     * @param path Der Pfad zur Datei im Assets-Verzeichnis.
     * @return Eine Base64-codierte Zeichenkette des Dateiinhalts.
     */
    fun getFromAssets(
        context: Context,
        path: String,
    ): String {
        val inputStream: InputStream = context.assets.open(path)
        val fileBytes = ByteArray(inputStream.available())
        inputStream.read(fileBytes)
        inputStream.close()
        return Base64.encodeToString(fileBytes, Base64.DEFAULT)
    }
}
