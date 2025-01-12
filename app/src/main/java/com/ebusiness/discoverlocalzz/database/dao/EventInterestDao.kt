package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.ebusiness.discoverlocalzz.database.models.EventInterest

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Event-Interessendaten in der Datenbank.
 */
@Dao
interface EventInterestDao {
    /**
     * Fügt einen oder mehrere Event-Interessensdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg interests: EventInterest)
}
