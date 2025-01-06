package com.ebusiness.discoverlocalzz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.ebusiness.discoverlocalzz.data.models.ZipCode

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Postleitzahldaten in der Datenbank.
 */
@Dao
interface ZipCodeDao {
    /**
     * Fügt einen oder mehrere Postleitzahldatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg zipCodes: ZipCode)
}
