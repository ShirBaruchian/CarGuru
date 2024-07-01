package com.example.carguru.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carguru.services.DropdownState
import com.example.carguru.viewmodels.CarRepository
import kotlinx.coroutines.launch

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
    var expandedState by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedState = true },
            trailingIcon = {
                Icon(
                    imageVector = if (expandedState) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expandedState = !expandedState }
                )
            },
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )
        DropdownMenu(
            expanded = expandedState,
            onDismissRequest = { expandedState = false },
            modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
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
                        expandedState = false
                    }
                )
            }
        }
    }
}

@Composable
fun CarDropdowns(
    yearsState: DropdownState<Int>,
    makesState: DropdownState<String>,
    modelsState: DropdownState<String>,
    trimsState: DropdownState<String>,
    viewModel: CarRepository
) {
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

    LaunchedEffect(makesState.selected.value, yearsState.selected.value) {
        if (makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                modelsState.items.value = viewModel.getModels(makesState.selected.value, yearsState.selected.value.toInt())
                modelsState.selected.value = ""  // Reset selected model when make changes
            }
        }
    }

    LaunchedEffect(modelsState.selected.value, makesState.selected.value, yearsState.selected.value) {
        if (modelsState.selected.value.isNotEmpty() && makesState.selected.value.isNotEmpty() && yearsState.selected.value.isNotEmpty()) {
            coroutineScope.launch {
                trimsState.items.value = viewModel.getTrims(makesState.selected.value, modelsState.selected.value, yearsState.selected.value.toInt())
                trimsState.selected.value = ""  // Reset selected trim when model changes
            }
        } else {
            trimsState.selected.value = ""
        }
    }

    Column {
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
        Row {
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuField(
                label = "Trim",
                options = trimsState.items.value,
                selectedOption = trimsState.selected.value,
                expanded = trimsState.expanded.value,
                onOptionSelected = { trimsState.selected.value = it },
                onExpandedChange = { trimsState.expanded.value = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}