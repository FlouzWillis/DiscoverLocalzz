package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ebusiness.discoverlocalzz.database.models.AccountInterest

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Account-Interessen-Daten in der Datenbank.
 */
@Dao
interface AccountInterestDao {
    /**
     * Fügt ein oder mehrere Interessen in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg interests: AccountInterest)

    /**
     * Alle Interessen eines Benutzers aus der Datenbank.
     */
    @Query("SELECT * FROM account_interest WHERE account_id = :userId")
    suspend fun getUserInterests(userId: Long): List<AccountInterest>

    /**
     * Löscht alle Interessen eines Benutzers aus der Datenbank.
     */
    @Query("DELETE FROM account_interest WHERE account_id = :userId")
    suspend fun deleteUserInterests(userId: Long)
}
