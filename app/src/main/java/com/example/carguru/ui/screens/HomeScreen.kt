package com.example.carguru.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun HomeScreen(userName: String) {
    var selectedManufacturer by remember { mutableStateOf("Manufacturer") }
    var selectedModel by remember { mutableStateOf("Model") }
    var selectedYear by remember { mutableStateOf("Year") }

    val manufacturers = listOf("Toyota", "Honda", "Ford")
    val models = listOf("Corolla", "Civic", "Mustang")
    val years = listOf("2022", "2021", "2020")

    val trims = listOf(
        Trim("SUN", 4.2, 10, "details details details details details"),
        Trim("GLI", 4.1, 42, "details details details details details"),
        Trim("HYB", 3.9, 11, "details details details details details"),
        Trim("EV", 5.0, 16, "details details details details details")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Home", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Hello", color = Color.Gray, fontSize = 14.sp)
                Text(text = userName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenu(selectedManufacturer, manufacturers) { selectedManufacturer = it }
            DropdownMenu(selectedModel, models) { selectedModel = it }
            DropdownMenu(selectedYear, years) { selectedYear = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "$selectedManufacturer $selectedModel - $selectedYear", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "details details details details details", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Trim", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            trims.forEach { trim ->
                TrimCard(trim)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DropdownMenu(selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(text = selectedOption, color = Color.White)
        }
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = Modifier.background(Color.DarkGray)
//        ) {
//            options.forEach { option ->
//                DropdownMenuItem(
//                    onClick = {
//                        onOptionSelected(option)
//                        expanded = false
//                    },
//                    modifier = Modifier.background(Color.DarkGray)
//                ) {
//                    Text(text = option, color = Color.White)
//                }
//            }
//        }
    }
}

@Composable
fun TrimCard(trim: Trim) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = trim.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Surface(
                    modifier = Modifier.background(Color.Blue, shape = RoundedCornerShape(50)),
                    color = Color.Transparent
                ) {
                    Text(
                        text = "${trim.rating}/5",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Text(text = "${trim.reviewCount} Reviews", color = Color.Gray, fontSize = 14.sp)
            Text(text = trim.details, color = Color.White, fontSize = 14.sp)
        }
    }
}

data class Trim(val name: String, val rating: Double, val reviewCount: Int, val details: String)

