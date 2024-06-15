package com.example.carguru.ui.screens

import java.util.UUID
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import com.example.carguru.data.model.Car
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import com.example.carguru.ui.screens.CarItem
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField

@Composable
fun CarList() {
    var brand by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    val cars = remember { mutableStateListOf<Car>() }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it },
            label = { Text("Brand") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Model") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val newCarId = UUID.randomUUID().toString()
                cars.add(Car(newCarId,brand, model, imageUrl))
                brand = ""
                model = ""
                imageUrl = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Car")
        }
        LazyColumn {
            items(cars) { car ->
                CarItem(car)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
