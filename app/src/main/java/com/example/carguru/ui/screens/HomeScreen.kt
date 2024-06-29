package com.example.carguru.ui.screens

import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import com.example.carguru.services.DropdownState
import androidx.compose.ui.text.style.TextOverflow
import com.example.carguru.viewmodels.CarRepository
import androidx.compose.material.icons.filled.ArrowDropDown

@Composable
fun HomeScreen(navController: NavController, userName: String, viewModel: CarRepository) {
    val yearsState = remember { DropdownState<Int>() }
    val makesState = remember { DropdownState<String>() }
    val modelsState = remember { DropdownState<String>() }
    val trimsState = remember { DropdownState<String>() }
    val coroutineScope = rememberCoroutineScope()


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

    LaunchedEffect(makesState.selected.value,yearsState.selected.value) {
        if (makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                modelsState.items.value = viewModel.getModels(makesState.selected.value, yearsState.selected.value.toInt())
                modelsState.selected.value = ""  // Reset selected model when make changes
            }
        }
    }

    LaunchedEffect(modelsState.selected.value,makesState.selected.value,yearsState.selected.value) {
        if (modelsState.selected.value.isNotEmpty() && makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                trimsState.items.value = viewModel.getTrims(makesState.selected.value, modelsState.selected.value, yearsState.selected.value.toInt())
                trimsState.selected.value = ""  // Reset selected trim when model changes
            }
        }else{
            trimsState.selected.value = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Top)
            )
            TextButton(onClick = {navController.navigate("profile")}) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Top),

                    )
            }
        }
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
        }
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
        if (makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty() && modelsState.selected.value.isNotEmpty()){
            Text("${makesState.selected.value} ${modelsState.selected.value} - ${yearsState.selected.value}", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (trimsState.items.value.isNotEmpty()) {
            Text("Available Trims:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            trimsState.items.value.forEach { trim ->
                TrimCard(name = trim, rating = 4.5f, reviews = 100)  // Mock ratings and reviews
            }
        } else if (modelsState.selected.value.isNotEmpty()) {
            Text("No trims available for the selected make, model, and year.", style = MaterialTheme.typography.bodySmall)
        }
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