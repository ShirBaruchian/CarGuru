package com.example.carguru.ui.components

import java.util.*
import android.widget.DatePicker
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import android.app.DatePickerDialog
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var date by remember { mutableStateOf(selectedDate) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(date)
        },
        year, month, day
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { datePickerDialog.show() }) {
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
    }
}
