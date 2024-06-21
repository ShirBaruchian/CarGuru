package services

import com.google.gson.annotations.SerializedName

//data class CarMakesResponse(
//    @SerializedName("Makes") val makes: List<CarMake>
//)
//
//data class CarMake(
//    @SerializedName("make_id") val makeId: String,
//    @SerializedName("make_display") val makeDisplay: String
//)

data class CarMakesResponse(val Makes: List<CarMake>)
data class CarMake(val make_id: String, val make_display: String)

data class CarYearsResponse(val Years: Years)
data class Years(val min_year: String, val max_year: String)

data class CarModelsResponse(val Models: List<CarModel>)
data class CarModel(val model_name: String, val model_make_id: String)


