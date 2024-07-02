package com.example.carguru.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carguru.models.ReviewWithUser
import com.example.carguru.viewmodels.ReviewsViewModel
import com.example.carguru.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import com.example.carguru.viewmodels.CarRepository
import com.example.carguru.services.DropdownState
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import com.example.carguru.ui.components.CarDropdowns
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    reviewsViewModel: ReviewsViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    viewModel: CarRepository = viewModel()
) {
    val yearsState = remember { DropdownState<Int>() }
    val makesState = remember { DropdownState<String>() }
    val modelsState = remember { DropdownState<String>() }
    val trimsState = remember { DropdownState<String>() }
    val coroutineScope = rememberCoroutineScope()

    val reviews by reviewsViewModel.reviews.collectAsState()
    val userName by userViewModel.userName.collectAsState()
    val loading by reviewsViewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            yearsState.items.value = viewModel.getYears()
        }
    }

    LaunchedEffect(yearsState.selected.value) {
        if (yearsState.selected.value.isNotEmpty()) {
            reviewsViewModel.setYear(yearsState.selected.value)
            coroutineScope.launch {
                makesState.items.value = viewModel.getMakes(yearsState.selected.value.toInt())
                makesState.selected.value = ""  // Reset selected make when year changes
            }
        }
    }

    LaunchedEffect(makesState.selected.value, yearsState.selected.value) {
        if (makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            reviewsViewModel.setMake(makesState.selected.value)
            coroutineScope.launch {
                modelsState.items.value = viewModel.getModels(makesState.selected.value, yearsState.selected.value.toInt())
                modelsState.selected.value = ""  // Reset selected model when make changes
            }
        }
    }

    LaunchedEffect(modelsState.selected.value, makesState.selected.value, yearsState.selected.value) {
        if (modelsState.selected.value.isNotEmpty() && makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            reviewsViewModel.setModel(modelsState.selected.value)
            coroutineScope.launch {
                trimsState.items.value = viewModel.getTrims(makesState.selected.value, modelsState.selected.value, yearsState.selected.value.toInt())
                trimsState.selected.value = ""  // Reset selected trim when model changes
            }
        }
    }

    LaunchedEffect(trimsState.selected.value, modelsState.selected.value, makesState.selected.value, yearsState.selected.value) {
        if (modelsState.selected.value.isNotEmpty() && makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            reviewsViewModel.setModel(modelsState.selected.value)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Reviews") },
                actions = {
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
            CarDropdowns(
                yearsState = yearsState,
                makesState = makesState,
                modelsState = modelsState,
                trimsState = trimsState,
                viewModel = viewModel
            )
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
                    )                }
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