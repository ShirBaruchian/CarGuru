package com.example.carguru.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carguru.data.model.Car
import services.CarViewModel


//@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun CarScreen(navController: NavHostController) {
//    val carViewModel: CarViewModel = viewModel()
//    val carYears by carViewModel.carYears.observeAsState(emptyList())
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Car Models") })
//        }
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            var make by remember { mutableStateOf("") }
//            OutlinedTextField(
//                value = make,
//                onValueChange = { make = it },
//                label = { Text("Car Make") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Button(onClick = { carViewModel.fetchCarYears() }) {
//                Text("Fetch Models")
//            }
//            Spacer(modifier = Modifier.height(16.dp))
////            CarModelList(carYears)
//        }
//    }
//
//    val viewModel = remember { CarViewModel() }
//    var selectedYear by remember { mutableStateOf("") }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        // Dropdowns
//        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
////            DropdownMenu(label = "Manufacturer", options = listOf("Toyota"), selectedOption = viewModel.selectedManufacturer) {
////                viewModel.selectedManufacturer = it
////            }
////            DropdownMenu(label = "Model", options = listOf("Corolla"), selectedOption = viewModel.selectedModel) {
////                viewModel.selectedModel = it
////            }
//            DropdownMenu(label = "Year", options = carYears, selectedOption = selectedYear) {
//                selectedYear = it
//            }
//        }
//
////        Spacer(modifier = Modifier.height(16.dp))
////
////        // Car details
////        Text(text = "${viewModel.selectedModel} - ${viewModel.selectedYear}")
////        Text(text = carInfo)
////
////        Spacer(modifier = Modifier.height(16.dp))
////
////        // Trims
////        Text(text = "Trim", style = MaterialTheme.typography.h6)
////        LazyColumn {
////            items(trims.size) { index ->
////                TrimItem(trim = trims[index])
////            }
////        }
//    }
//}
//
////@Composable
////fun DropdownMenu(label: String, items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
////    var expanded by remember { mutableStateOf(false) }
////    Box {
////        Button(onClick = { expanded = true }) {
////            Text(text = selectedItem)
////        }
////        DropdownMenu(
////            expanded = expanded,
////            onDismissRequest = { expanded = false }
////        ) {
////            items.forEach { item ->
////                DropdownMenuItem(onClick = {
////                    onItemSelected(item)
////                    expanded = false
////                }) {
////                    Text(text = item)
////                }
////            }
////        }
////    }
////}
//
////@Composable
////fun TrimItem(trim: Trim) {
////    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
////        Column(modifier = Modifier.padding(8.dp)) {
////            Text(text = trim.name, style = MaterialTheme.typography.h6)
////            Text(text = "Rating: ${trim.rating}")
////            Text(text = "Reviews: ${trim.reviews}")
////        }
////    }
////}
//
//@Composable
//fun CarModelList(carModels: List<Car>) {
//    LazyColumn {
//        items(carModels) { carModel ->
//            Card(modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 4.dp)) {
//                Column(modifier = Modifier.padding(16.dp)) {
////                    Text(text = "Model: ${carModel}")
////                    Text(text = "Make: ${carModel.model_make_id}")
//                    Text(text = "Year: ${carModel}")
//                }
//            }
//        }
//    }
//}

@Composable
fun CarScreen(viewModel: CarViewModel = viewModel()) {
    val years by viewModel.years

    val selectedItem by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Home", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
//            DropdownMenu("manufacturer", options = listOf("gy"), selectedOption = uh)
            Spacer(modifier = Modifier.width(8.dp))
//            DropdownMenu("model",options = years, selectedOption = )
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenu("year", options = years, selectedOption = selectedItem){}
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Toyota Corolla - 2022", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("details details details details details details details details")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Trim", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TrimCard("SUN", 4.2f, 10)
            Spacer(modifier = Modifier.width(8.dp))
            TrimCard("GLI", 4.1f, 42)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            TrimCard("HYB", 3.9f, 11)
            Spacer(modifier = Modifier.width(8.dp))
            TrimCard("EV", 5f, 16)
        }
    }
}

//@Composable
//fun DropdownMenu(label: String, items: List<Int> = listOf()) {
//    var expanded by remember { mutableStateOf(false) }
//    var selectedItem by remember { mutableStateOf(label) }
//
//    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
//        Text(
//            text = selectedItem,
//            modifier = Modifier
//                .clickable(onClick = { expanded = true })
//                .background(MaterialTheme.colors.surface)
//                .padding(16.dp)
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            items.forEach { item ->
//                DropdownMenuItem(onClick = {
//                    selectedItem = item.toString()
//                    expanded = false
//                }) {
//                    Text(text = item.toString())
//                }
//            }
//        }
//    }
//}

@Composable
fun TrimCard(name: String, rating: Float, reviews: Int) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, style = MaterialTheme.typography.bodySmall)
            Text(text = "$rating/5", style = MaterialTheme.typography.bodyMedium)
            Text(text = "$reviews Reviews", style = MaterialTheme.typography.bodyMedium)
            Text(text = "details details details details")
        }
    }
}
