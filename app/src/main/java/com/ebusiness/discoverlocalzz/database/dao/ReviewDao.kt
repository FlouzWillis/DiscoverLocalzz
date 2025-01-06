package com.ebusiness.discoverlocalzz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.ebusiness.discoverlocalzz.data.models.Review

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Bewertungsdaten in der Datenbank.
 */
@Dao
interface ReviewDao {
    /**
     * Fügt einen oder mehrere Bewertungsdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg reviews: Review)
}
