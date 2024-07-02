package com.example.carguru.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.carguru.data.local.ReviewEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

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

    @Query("SELECT * FROM reviews")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: String)

    @Query("DELETE FROM reviews")
    suspend fun clearAllReviews()
}
