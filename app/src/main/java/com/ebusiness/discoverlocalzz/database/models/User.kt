package com.ebusiness.discoverlocalzz.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Die Klasse User stellt einen Benutzer in der Room-Datenbank dar.
 *
 * @property accountId Die ID des zugehörigen Kontos.
 * @property name Der Vorname des Benutzers.
 * @property surname Der Nachname des Benutzers.
 * @property birthdate Das Geburtsdatum des Benutzers.
 */
@Entity(tableName = "user")
open class User(
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "surname") val surname: String,
    @ColumnInfo(name = "birthdate") val birthdate: Long,
) {
    /**
     * Die eindeutige ID des Benutzers.
     */
    @ColumnInfo(name = "user_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
