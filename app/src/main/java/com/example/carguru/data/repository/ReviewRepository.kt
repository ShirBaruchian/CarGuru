package com.example.carguru.data.repository

import com.example.carguru.data.local.ReviewDao
import com.example.carguru.data.local.ReviewEntity
import com.example.carguru.data.local.UserDao
import com.example.carguru.data.remote.FirebaseReviewService
import com.example.carguru.data.repository.UserRepository
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

    suspend fun syncReviews(scope: CoroutineScope) = withContext(Dispatchers.IO) {
        val localReviews = reviewDao.getAllReviews().first()
        val remoteReviews = firebaseReviewService.getAllReviews()

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
            if (remoteReview == null || remoteReview.lastUpdated?.before(localReview.lastUpdated) != false) {
                firebaseReviewService.updateReview(localReview.toReview())
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
            firebaseReviewService.deleteReview(reviewToDelete.id)
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
}
