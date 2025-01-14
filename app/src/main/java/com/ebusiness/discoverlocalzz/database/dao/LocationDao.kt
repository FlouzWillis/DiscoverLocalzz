package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ebusiness.discoverlocalzz.database.models.Location
import com.ebusiness.discoverlocalzz.database.models.LocationWithAddress
import com.ebusiness.discoverlocalzz.database.models.LocationWithAddressOrganizerReviews

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Veranstaltungsdaten in der Datenbank.
 */
@Dao
interface LocationDao {
    /**
     * Holt alle Veranstaltungen mit Addresse aus der Datenbank.
     */
    @Transaction
    @Query("SELECT * FROM location")
    suspend fun getAll(): List<LocationWithAddress>

    /**
     * Sucht nach einer Veranstaltung anhand ihrer ID und gibt diese zurück, falls vorhanden.
     */
    @Query("SELECT * FROM location WHERE location_id = :id LIMIT 1")
    suspend fun get(id: Long): Location?

    /**
     * Holt eine Veranstaltung zusammen mit zugehörigen Adressen, Organisatoren und Bewertungen anhand ihrer ID.
     */
    @Transaction
    @Query("SELECT * FROM location WHERE location_id = :id LIMIT 1")
    suspend fun getWithAddressOrganizerReviews(id: Long): LocationWithAddressOrganizerReviews?

    /**
     * Fügt einen oder mehrere Veranstaltungsdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg locations: Location)
}
