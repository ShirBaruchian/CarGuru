package com.example.carguru.ui.screens

import java.util.*
import android.net.Uri
import android.widget.Toast
import com.example.carguru.R
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.squareup.picasso.Picasso
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.input.TextFieldValue
import com.example.carguru.viewmodels.ReviewsViewModel
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    navController: NavController,
    reviewId: String,
    reviewsViewModel: ReviewsViewModel
) {
    val reviewWithUser by reviewsViewModel.getReview(reviewId).collectAsState(initial = null)
    var isEditMode by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(TextFieldValue("")) }
    var editedText by remember { mutableStateOf(TextFieldValue("")) }
    var editedImageUri by remember { mutableStateOf<Uri?>(null) }
    var editedImageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val context = LocalContext.current

    val currentUser = FirebaseAuth.getInstance().currentUser

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        editedImageUri = uri
    }

    LaunchedEffect(editedImageUri) {
        editedImageUri?.let {
            editedImageBitmap = withContext(Dispatchers.IO) {
                try {
                    Picasso.get().load(it).get()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentUser?.uid == reviewWithUser?.review?.userId) {
                        IconButton(onClick = { isEditMode = !isEditMode }) {
                            Icon(if (isEditMode) Icons.Default.Close else Icons.Default.Build, contentDescription = null)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        reviewWithUser?.let { review ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                if (isEditMode) {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text(review.review.title) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        label = { Text("Review Text") },
                        placeholder = { Text(review.review.text) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        editedImageBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Review Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } ?: run {
                            Image(
                                painter = painterResource(id = R.drawable.logo), // Placeholder image
                                contentDescription = "Review Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Text(
                        text = review.review.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = review.review.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    review.review.imageUrl?.let { imageUrl ->
                        val imageBitmap by rememberUpdatedState(
                            newValue = loadImageFromUrl(imageUrl)
                        )
                        imageBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Review Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val dateFormat = remember { SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm:ss a", Locale.getDefault()) }
                val formattedDate = review.review.timestamp?.let { dateFormat.format(it) } ?: "Unknown"
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (isEditMode && currentUser?.uid == review.review.userId) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        reviewsViewModel.updateReview(
                            reviewId,
                            newTitle = if (editedTitle.text.isNotBlank()) editedTitle.text else review.review.title,
                            newText = if (editedText.text.isNotBlank()) editedText.text else review.review.text,
                            newImageUri = editedImageUri,
                            onSuccess = {
                                Toast.makeText(context, "Review updated successfully", Toast.LENGTH_SHORT).show()
                                isEditMode = false
                            },
                            onFailure = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }) {
                        Text("Save Changes")
                    }
                }
                if (isEditMode && currentUser?.uid == review.review.userId) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        reviewsViewModel.deleteReview(
                            reviewId,
                            onSuccess = {
                                Toast.makeText(context, "Review deleted successfully", Toast.LENGTH_SHORT).show()
                                navController.navigateUp()
                            },
                            onFailure = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }) {
                        Text("Delete Review")
                    }
                }
            }
        }
    }
}

@Composable
fun loadImageFromUrl(imageUrl: String): android.graphics.Bitmap? {
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(imageUrl) {
        withContext(Dispatchers.IO) {
            try {
                imageBitmap = Picasso.get().load(imageUrl).get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return imageBitmap
}