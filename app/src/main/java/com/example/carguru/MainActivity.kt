package com.example.carguru

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.composable
import com.example.carguru.ui.screens.HomeScreen
import com.example.carguru.ui.theme.CarGuruTheme
import com.example.carguru.ui.screens.LoginScreen
import com.example.carguru.ui.screens.SignUpScreen
import com.example.carguru.viewmodels.LoginViewModel
import com.example.carguru.viewmodels.SignUpViewModel
import androidx.navigation.compose.rememberNavController
import com.example.carguru.ui.screens.ProfileScreen

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val signUpViewModel: SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarGuruTheme {
                AppNavigation(loginViewModel, signUpViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(loginViewModel: LoginViewModel, signUpViewModel: SignUpViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
        composable("signup") {
            SignUpScreen(navController = navController, signUpViewModel = signUpViewModel)
        }
        composable("home/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            HomeScreen(navController = navController,userName = userName) // Pass actual user name if available
        }
    }
}