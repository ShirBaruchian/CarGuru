package com.example.carguru

import android.app.Application
import androidx.room.Room
import com.example.carguru.data.local.AppDatabase
import com.example.carguru.data.remote.FirebaseReviewService
import com.example.carguru.data.remote.FirebaseUserService
import com.example.carguru.data.repository.ReviewRepository
import com.example.carguru.data.repository.UserRepository

class MyApplication : Application() {
    lateinit var reviewRepository: ReviewRepository
        private set
    lateinit var userRepository: UserRepository
        private set

    override fun onCreate() {
        super.onCreate()
        try {


            val database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()


            reviewRepository = ReviewRepository(database, FirebaseReviewService())
            userRepository = UserRepository(database, FirebaseUserService())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
