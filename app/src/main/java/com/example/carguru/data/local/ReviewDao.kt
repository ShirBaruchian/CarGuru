package com.example.carguru.data.local

import java.util.Date
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy

@Dao
interface ReviewDao {
    @Query("SELECT MAX(lastUpdated) FROM reviews")
    suspend fun getLatestUpdateDate(): Date?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("DELETE FROM reviews WHERE id IN (:reviewIds)")
    suspend fun deleteReviewsByIds(reviewIds: List<String>)

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    fun getReviewById(reviewId: String): Flow<ReviewEntity?>

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: String)

    @Query("DELETE FROM reviews")
    suspend fun clearAllReviews()

    @Transaction
    @Query("SELECT * FROM reviews WHERE userId = :userId")
    fun getReviewsByUserId(userId: String): Flow<List<ReviewEntity>>
}
