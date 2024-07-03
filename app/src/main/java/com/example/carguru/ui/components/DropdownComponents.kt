package com.example.carguru.ui.components

import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import com.example.carguru.services.DropdownState
import androidx.compose.ui.text.style.TextOverflow
import com.example.carguru.viewmodels.CarRepository
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.ArrowDropDown

@Composable
fun CarDropdowns(
    yearsState: DropdownState<Int>,
    makesState: DropdownState<String>,
    modelsState: DropdownState<String>,
    trimsState: DropdownState<String>,
    carRepository: CarRepository
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            yearsState.items.value = carRepository.getYears()
        }
    }

    LaunchedEffect(yearsState.selected.value) {
        if (yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                makesState.items.value = carRepository.getMakes(yearsState.selected.value.toInt())
                makesState.selected.value = ""
            }
        }
    }

    LaunchedEffect(makesState.selected.value, yearsState.selected.value) {
        if (makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                modelsState.items.value = carRepository.getModels(makesState.selected.value, yearsState.selected.value.toInt())
                modelsState.selected.value = ""
            }
        }
    }

    LaunchedEffect(modelsState.selected.value, makesState.selected.value, yearsState.selected.value) {
        if (modelsState.selected.value.isNotEmpty() && makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                trimsState.items.value = carRepository.getTrims(makesState.selected.value, modelsState.selected.value, yearsState.selected.value.toInt())
                trimsState.selected.value = ""
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuField(
            label = "Year",
            options = yearsState.items.value.map { it.toString() },
            selectedOption = yearsState.selected.value,
            expanded = yearsState.expanded.value,
            onOptionSelected = { yearsState.selected.value = it },
            onExpandedChange = { yearsState.expanded.value = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        DropdownMenuField(
            label = "Make",
            options = makesState.items.value,
            selectedOption = makesState.selected.value,
            expanded = makesState.expanded.value,
            onOptionSelected = { makesState.selected.value = it },
            onExpandedChange = { makesState.expanded.value = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        DropdownMenuField(
            label = "Model",
            options = modelsState.items.value,
            selectedOption = modelsState.selected.value,
            expanded = modelsState.expanded.value,
            onOptionSelected = { modelsState.selected.value = it },
            onExpandedChange = { modelsState.expanded.value = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        DropdownMenuField(
            label = "Trim",
            options = trimsState.items.value,
            selectedOption = trimsState.selected.value,
            expanded = trimsState.expanded.value,
            onOptionSelected = { trimsState.selected.value = it },
            onExpandedChange = { trimsState.expanded.value = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

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
    Box(modifier = modifier) {
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
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White).heightIn(max = 300.dp)
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
