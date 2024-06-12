package com.example.carguru.ui.screens

import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.material3.Button
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.material3.TextField
import androidx.navigation.NavHostController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun SignUpScreen(navController: NavHostController) {
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Sign Up")
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") }
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Confirm Password") }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            val isEmailValid = email.value.contains("@") && email.value.contains(".")
            val isPasswordValid = password.value.length >= 8 && password.value.any { it.isUpperCase() }
            val isConfirmPasswordValid = password.value == confirmPassword.value

            if (username.value.isEmpty() || email.value.isEmpty() || password.value.isEmpty() || confirmPassword.value.isEmpty()) {
                errorMessage.value = "Please fill all fields"
            } else if (!isEmailValid) {
                errorMessage.value = "Email is not valid"
            } else if (!isPasswordValid) {
                errorMessage.value = "Password must be at least 8 characters long and contain at least one uppercase letter"
            } else if(!isConfirmPasswordValid){
                errorMessage.value = "The passwords are not equal"
            } else{
                errorMessage.value = ""
            }
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (errorMessage.value.isNotEmpty()) {
            Text(text = errorMessage.value, color = Color.Red)
        }
        Button(onClick = {
            navController.navigate("login")
        }) {
            Text("Back to Login")
        }
    }
}