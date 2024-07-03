package com.example.carguru.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.draw.clip
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carguru.services.DropdownState
import com.example.carguru.ui.components.CarDropdowns
import com.example.carguru.utils.hideKeyboard
import com.example.carguru.viewmodels.AddReviewViewModel
import com.example.carguru.viewmodels.CarRepository
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(
    navController: NavController,
    addReviewViewModel: AddReviewViewModel = viewModel(),
    carRepository: CarRepository
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var title by remember { mutableStateOf("") }
    val yearsState = remember { DropdownState<Int>() }
    val makesState = remember { DropdownState<String>() }
    val modelsState = remember { DropdownState<String>() }
    val trimsState = remember { DropdownState<String>() }
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(imageUri) {
        imageUri?.let {
            imageBitmap = withContext(Dispatchers.IO) {
                Picasso.get().load(it).resize(100, 100).centerCrop().get()
            }
        }
    }

    val onAddReviewClicked = {
        if (title.isNotEmpty() && modelsState.selected.value.isNotEmpty()
            && makesState.selected.value.isNotEmpty() &&
            yearsState.selected.value.isNotEmpty() && rating > 0 && reviewText.isNotEmpty()
        ) {
            addReviewViewModel.addReview(
                title, makesState.selected.value,
                modelsState.selected.value,
                yearsState.selected.value,
                trimsState.selected.value, rating, reviewText, imageUri
            ) { error ->
                if (error != null) {
                    errorMessage = error
                } else {
                    navController.navigate("home") {
                        popUpTo("addReview") { inclusive = true }
                    }
                }
            }
        } else {
            errorMessage = "Please fill in all fields and provide a rating."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Review") },
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
        Column(
            modifier = Modifier
                .fillMaxSize().pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        hideKeyboard(context)
                    })
                }
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            CarDropdowns(
                yearsState = yearsState,
                makesState = makesState,
                modelsState = modelsState,
                trimsState = trimsState,
                carRepository
            )
            Spacer(modifier = Modifier.height(16.dp))
            RatingBar(rating = rating) { newRating -> rating = newRating }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("Review") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(bottom = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier.clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Text("Select Image", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAddReviewClicked, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onAddReviewClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Add Review")
                }
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun RatingBar(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}