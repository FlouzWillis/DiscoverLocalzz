package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ebusiness.discoverlocalzz.database.models.Event
import com.ebusiness.discoverlocalzz.database.models.EventWithAddress
import com.ebusiness.discoverlocalzz.database.models.EventWithAddressOrganizerReviews

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Veranstaltungsdaten in der Datenbank.
 */
@Dao
interface EventDao {
    /**
     * Holt alle Veranstaltungen mit Addresse aus der Datenbank.
     */
    @Transaction
    @Query("SELECT * FROM event")
    suspend fun getAll(): List<EventWithAddress>

    /**
     * Sucht nach einer Veranstaltung anhand ihrer ID und gibt diese zurück, falls vorhanden.
     */
    @Query("SELECT * FROM event WHERE event_id = :id LIMIT 1")
    suspend fun get(id: Long): Event?

    /**
     * Holt eine Veranstaltung zusammen mit zugehörigen Adressen, Organisatoren und Bewertungen anhand ihrer ID.
     */
    @Transaction
    @Query("SELECT * FROM event WHERE event_id = :id LIMIT 1")
    suspend fun getWithAddressOrganizerReviews(id: Long): EventWithAddressOrganizerReviews?

    /**
     * Fügt einen oder mehrere Veranstaltungsdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg events: Event)
}
