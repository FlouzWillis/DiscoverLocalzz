package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.ebusiness.discoverlocalzz.database.models.LocationInterest

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Location-Interessendaten in der Datenbank.
 */
@Dao
interface LocationInterestDao {
    /**
     * Fügt einen oder mehrere Location-Interessensdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg interests: LocationInterest)

    @Insert
    suspend fun insert(locationInterest: LocationInterest)
}
