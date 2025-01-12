package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.ebusiness.discoverlocalzz.database.models.Organizer

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Veranstalterdaten in der Datenbank.
 */
@Dao
interface OrganizerDao {
    /**
     * Fügt einen oder mehrere Veranstalterdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg organizers: Organizer)
}
