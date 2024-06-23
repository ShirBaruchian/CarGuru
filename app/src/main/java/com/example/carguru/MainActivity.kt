package com.example.carguru

import android.os.Bundle
import androidx.compose.material3.Text
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carguru.ui.screens.AddReviewScreen
import com.example.carguru.ui.screens.HomeScreen
import com.example.carguru.ui.theme.CarGuruTheme
import com.example.carguru.viewmodels.LoginViewModel
import com.example.carguru.ui.screens.LoginScreen
import com.example.carguru.ui.screens.ReviewDetailScreen
import com.example.carguru.ui.screens.SignUpScreen
import com.example.carguru.viewmodels.AddReviewViewModel
import com.example.carguru.viewmodels.ReviewsViewModel
import com.example.carguru.viewmodels.SignUpViewModel
import com.example.carguru.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val signUpViewModel: SignUpViewModel by viewModels()
    private val reviewsViewModel: ReviewsViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val addReviewViewModel: AddReviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            CarGuruTheme {

//                val navController = rememberNavController()
//                NavHost(navController = navController, startDestination = "login") {
//                    composable("login") { LoginScreen(navController) }
//                    composable("signup") { SignUpScreen(navController) }
//                }
//                CarList()
//                ReviewScreen()
                AppNavigation(loginViewModel, signUpViewModel,
                    reviewsViewModel, userViewModel, addReviewViewModel)

//                ProfileScreen(
//                    profile = User(
//                        id = "sd",
//                        username = "John Doe",
//                        email = "john.doe@example.com",
//                        password = "sdfcs",
//                        birthdate = "20 jan 2001"
//                    ),
//                    onLogout = { /* Handle logout */ }
//                )

            }
        }
    }
}

@Composable
fun AppNavigation(loginViewModel: LoginViewModel, signUpViewModel: SignUpViewModel,
                  reviewsViewModel: ReviewsViewModel, userViewModel: UserViewModel,
                  addReviewViewModel: AddReviewViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, loginViewModel = loginViewModel,
                userViewModel = userViewModel)
        }
        composable("signup") {
            SignUpScreen(navController = navController, signUpViewModel = signUpViewModel)
        }
        composable("home") {
            HomeScreen(navController = navController, reviewsViewModel = reviewsViewModel, userViewModel) // Pass actual user name if available
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
    }
}