package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ebusiness.discoverlocalzz.database.models.Account

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Account-Daten in der Datenbank.
 */
@Dao
interface AccountDao {
    /**
     * Sucht nach einem Account anhand der ID und gibt diesen zurück, falls vorhanden.
     */
    @Query("SELECT * FROM account WHERE account_id = :id AND is_deleted = 0 LIMIT 1")
    suspend fun get(id: Long): Account?

    /**
     * Sucht nach einem Account anhand der E-Mail oder Telefonnummer und gibt diesen zurück, falls vorhanden.
     */
    @Query("SELECT * FROM account WHERE (e_mail = :eMailOrPhone OR phone = :eMailOrPhone) AND is_deleted = 0 LIMIT 1")
    suspend fun get(eMailOrPhone: String): Account?

    /**
     * Markiert einen Account anhand der ID zur Löschung.
     */
    @Query("UPDATE account SET is_deleted = 1 WHERE account_id = :id")
    suspend fun delete(id: Long)

    /**
     * Fügt einen oder mehrere Accounts in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg accounts: Account)

    /**
     * Fügt einen Account in die Datenbank ein und gibt die ID des neuen Accounts zurück.
     */
    @Insert
    suspend fun insert(account: Account): Long
}
