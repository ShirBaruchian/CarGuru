package com.example.carguru.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.carguru.models.ReviewWithUser
import com.example.carguru.viewmodels.ReviewsViewModel
import com.example.carguru.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    reviewsViewModel: ReviewsViewModel,
    userViewModel: UserViewModel
) {
    val reviews by reviewsViewModel.reviews.collectAsState()
    val userName by userViewModel.userName.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Reviews") },
                actions = {
                    IconButton(onClick = {
                        userViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addReview") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Review")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                text = "Hello $userName",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                if (reviews.isEmpty()) {
                    item {
                        Text("No reviews available", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    items(reviews) { reviewWithUser ->
                        CompactReviewItem(reviewWithUser, navController)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun CompactReviewItem(
    reviewWithUser: ReviewWithUser,
    navController: NavController
) {
    //val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    //val formattedDate = reviewWithUser.review.timestamp?.let { dateFormat.format(it) } ?: "Unknown"

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { navController.navigate("reviewDetail/${reviewWithUser.review.id}") } // Navigate to the detailed view
    ) {
        Text(
            text = "${reviewWithUser.review.manufacturer} ${reviewWithUser.review.model} (${reviewWithUser.review.year}) - ${reviewWithUser.review.trim}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = reviewWithUser.review.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = reviewWithUser.review.rating) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    navController: NavController,
    reviewId: String,
    reviewsViewModel: ReviewsViewModel
) {
    val reviewWithUser by reviewsViewModel.getReviewWithUser(reviewId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        reviewWithUser?.let { review: ReviewWithUser ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "${review.review.manufacturer} ${review.review.model} (${review.review.year}) - ${review.review.trim}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = review.review.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatingBar(rating = review.review.rating) {}
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Review by: ${review.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = review.review.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun ReviewItem(reviewWithUser: ReviewWithUser) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = reviewWithUser.review.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "By ${reviewWithUser.review.manufacturer} ${reviewWithUser.review.model} (${reviewWithUser.review.year} ${reviewWithUser.review.trim})",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Review by: ${reviewWithUser.username}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = reviewWithUser.review.text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        RatingBar(rating = reviewWithUser.review.rating) {}
    }
}
