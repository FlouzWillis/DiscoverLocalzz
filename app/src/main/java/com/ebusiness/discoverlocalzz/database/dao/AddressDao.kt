package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.ebusiness.discoverlocalzz.database.models.Address

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Adressdaten in der Datenbank.
 */
@Dao
interface AddressDao {
    /**
     * Fügt einen oder mehrere Adressdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg addresses: Address)
}
