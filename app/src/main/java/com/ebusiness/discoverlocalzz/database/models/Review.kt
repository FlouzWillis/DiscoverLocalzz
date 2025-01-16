package com.ebusiness.discoverlocalzz.database.models

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.database.AppDatabase
import com.ebusiness.discoverlocalzz.database.SimpleListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Die Klasse Review repräsentiert eine Bewertung in der Room-Datenbank.
 *
 * @property locationId Die ID des zugehörigen Locations.
 * @property userId Die ID des Benutzers, der die Bewertung abgegeben hat.
 * @property message Die Nachricht oder der Kommentar zur Bewertung.
 * @property stars Die Anzahl der Sterne, die für die Bewertung vergeben wurden.
 * @property date Das Datum, an dem die Bewertung abgegeben wurde.
 */
@Entity(tableName = "review")
class Review(
    @ColumnInfo(name = "location_id") val locationId: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "stars") val stars: Float,
    @ColumnInfo(name = "date") val date: Long,
) {
    /**
     * Die eindeutige ID der Bewertung in der Datenbank.
     */
    @ColumnInfo(name = "review_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    suspend fun getLocationName(context: Context): String {
        return AppDatabase.getInstance(context).locationDao().get(locationId)?.title ?: ""
    }

    fun getReviewDateAsString(): String =
        SimpleDateFormat(
            "d. MMM yyyy",
            Locale.getDefault(),
        ).format(date)

    suspend fun toListItem(context: Context): SimpleListItem {
        return SimpleListItem(
            context.getString(R.string.start_format, stars.toString(), getReviewDateAsString()),
            message,
            R.drawable.ic_circle_star_filled,
            getLocationName(context),
        )
    }
}
