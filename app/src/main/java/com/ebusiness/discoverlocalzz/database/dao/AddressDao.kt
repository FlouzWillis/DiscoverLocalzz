package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("SELECT * FROM address WHERE address_id = :addressId")
    suspend fun getAddressById(addressId: Long): Address?

    @Insert
    suspend fun insert(address: Address): Long
}
