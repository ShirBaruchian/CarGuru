package com.example.carguru

import android.os.Bundle
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.composable
import androidx.compose.material3.MaterialTheme
import com.example.carguru.ui.theme.CarGuruTheme
import com.example.carguru.ui.screens.LoginScreen
import com.example.carguru.ui.screens.SignUpScreen
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarGuruTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("signup") { SignUpScreen(navController) }
                }
                // A surface container using the 'background' color from the theme
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