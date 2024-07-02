package com.example.carguru.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carguru.models.ReviewWithUser
import com.example.carguru.viewmodels.ReviewsViewModel
import com.example.carguru.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.Alignment
import com.example.carguru.viewmodels.CarRepository
import com.example.carguru.services.DropdownState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.carguru.ui.components.CarFilterDialog
import com.example.carguru.ui.components.ReviewFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    reviewsViewModel: ReviewsViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    viewModel: CarRepository = viewModel()
) {

    val reviews by reviewsViewModel.reviews.collectAsState()
    val userName by userViewModel.userName.collectAsState()
    val loading by reviewsViewModel.loading.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val yearsState = remember { DropdownState<Int>() }
    val makesState = remember { DropdownState<String>() }
    val modelsState = remember { DropdownState<String>() }
    val trimsState = remember { DropdownState<String>() }

    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
    }

    LaunchedEffect(yearsState.selected.value) {
        reviewsViewModel.setYear(yearsState.selected.value)
    }

    LaunchedEffect(makesState.selected.value) {
        reviewsViewModel.setMake(makesState.selected.value)
    }

    LaunchedEffect(modelsState.selected.value) {
        reviewsViewModel.setModel(modelsState.selected.value)
    }

    LaunchedEffect(trimsState.selected.value) {
        reviewsViewModel.setTrim(trimsState.selected.value)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Reviews") },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Filter")
                    }
                    IconButton(onClick = {navController.navigate("profile")}) {
                        Icon(imageVector = Icons.Default.AccountBox, contentDescription = "EditProfile")
                    }
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = "Hello $userName",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (yearsState.selected.value.isNotEmpty()) {
                    ReviewFilter(text = yearsState.selected.value) { yearsState.selected.value = "" }
                }
                if (makesState.selected.value.isNotEmpty()) {
                    ReviewFilter(text = makesState.selected.value) { makesState.selected.value = "" }
                }
                if (modelsState.selected.value.isNotEmpty()) {
                    ReviewFilter(text = modelsState.selected.value) { modelsState.selected.value = "" }
                }
                if (trimsState.selected.value.isNotEmpty()) {
                    ReviewFilter(text = trimsState.selected.value) { trimsState.selected.value = "" }
                }
            }
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp) // size of the indicator
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f) // Use weight to take the remaining space
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {

                        items(reviews) { reviewWithUser ->
                            CompactReviewItem(reviewWithUser, navController)
                            Divider()
                        }

                }
            }
            CarFilterDialog(
                showDialog = showDialog,
                onDismissRequest = { showDialog = false },
                onFilterApplied = { year, make, model, trim ->
                    reviewsViewModel.setYear(year)
                    reviewsViewModel.setMake(make)
                    reviewsViewModel.setModel(model)
                    reviewsViewModel.setTrim(trim)
                },
                yearsState,
                makesState,
                modelsState,
                trimsState,
                carRepository = viewModel
            )
        }
    }
}

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
        }
    }
}

@Composable
fun CompactReviewItem(
    reviewWithUser: ReviewWithUser,
    navController: NavController
) {
    val dateFormat = remember { SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm:ss a", Locale.getDefault()) }
    val formattedDate = reviewWithUser.review.timestamp?.let { dateFormat.format(it) } ?: "Unknown"

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable { navController.navigate("reviewDetail/${reviewWithUser.review.id}") }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Manufacturer: ${reviewWithUser.review.manufacturer}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Model: ${reviewWithUser.review.model}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Year: ${reviewWithUser.review.year}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Trim: ${reviewWithUser.review.trim}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.Gray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reviewWithUser.review.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            RatingBar(rating = reviewWithUser.review.rating) {}
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Review by: ${reviewWithUser.username}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}