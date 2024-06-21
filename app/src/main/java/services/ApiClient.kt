import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

//object ApiClient {
//    private const val BASE_URL = "https://www.carqueryapi.com"
//
//    val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    val carQueryService: CarQueryApi.CarQueryService = retrofit.create(CarQueryApi.CarQueryService::class.java)
//
//}
interface ApiClient {
    @GET("getYears")
    suspend fun getYears(): YearsResponse
}

data class YearsResponse(val years: List<Int>)

object RetrofitInstance {
    val api: CarQueryService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.carqueryapi.com/api/0.3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CarQueryService::class.java)
    }
}
