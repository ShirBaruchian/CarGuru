package com.example.carguru.ui.screens

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import coil.compose.rememberImagePainter
import com.example.carguru.data.model.Car
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale

@Composable
fun CarItem(car: Car) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = car.brand, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = car.model, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberImagePainter(data = car.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}