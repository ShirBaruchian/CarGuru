package com.example.carguru.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.sp
import com.example.carguru.models.ReviewWithUser
import com.example.carguru.viewmodels.ReviewsViewModel
import com.example.carguru.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import com.example.carguru.services.DropdownState
import com.example.carguru.viewmodels.CarRepository
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    reviewsViewModel: ReviewsViewModel,
    userViewModel: UserViewModel,
    viewModel: CarRepository
) {
    val user = FirebaseAuth.getInstance().currentUser
    val userName = user?.displayName ?: user?.email ?: "User"

    val yearsState = remember { DropdownState<Int>() }
    val makesState = remember { DropdownState<String>() }
    val modelsState = remember { DropdownState<String>() }
    val trimsState = remember { DropdownState<String>() }
    val coroutineScope = rememberCoroutineScope()

    val reviews by reviewsViewModel.reviews.collectAsState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            yearsState.items.value = viewModel.getYears()
        }
    }

    LaunchedEffect(yearsState.selected.value) {
        if (yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                makesState.items.value = viewModel.getMakes(yearsState.selected.value.toInt())
                makesState.selected.value = ""  // Reset selected make when year changes
            }
        }
    }

    LaunchedEffect(makesState.selected.value, yearsState.selected.value) {
        if (makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                modelsState.items.value = viewModel.getModels(
                    makesState.selected.value,
                    yearsState.selected.value.toInt()
                )
                modelsState.selected.value = ""  // Reset selected model when make changes
            }
        }
    }

    LaunchedEffect(
        modelsState.selected.value,
        makesState.selected.value,
        yearsState.selected.value
    ) {
        if (modelsState.selected.value.isNotEmpty() && makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                trimsState.items.value = viewModel.getTrims(
                    makesState.selected.value,
                    modelsState.selected.value,
                    yearsState.selected.value.toInt()
                )
                trimsState.selected.value = ""  // Reset selected trim when model changes
            }
        } else {
            trimsState.selected.value = ""
        }
    }

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
                    TextButton(onClick = { navController.navigate("profile") }) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Top),

                            )
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuField(
                        label = "Year",
                        options = yearsState.items.value.map { it.toString() },
                        selectedOption = yearsState.selected.value,
                        expanded = yearsState.expanded.value,
                        onOptionSelected = { yearsState.selected.value = it },
                        onExpandedChange = { yearsState.expanded.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        DropdownMenuField(
                            label = "Make",
                            options = makesState.items.value,
                            selectedOption = makesState.selected.value,
                            expanded = makesState.expanded.value,
                            onOptionSelected = { makesState.selected.value = it },
                            onExpandedChange = { makesState.expanded.value = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))
                        DropdownMenuField(
                            label = "Model",
                            options = modelsState.items.value,
                            selectedOption = modelsState.selected.value,
                            expanded = modelsState.expanded.value,
                            onOptionSelected = { modelsState.selected.value = it },
                            onExpandedChange = { modelsState.expanded.value = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty() && modelsState.selected.value.isNotEmpty()) {
                        Text(
                            "${makesState.selected.value} ${modelsState.selected.value} - ${yearsState.selected.value}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (trimsState.items.value.isNotEmpty()) {
                        Text("Available Trims:", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        trimsState.items.value.forEach { trim ->
                            TrimCard(
                                name = trim,
                                rating = 4.5f,
                                reviews = 100
                            )  // Mock ratings and reviews
                        }
                    } else if (modelsState.selected.value.isNotEmpty()) {
                        Text(
                            "No trims available for the selected make, model, and year.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f) // Use weight to take the remaining space
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
}


@Composable
fun CompactReviewItem(
    reviewWithUser: ReviewWithUser,
    navController: NavController
) {
    val dateFormat =
        remember { SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm:ss a", Locale.getDefault()) }
    val formattedDate =
        reviewWithUser.review.timestamp?.let { dateFormat.format(it) } ?: "Unknown"

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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ReviewDetailScreen(
        navController: NavController,
        reviewId: String,
        reviewsViewModel: ReviewsViewModel
    ) {
        val reviewWithUser by reviewsViewModel.getReviewWithUser(reviewId)
            .collectAsState(initial = null)

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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DropdownMenuField(
        label: String,
        options: List<String>,
        selectedOption: String,
        expanded: Boolean,
        onOptionSelected: (String) -> Unit,
        onExpandedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                label = { Text(label) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(true) },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { onExpandedChange(!expanded) }
                    )
                },
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 14.sp
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            onExpandedChange(false)
                        }
                    )
                }
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


