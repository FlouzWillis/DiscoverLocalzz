package com.ebusiness.discoverlocalzz.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ebusiness.discoverlocalzz.database.models.Review

/**
 * Data Access Object (DAO) Schnittstelle für den Zugriff auf Bewertungsdaten in der Datenbank.
 */
@Dao
interface ReviewDao {
    /**
     * Fügt einen oder mehrere Bewertungsdatensätze in die Datenbank ein.
     */
    @Insert
    suspend fun insertAll(vararg reviews: Review)

    @Query("SELECT * FROM review")
    suspend fun getAllReviews(): List<Review>

    @Query("SELECT * FROM review WHERE location_id = :id")
    suspend fun getReviewsForLocation(id: Long): List<Review>

    @Insert
    suspend fun saveReviewForLocation(review: Review)

    @Query("SELECT * FROM review WHERE user_id = :userId")
    suspend fun getReviewsFromUser(userId: Long): List<Review>?
}
