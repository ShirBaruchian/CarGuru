package com.example.carguru.data.repository

import com.example.carguru.data.local.ReviewDao
import com.example.carguru.data.model.Review
import com.example.carguru.data.local.ReviewEntity
import com.example.carguru.data.remote.FirebaseReviewService
import com.example.carguru.utils.toReview
import com.example.carguru.utils.toReviewEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

class ReviewRepository(private val reviewDao: ReviewDao, private val firebaseService: FirebaseReviewService) {
    fun startListeningForUpdates(scope: CoroutineScope) {
        firebaseService.addReviewListener { reviews ->
            updateLocalDatabase(scope, reviews.map { it.toReviewEntity() })
        }
    }

    private fun updateLocalDatabase(scope: CoroutineScope, reviews: List<ReviewEntity>) {
        scope.launch {
            withContext(Dispatchers.IO) {
                // Clear existing data and insert the new data
                reviewDao.clearAllReviews()
                reviewDao.insertReviews(reviews)
            }
        }
    }
    suspend fun getReview(reviewId: String): ReviewEntity? {
        return reviewDao.getReviewById(reviewId) ?: firebaseService.getReview(reviewId)?.toReviewEntity()?.also { reviewEntity ->
            reviewDao.insertReview(reviewEntity)
        }
    }

    suspend fun saveReview(review: ReviewEntity) {
        reviewDao.insertReview(review)
        firebaseService.saveReview(review.toReview())
    }

    suspend fun syncReviews() = withContext(Dispatchers.IO) {
        val localReviews = reviewDao.getAllReviews()
        val remoteReviews = firebaseService.getAllReviews()

        val localReviewMap = localReviews.associateBy { it.id }
        val remoteReviewMap = remoteReviews.associateBy { it.id }

        // Update or insert reviews from Firebase into Room
        for (remoteReview in remoteReviews) {
            val localReview = localReviewMap[remoteReview.id]
            if (localReview == null || localReview.lastUpdated.before(remoteReview.lastUpdated)) {
                reviewDao.insertReview(remoteReview.toReviewEntity())
            }
        }

        // Update or insert reviews from Room into Firebase
        for (localReview in localReviews) {
            val remoteReview = remoteReviewMap[localReview.id]
            if (remoteReview == null || remoteReview.lastUpdated!!.before(localReview.lastUpdated)) {
                firebaseService.updateReview(localReview.toReview())
            }
        }

        // Identify and delete reviews from Room that are no longer in Firebase
        val remoteReviewIds = remoteReviews.map { it.id }.toSet()
        val reviewsToDeleteFromRoom = localReviews.filter { it.id !in remoteReviewIds }
        for (reviewToDelete in reviewsToDeleteFromRoom) {
            reviewDao.deleteReviewById(reviewToDelete.id)
        }

        // Identify and delete reviews from Firebase that are no longer in Room
        val localReviewIds = localReviews.map { it.id }.toSet()
        val reviewsToDeleteFromFirebase = remoteReviews.filter { it.id !in localReviewIds }
        for (reviewToDelete in reviewsToDeleteFromFirebase) {
            firebaseService.deleteReview(reviewToDelete.id)
        }
    }

    suspend fun getAllReviews(): List<ReviewEntity> {
        return reviewDao.getAllReviews()
    }
}
