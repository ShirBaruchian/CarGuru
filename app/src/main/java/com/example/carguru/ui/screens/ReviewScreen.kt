package com.example.carguru.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.carguru.data.model.Review


@Composable
fun ReviewScreen() {
    val reviews = listOf(
        Review("efewrfe","gfvgrtgvs","fdgvbdsfbv","Name Name", "January 2023", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum at ante ac velit pharetra ullamcorper.", 4, "https://example.com/image.jpg"),
        Review("efewrdgfe","gfvgrtgvs","fdgvbdsfbv","Name Name", "December 2022", "Curabitur bibendum vehicula nisi at sagittis. Donec viverra faucibus", 5, "https://example.com/image.jpg"),
        Review("efewrgffe","gfvgrtgvs","fdgvbdsfbv","Name Name", "September 2022", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum at ante ac velit pharetra ullamcorper.", 3, "https://example.com/image.jpg")
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
        Header()
        CarTitle()
        ReviewList(reviews)
        Footer()
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Home", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Hello", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Erlich Bachman", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CarTitle() {
    Text(text = "Toyota corolla - 2022 - GLI", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
}

@Composable
fun ReviewList(reviews: List<Review>) {
    LazyColumn {
        items(reviews) { review ->
            ReviewItem(review)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = review.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = review.date, color = Color.Gray, fontSize = 14.sp)
            Text(text = review.text, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
            Row {
                repeat(review.rating) {
                    Text(text = "⭐", color = Color.Yellow, fontSize = 16.sp)
                }
            }
        }
//        Image(
//            painter = rememberImagePainter(review.imageUrl),
//            contentDescription = null,
//            modifier = Modifier.size(64.dp).padding(start = 8.dp),
//            contentScale = ContentScale.Crop
//        )
    }
}

@Composable
fun Footer() {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "got this car?", color = Color.White, fontSize = 14.sp)
        Text(text = "add your review", color = Color.Blue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

