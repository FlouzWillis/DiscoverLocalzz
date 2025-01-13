package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ebusiness.discoverlocalzz.database.models.Coupon
import com.ebusiness.discoverlocalzz.database.models.CouponWithEvent
import com.ebusiness.discoverlocalzz.database.models.CouponWithEventWithAddress

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Ticketdaten in der Datenbank.
 */
@Dao
interface CouponDao {

    /**
     * Holt alle Tickets eines Benutzers mit den zugehörigen Veranstaltungsdaten.
     */
    @Transaction
    @Query("SELECT * FROM coupon WHERE user_id = :userId AND is_deleted = 0 ORDER BY expiry_date DESC")
    suspend fun getAll(userId: Long): List<CouponWithEvent>

    /**
     * Holt ein spezifisches Ticket eines Benutzers mit zugehöriger Veranstaltung und Adresse.
     */
    @Transaction
    @Query("SELECT * FROM coupon WHERE coupon_id = :id AND user_id = :userId LIMIT 1")
    suspend fun getWithEventWithAddress(
        id: Long,
        userId: Long,
    ): CouponWithEventWithAddress?

    /**
     * Markiert ein Ticket anhand der ID zur Löschung.
     */
    @Query("UPDATE coupon SET is_deleted = 1 WHERE coupon_id = :id")
    suspend fun delete(id: Long)

    /**
     * Fügt einen oder mehrere Ticketdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg coupons: Coupon)

    /**
     * Fügt einen einzelnen Ticketdatensatz in die Datenbank ein und gibt die generierte Ticket-ID zurück.
     */
    @Insert
    suspend fun insert(coupon: Coupon): Long
}
