package com.example.carguru

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.compose.composable
import com.example.carguru.ui.screens.HomeScreen
import com.example.carguru.ui.theme.CarGuruTheme
import com.example.carguru.ui.screens.LoginScreen
import com.example.carguru.ui.screens.SignUpScreen
import com.example.carguru.ui.screens.ProfileScreen
import com.example.carguru.viewmodels.CarRepository
import com.example.carguru.viewmodels.UserViewModel
import com.example.carguru.viewmodels.LoginViewModel
import com.example.carguru.viewmodels.SignUpViewModel
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val loginViewModel: LoginViewModel by viewModels()
    private val signUpViewModel: SignUpViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val carViewModel: CarRepository by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.fetchUserDetails(
            onSuccess = {
            },
            onFailure = {
            }
        )
        setContent {
            CarGuruTheme {
                val currentUser = firebaseAuth.currentUser
                val startDestination = if (currentUser != null) "home" else "login"
                AppNavigation(startDestination,loginViewModel, signUpViewModel,userViewModel,carViewModel)
            }
        }
    }
}



@Composable
fun AppNavigation(startDestination: String,loginViewModel: LoginViewModel, signUpViewModel: SignUpViewModel,userViewModel: UserViewModel, carViewModel: CarRepository) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
        composable("signup") {
            SignUpScreen(navController = navController, signUpViewModel = signUpViewModel)
        }
        composable("home") {
            val user = FirebaseAuth.getInstance().currentUser
            val userName = user?.displayName ?: user?.email ?: "User"
            HomeScreen(navController = navController,userName = userName, viewModel = carViewModel)
        }
        composable("profile") {
            val user = userViewModel.user.value
            user?.let {
                ProfileScreen(navController = navController, profile = it,userViewModel = userViewModel, onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                })
            }
        }
    }
}