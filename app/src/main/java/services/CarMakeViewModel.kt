package services

//import ApiClient.carQueryService
//import CarQueryApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*


//class CarMakeViewModel : ViewModel() {
//
//    private val apiService = ApiClient.retrofit.create(CarQueryApi::class.java)
//    private val _carMakes = mutableStateListOf<CarMake>()
//    val carMakes: List<CarMake> = _carMakes
//
//    init {
//        fetchCarMakes(2000, 1)
//    }
//
//    private fun fetchCarMakes(year: Int, soldInUs: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val call = apiService.getCarMakes("getMakes", year, soldInUs)
//            call.enqueue(object : Callback<CarMakesResponse> {
//                override fun onResponse(call: Call<CarMakesResponse>, response: Response<CarMakesResponse>) {
//                    if (response.isSuccessful) {
//                        response.body()?.makes?.let {
//                            _carMakes.clear()
//                            _carMakes.addAll(it)
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<CarMakesResponse>, t: Throwable) {
//                    // Handle error
//                }
//            })
//        }
//    }
//}
//class CarViewModel : ViewModel() {
//    private val _carMakes = MutableLiveData<List<CarMake>>()
//    val carMakes: LiveData<List<CarMake>> = _carMakes
//
//    private val _carYears = MutableLiveData<List<String>>()
//    val carYears: LiveData<List<String>> = _carYears
//
//    private val _carModels = MutableLiveData<List<CarModel>>()
//    val carModels: LiveData<List<CarModel>> = _carModels
//
//    init {
//        fetchCarYears()
//    }
//
//    private fun fetchCarMakes(year: String) {
//        viewModelScope.launch {
//            val response = carQueryService.getMakes()
//            if (response.isSuccessful) {
//                _carMakes.postValue(response.body()?.Makes)
//            }
//        }
//    }
//
//    fun fetchCarYears() {
//        viewModelScope.launch {
//            val response = carQueryService.getYears()
//            if (response.isSuccessful) {
//                val years = response.body()?.Years
//                if (years != null) {
//                    val yearRange = (years.min_year.toInt()..years.max_year.toInt()).map { it.toString() }
//                    _carYears.postValue(yearRange)
//                }
//            }
//        }
//    }
//
//    fun fetchCarModels(make: String, year: String) {
//        viewModelScope.launch {
//            try {
//                val response = ApiClient.carQueryService.getModels(make = make, year = year)
//                if (response.isSuccessful) {
//                    _carModels.postValue(response.body()?.Models)
//                }
//            } catch (e: Exception) {
//                // Handle the exception appropriately
//            }
//        }
//    }
////
////    fun onYearSelected(year: String) {
////        fetchCarMakes(year)
////    }
//}


class CarViewModel : ViewModel() {
    private val repository = CarRepository()
    private val _years = mutableStateOf(listOf<String>())
    val years: State<List<String>> = _years

    init {
        viewModelScope.launch {
            _years.value = repository.getYears()
        }
    }
}
class CarRepository {
    suspend fun getYears(): List<String> {
//        return RetrofitInstance.api.getYears().years
        return listOf("2001","2002")
    }
}


