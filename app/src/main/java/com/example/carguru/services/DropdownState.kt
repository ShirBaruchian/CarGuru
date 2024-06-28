package com.example.carguru.services

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class DropdownState<T> (

    initialList: List<T> = emptyList(),
    initialSelected: String = ""

    ) {
        val items: MutableState<List<T>> = mutableStateOf(initialList)
        var selected: MutableState<String> = mutableStateOf(initialSelected)
        var expanded: MutableState<Boolean> = mutableStateOf(false)
    }
