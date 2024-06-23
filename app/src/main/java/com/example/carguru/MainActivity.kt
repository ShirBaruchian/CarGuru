package com.example.carguru

import android.annotation.SuppressLint
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
import com.example.carguru.data.model.User
//import com.example.carguru.ui.screens.CarScreen
import com.example.carguru.ui.screens.ProfileScreen
import com.example.carguru.ui.screens.ReviewScreen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
//import services.CarMake
//import services.CarViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carguru.ui.screens.CarScreen
//import com.example.carguru.ui.screens.CarQueryAPI
//import com.example.carguru.ui.screens.CarScreen
import com.example.carguru.ui.screens.DropdownMenu
//import services.CarQueryViewModel
import com.example.carguru.services.CarRepository
//import services.CarViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            MaterialTheme {
//                CarMakeListScreen()
//            }
            CarGuruTheme {
                val viewModel: CarRepository = viewModel()
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("signup") { SignUpScreen(navController) }
                    composable("main") {
                        CarScreen(viewModel)
                    }
                    composable("profile") { ProfileScreen(navController,
                        User(
                        id = "sd",
                        username = "John Doe",
                        email = "john.doe@example.com",
                        password = "sdfcs",
                        birthdate = "20 jan 2001"
                        ),
                        onLogout = { /* Handle logout */ })
                    }
                    composable("review") { ReviewScreen(navController) }
                }
//                CarList()
//                ReviewScreen()
            }
        }
    }
}
//
//@Composable
//fun CarMakeListScreen(viewModel: CarViewModel = viewModel()) {
//    val carYears by viewModel.carYears.observeAsState(emptyList())
////    val carMakes by viewModel.carMakes.observeAsState(emptyList())
//
////    val carMakeNames = carMakes.map { it.make_display }
//
////    var selectedMake by remember { mutableStateOf("") }
//    var selectedYear by remember { mutableStateOf("") }
//
//    Column {
////        DropdownMenu(
////            label = "Make",
////            options = carMakeNames,
////            selectedOption = selectedMake,
////            onOptionSelected = { selectedMake = it }
////        )
//
//        DropdownMenu(
//            label = "Year",
//            options = carYears.map { it.toString() },
//            selectedOption = selectedYear,
//            onOptionSelected = { selectedYear = it }
//        )
//
//
//
//        // Display the selected make and year
////        Text(
////            text = "Selected Car Make: $selectedMake",
////            modifier = Modifier.padding(16.dp)
////        )
////        DropdownMenu(label = "Manufacturer", options = listOf("Toyota"), selectedOption = viewModel.selectedManufacturer) {
////            viewModel.selectedManufacturer = it
////        }
//        Text(
//            text = "Selected Car Year: $selectedYear",
//            modifier = Modifier.padding(16.dp)
//        )
//    }
//}


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


//@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
//@Composable
//fun CarMakeListScreen(viewModel: CarMakeViewModel = viewModel()) {
//    val carMakes by remember { mutableStateOf(viewModel.carMakes) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Car Makes") })
//        }
//    ) {
//        CarMakeList(carMakes = carMakes)
//    }
//}

//@Composable
//fun CarMakeList(carMakes: List<CarMake>) {
//    LazyColumn(
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(carMakes) { carMake ->
//            CarMakeItem(carMake)
//        }
//    }
//}
//
//@Composable
//fun CarMakeItem(carMake: CarMake) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = 4.dp
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(text = carMake.makeDisplay, style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}