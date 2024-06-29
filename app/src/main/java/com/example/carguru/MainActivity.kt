package com.example.carguru

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carguru.ui.screens.AddReviewScreen
import com.example.carguru.ui.screens.HomeScreen
import com.example.carguru.ui.theme.CarGuruTheme
import com.example.carguru.ui.screens.LoginScreen
import com.example.carguru.ui.screens.ReviewDetailScreen
import com.example.carguru.ui.screens.SignUpScreen
import com.example.carguru.viewmodels.AddReviewViewModel
import com.example.carguru.ui.screens.ProfileScreen
import com.example.carguru.viewmodels.CarRepository
import com.example.carguru.viewmodels.UserViewModel
import com.example.carguru.viewmodels.LoginViewModel
import com.example.carguru.viewmodels.ReviewsViewModel
import com.example.carguru.viewmodels.SignUpViewModel
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val loginViewModel: LoginViewModel by viewModels()
    private val signUpViewModel: SignUpViewModel by viewModels()
    private val reviewsViewModel: ReviewsViewModel by viewModels()
    private val addReviewViewModel: AddReviewViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val carViewModel: CarRepository by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.fetchUserDetails()
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



@Composable
fun AppNavigation(startDestination: String, loginViewModel: LoginViewModel, signUpViewModel: SignUpViewModel,
                  reviewsViewModel: ReviewsViewModel, userViewModel: UserViewModel,
                  addReviewViewModel: AddReviewViewModel,carViewModel: CarRepository) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController, loginViewModel = loginViewModel,
                userViewModel = userViewModel)
        }
        composable("signup") {
            SignUpScreen(navController = navController, signUpViewModel = signUpViewModel)
        }
        composable("home") {
            HomeScreen(navController = navController, reviewsViewModel = reviewsViewModel, userViewModel, carViewModel) // Pass actual user name if available
        }
        composable("addReview") {
            AddReviewScreen(navController = navController, addReviewViewModel = addReviewViewModel)
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
                ProfileScreen(navController = navController, profile = it,userViewModel = userViewModel)
            }
        }
    }
}