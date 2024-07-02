package com.example.carguru.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.carguru.services.DropdownState
import com.example.carguru.viewmodels.CarRepository

@Composable
fun CarFilterDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onFilterApplied: (year: String?, make: String?, model: String?, trim: String?) -> Unit,
    yearsState: DropdownState<Int>,
    makesState: DropdownState<String>,
    modelsState: DropdownState<String>,
    trimsState: DropdownState<String>,
    carRepository: CarRepository
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {

                Column(modifier = Modifier.padding(16.dp)) {
                    CarDropdowns(
                        yearsState = yearsState,
                        makesState = makesState,
                        modelsState = modelsState,
                        trimsState = trimsState,
                        carRepository = carRepository
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            onFilterApplied(yearsState.selected.value,
                                makesState.selected.value,
                                modelsState.selected.value,
                                trimsState.selected.value)
                            onDismissRequest()
                        }) {
                            Text("Apply")
                        }
                    }
                }
            }
        }
    }
}
