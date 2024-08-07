package com.example.carguru

import android.os.Bundle
import androidx.room.Room
import androidx.navigation.NavType
import android.annotation.SuppressLint
import androidx.navigation.navArgument
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.compose.composable
import com.example.carguru.ui.screens.HomeScreen
import com.example.carguru.ui.theme.CarGuruTheme
import com.example.carguru.ui.screens.LoginScreen
import com.example.carguru.data.local.AppDatabase
import com.example.carguru.ui.screens.SignUpScreen
import com.example.carguru.ui.screens.ProfileScreen
import com.example.carguru.viewmodels.CarRepository
import com.example.carguru.viewmodels.UserViewModel
import com.example.carguru.viewmodels.LoginViewModel
import com.example.carguru.viewmodels.SignUpViewModel
import com.example.carguru.ui.screens.AddReviewScreen
import com.example.carguru.viewmodels.ReviewsViewModel
import androidx.navigation.compose.rememberNavController
import com.example.carguru.viewmodels.AddReviewViewModel
import com.example.carguru.ui.screens.ReviewDetailScreen
import com.example.carguru.data.repository.UserRepository
import com.example.carguru.data.remote.FirebaseUserService
import com.example.carguru.data.repository.ReviewRepository
import com.example.carguru.data.remote.FirebaseReviewService

class MainActivity : ComponentActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userViewModel: UserViewModel
    private lateinit var reviewsViewModel: ReviewsViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var addReviewViewModel: AddReviewViewModel
    private lateinit var carViewModel: CarRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()
        // Initialize Firebase services
        val firebaseUserService = FirebaseUserService()
        val firebaseReviewService = FirebaseReviewService()

        // Initialize repositories
        val userRepository = UserRepository(database.userDao(), firebaseUserService)
        val reviewRepository = ReviewRepository(database.reviewDao(), firebaseReviewService, userRepository)

        userViewModel = UserViewModel(userRepository)
        reviewsViewModel = ReviewsViewModel(reviewRepository)
        loginViewModel = LoginViewModel(userRepository)
        signUpViewModel = SignUpViewModel(userRepository)
        addReviewViewModel = AddReviewViewModel(reviewRepository)
        carViewModel = CarRepository()
        userViewModel.fetchCurrentUser()
        setContent {
            CarGuruTheme {

                val currentUser = firebaseAuth.currentUser
                val startDestination = if (currentUser != null) "home" else "login"
                AppNavigation(startDestination, loginViewModel, signUpViewModel,
                    reviewsViewModel, userViewModel,
                    addReviewViewModel, carViewModel)
            }
        }
    }
}



@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(startDestination: String, loginViewModel: LoginViewModel, signUpViewModel: SignUpViewModel,
                  reviewsViewModel: ReviewsViewModel, userViewModel: UserViewModel,
                  addReviewViewModel: AddReviewViewModel,carViewModel: CarRepository) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
        composable("signup") {
            SignUpScreen(navController = navController, signUpViewModel = signUpViewModel)
        }
        composable("home") {
            HomeScreen(navController = navController, reviewsViewModel = reviewsViewModel, userViewModel, carViewModel) // Pass actual user name if available
        }
        composable("addReview") {
            AddReviewScreen(navController = navController, addReviewViewModel = addReviewViewModel,
                carViewModel)
        }
        composable(
            "reviewDetail/{reviewId}",
            arguments = listOf(navArgument("reviewId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reviewId = backStackEntry.arguments?.getString("reviewId") ?: return@composable
            ReviewDetailScreen(navController = navController, reviewId = reviewId, reviewsViewModel = reviewsViewModel)
        }
        composable("profile") {
            val user = userViewModel.user.value
            user?.let {
                ProfileScreen(navController = navController, profile = it,userViewModel = userViewModel,
                    reviewsViewModel = reviewsViewModel)
            }
        }
    }
}