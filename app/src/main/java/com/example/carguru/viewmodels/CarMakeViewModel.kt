package com.example.carguru.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import com.google.gson.JsonParser
import kotlinx.coroutines.withContext

class CarRepository : ViewModel() {

    suspend fun getYears(): List<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.carQueryService.getYears()
                val jsonResponse = response.removePrefix("?(").removeSuffix(");")
                val jsonElement = JsonParser.parseString(jsonResponse).asJsonObject
                val yearsObject = jsonElement.getAsJsonObject("Years")
                val minYear = yearsObject.get("min_year").asInt
                val maxYear = yearsObject.get("max_year").asInt
                (minYear..maxYear).toList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getMakes(year: Int): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.carQueryService.getMakes(year)
                val jsonResponse = response.removePrefix("?(").removeSuffix(");")
                val jsonElement = JsonParser.parseString(jsonResponse).asJsonObject
                val makesArray = jsonElement.getAsJsonArray("Makes")
                makesArray.map { it.asJsonObject.get("make_display").asString }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getModels(make: String, year: Int): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.carQueryService.getModels(make, year)
                val jsonResponse = response.removePrefix("?(").removeSuffix(");")
                val jsonElement = JsonParser.parseString(jsonResponse).asJsonObject
                val modelsArray = jsonElement.getAsJsonArray("Models")
                modelsArray.map { it.asJsonObject.get("model_name").asString }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getTrims(make: String, model: String, year: Int): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.carQueryService.getTrims(make, model, year)
                val jsonResponse = response.removePrefix("?(").removeSuffix(");")
                val jsonElement = JsonParser.parseString(jsonResponse).asJsonObject
                val trimsArray = jsonElement.getAsJsonArray("Trims")
                trimsArray.map { it.asJsonObject.get("model_trim").asString }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}



