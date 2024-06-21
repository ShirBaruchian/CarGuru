package com.example.carguru

import android.os.Bundle
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carguru.ui.theme.CarGuruTheme
import com.example.carguru.viewmodels.LoginViewModel
import com.example.carguru.views.LoggedInScreen
import com.example.carguru.views.LoginScreen
import com.example.carguru.ui.theme.CarGuruTheme
import com.example.carguru.ui.screens.SignUpScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.rememberNavController
import com.example.carguru.data.model.User
import com.example.carguru.ui.screens.CarList
import com.example.carguru.ui.screens.HomeScreen
import com.example.carguru.ui.screens.ProfileScreen
import com.example.carguru.ui.screens.ReviewScreen

class MainActivity : ComponentActivity() {
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
                val loginViewModel = LoginViewModel()
                LoginScreen(loginViewModel)

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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CarGuruTheme {
        Greeting("Android")
    }
}