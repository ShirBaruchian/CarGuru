package com.example.carguru.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.carguru.services.CarRepository


@Composable
fun CarScreen(viewModel: CarRepository = viewModel()) {
    val years = remember { mutableStateOf<List<Int>>(emptyList()) }
    var selectedYear by remember { mutableStateOf("") }
    var expandedYear by remember { mutableStateOf(false) }
    val makes = remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedMake by remember { mutableStateOf("") }
    var expandedMake by remember { mutableStateOf(false) }
    val models = remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedModel by remember { mutableStateOf("") }
    var expandedModel by remember { mutableStateOf(false) }
    val trims = remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTrim by remember { mutableStateOf("") }
    var expandedTrim by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            years.value = viewModel.getYears()
        }
    }

    LaunchedEffect(selectedYear) {
        if (selectedYear.isNotEmpty()) {
            coroutineScope.launch {
                makes.value = viewModel.getMakes(selectedYear.toInt())
                selectedMake = ""  // Reset selected make when year changes
            }
        }
    }

    LaunchedEffect(selectedMake,selectedYear) {
        if (selectedMake.isNotEmpty() && selectedYear.isNotEmpty()) {
            coroutineScope.launch {
                models.value = viewModel.getModels(selectedMake, selectedYear.toInt())
                selectedModel = ""  // Reset selected model when make changes
            }
        }
    }

    LaunchedEffect(selectedModel,selectedMake,selectedYear) {
        if (selectedModel.isNotEmpty() && selectedMake.isNotEmpty() && selectedYear.isNotEmpty()) {
            coroutineScope.launch {
                trims.value = viewModel.getTrims(selectedMake, selectedModel, selectedYear.toInt())
                selectedTrim = ""  // Reset selected trim when model changes
            }
        }else{
            selectedTrim = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Home", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuField(
                label = "Year",
                options = years.value.map { it.toString() },
                selectedOption = selectedYear,
                expanded = expandedYear,
                onOptionSelected = { selectedYear = it },
                onExpandedChange = { expandedYear = it },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuField(
                label = "Make",
                options = makes.value,
                selectedOption = selectedMake,
                expanded = expandedMake,
                onOptionSelected = { selectedMake = it },
                onExpandedChange = { expandedMake = it },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuField(
                label = "Model",
                options = models.value,
                selectedOption = selectedModel,
                expanded = expandedModel,
                onOptionSelected = { selectedModel = it },
                onExpandedChange = { expandedModel = it },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedMake.isNotEmpty() && selectedYear.isNotEmpty() && selectedModel.isNotEmpty()){
            Text("${selectedMake} ${selectedModel} - ${selectedYear}", style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (trims.value.isNotEmpty()) {
            Text("Available Trims:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            trims.value.forEach { trim ->
                TrimCard(name = trim, rating = 4.5f, reviews = 100)  // Mock ratings and reviews
            }
        } else if (selectedModel.isNotEmpty()) {
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