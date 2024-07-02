package com.example.carguru.data.repository

import com.example.carguru.data.local.ReviewDao
import com.example.carguru.data.local.ReviewEntity
import com.example.carguru.data.remote.FirebaseReviewService
import com.example.carguru.models.ReviewWithUser
import com.example.carguru.utils.toReview
import com.example.carguru.utils.toReviewEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class ReviewRepository(
    private val reviewDao: ReviewDao,
    private val firebaseReviewService: FirebaseReviewService,
    private val userRepository: UserRepository
) {

    fun startListeningForUpdates(scope: CoroutineScope) {
        firebaseReviewService.addReviewListener { reviews ->
            scope.launch {
                withContext(Dispatchers.IO) {
                    updateLocalDatabase(reviews.map { it.toReviewEntity() })
                }
            }
        }
    }

    private suspend fun updateLocalDatabase(reviews: List<ReviewEntity>) {
        reviewDao.clearAllReviews()
        reviewDao.insertReviews(reviews)
    }

    fun getAllReviews(): Flow<List<ReviewEntity>> {
        return reviewDao.getAllReviews()
    }

    fun getAllReviewsWithUser(): Flow<List<ReviewWithUser>> {
        return reviewDao.getAllReviews().map { reviewEntities ->
            reviewEntities.map { reviewEntity ->
                val user = userRepository.getUser(reviewEntity.userId)?.firstOrNull()  // Assuming you have a method to get the user
                ReviewWithUser(reviewEntity, user?.username ?: "Unknown")
            }
        }
    }

    suspend fun saveReview(review: ReviewEntity) {
        reviewDao.insertReview(review)
        firebaseReviewService.updateReview(review.toReview())
    }

    suspend fun syncReviews() = withContext(Dispatchers.IO) {
        val lastUpdateDate = reviewDao.getLatestUpdateDate() ?: Date(0) // Default to epoch if no date

        val updatedReviews = firebaseReviewService.getReviewsUpdatedAfter(lastUpdateDate)

        if (updatedReviews.isNotEmpty()) {
            reviewDao.insertReviews(updatedReviews.map { it.toReviewEntity() })
        }

        // Step 3: Handle deletions
        val allFirebaseReviews = firebaseReviewService.getAllReviews()
        val localReviews = reviewDao.getAllReviews().first() // Collect the Flow to get the current value
        val firebaseReviewIds = allFirebaseReviews.map { it.id }.toSet()
        val reviewsToDelete = localReviews.filter { it.id !in firebaseReviewIds }
        if (reviewsToDelete.isNotEmpty()) {
            reviewDao.deleteReviewsByIds(reviewsToDelete.map { it.id })
        }
    }

    fun getReview(reviewId: String): Flow<ReviewWithUser?> {
        return reviewDao.getReviewById(reviewId).map { reviewEntity ->
            reviewEntity?.let {
                val user = userRepository.getUser(it.userId).firstOrNull()  // Assuming you have a method to get the user
                ReviewWithUser(it, user?.username ?: "Unknown")
            }
        }
    }

    fun getReviewsByUserId(userId: String): Flow<List<ReviewWithUser>> {
        return reviewDao.getReviewsByUserId(userId).map { reviewEntities ->
            reviewEntities.map { reviewEntity ->
                val user = userRepository.getUser(reviewEntity.userId)?.firstOrNull()  // Assuming you have a method to get the user
                ReviewWithUser(reviewEntity, user?.username ?: "Unknown")
            }
        }
    }
}
