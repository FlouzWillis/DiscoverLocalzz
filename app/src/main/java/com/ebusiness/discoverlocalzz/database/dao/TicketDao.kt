package com.ebusiness.discoverlocalzz.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ebusiness.discoverlocalzz.data.models.Ticket
import com.ebusiness.discoverlocalzz.data.models.TicketWithEvent
import com.ebusiness.discoverlocalzz.data.models.TicketWithEventWithAddress

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Ticketdaten in der Datenbank.
 */
@Dao
interface TicketDao {

    @Query("SELECT * FROM ticket")
    suspend fun getAllTickets(): List<TicketWithEvent>

    /**
     * Holt alle Tickets eines Benutzers mit den zugehörigen Veranstaltungsdaten.
     */
    @Transaction
    @Query("SELECT * FROM ticket WHERE user_id = :userId AND is_deleted = 0 ORDER BY purchased_at DESC")
    suspend fun getAll(userId: Long): List<TicketWithEvent>

    /**
     * Holt ein spezifisches Ticket eines Benutzers mit zugehöriger Veranstaltung und Adresse.
     */
    @Transaction
    @Query("SELECT * FROM ticket WHERE ticket_id = :id AND user_id = :userId LIMIT 1")
    suspend fun getWithEventWithAddress(
        id: Long,
        userId: Long,
    ): TicketWithEventWithAddress?

    /**
     * Markiert ein Ticket anhand der ID zur Löschung.
     */
    @Query("UPDATE ticket SET is_deleted = 1 WHERE ticket_id = :id")
    suspend fun delete(id: Long)

    /**
     * Fügt einen oder mehrere Ticketdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg tickets: Ticket)

    /**
     * Fügt einen einzelnen Ticketdatensatz in die Datenbank ein und gibt die generierte Ticket-ID zurück.
     */
    @Insert
    suspend fun insert(ticket: Ticket): Long
}
