package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ebusiness.discoverlocalzz.data.models.User
import com.ebusiness.discoverlocalzz.data.models.UserWithAccount

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf User-Daten in der Datenbank.
 */
@Dao
interface UserDao {
    /**
     * Sucht nach einem User anhand der ID und gibt diesen zurück, falls vorhanden.
     */
    @Transaction
    @Query("SELECT * FROM user WHERE account_id = :id LIMIT 1")
    suspend fun get(id: Long): UserWithAccount?

    /**
     * Fügt einen oder mehrere User in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg user: User)
}
