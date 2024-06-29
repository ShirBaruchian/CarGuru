package com.example.carguru.ui.screens

import android.widget.Toast
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, profile: User, userViewModel: UserViewModel) {
    var isEditMode by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf(profile.username) }
    val email by remember { mutableStateOf(profile.email) }
    var birthdate by remember { mutableStateOf(profile.birthdate) }
    val context = LocalContext.current

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
                .padding(innerPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Placeholder for the image
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with actual image loading logic
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (isEditMode) {
                    IconButton(
                        onClick = {
                            // Handle image edit action
                            Toast.makeText(context, "Edit image clicked", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White, shape = CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google), // Replace with actual edit icon
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
                        userViewModel.updateUserDetails(
                            profile.id,
                            newUsername = userName,
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
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Birthday", fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
            Text(text = birthdate, fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
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