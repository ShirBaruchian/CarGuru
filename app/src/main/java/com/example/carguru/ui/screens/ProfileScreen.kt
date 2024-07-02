package com.example.carguru.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.carguru.R
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import com.example.carguru.data.model.User
import androidx.compose.foundation.layout.*
import androidx.navigation.NavHostController
import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import com.example.carguru.viewmodels.UserViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.asImageBitmap
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.carguru.data.local.UserEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, profile: UserEntity, userViewModel: UserViewModel) {
    var isEditMode by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf(profile.username) }
    val email by remember { mutableStateOf(profile.email) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    userViewModel.fetchCurrentUser()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    LaunchedEffect(profileImageUri, profile.profileImageUrl) {
        if (profileImageUri != null) {
            profileImageBitmap = withContext(Dispatchers.IO) {
                try {
                    Picasso.get().load(profileImageUri).resize(100, 100).centerCrop().get()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        } else if (profile.profileImageUrl != null) {
            profileImageBitmap = withContext(Dispatchers.IO) {
                try {
                    Picasso.get().load(profile.profileImageUrl).resize(100, 100).centerCrop().get()
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
                title = { Text("Profile") },
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
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.BottomEnd
            ) {
                profileImageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } ?: run {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background), // Placeholder image
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                if (isEditMode) {
                    IconButton(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White, shape = CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with actual edit icon
                            contentDescription = "Edit Image",
                            tint = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = email, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isEditMode) {
                        userViewModel.updateProfile(
                            profile.id,
                            newUsername = userName,
                            newProfileImageUri = profileImageUri,
                            onSuccess = {
                                isEditMode = false
                                Toast.makeText(
                                    context,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onFailure = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        isEditMode = true
                    }
                }
            ) {
                Text(text = if (isEditMode) "Save Profile" else "Edit Profile")
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (isEditMode) {
                TextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )
            } else {
                Text(
                    text = "Username",
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(text = userName, fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    userViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log out", color = Color.White)
            }
        }
    }
}