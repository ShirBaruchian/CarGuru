package com.example.carguru.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.carguru.data.model.User
import androidx.compose.foundation.background
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController,profile: User, onLogout: () -> Unit) {
    var isEditMode by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf(profile.username) }
    var email by remember { mutableStateOf(profile.email) }
    var birthdate by remember { mutableStateOf(profile.birthdate) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Image(
//            painter = rememberImagePainter(data = imageUrl),
//            contentDescription = null,
//            modifier = Modifier
//                .size(100.dp)
//                .clip(CircleShape),
//            contentScale = ContentScale.Crop
//        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = userName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = email, color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { isEditMode = !isEditMode },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(text = if (isEditMode) "Save Profile" else "Edit Profile", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isEditMode) {
            TextField(
                value = birthdate,
                onValueChange = { birthdate = it },
                label = { Text("birthday", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
//                    textColor = Color.White,
                    containerColor = Color.DarkGray,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("userName", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
//                    textColor = Color.White,
                    containerColor = Color.DarkGray,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        } else {
            Text(text = "birthday", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
            Text(text = birthdate, color = Color.White, fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "userName", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
            Text(text = userName, color = Color.White, fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {navController.navigate("main")},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log out", color = Color.White)
        }
    }
}