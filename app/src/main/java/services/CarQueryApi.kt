import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import services.CarMakesResponse
import services.CarModelsResponse
import services.CarYearsResponse

//interface CarQueryApi {
//    @GET("/api/0.3/")
//    fun getCarMakes(
//        @Query("cmd") command: String,
//        @Query("year") year: Int,
//        @Query("sold_in_us") soldInUs: Int
//    ): Call<CarMakesResponse>
//}
interface CarQueryService {
    @GET("/api/0.3/")
    suspend fun getMakes(@Query("cmd") cmd: String = "getMakes"): Response<CarMakesResponse>

    @GET("/api/0.3/")
    suspend fun getYears(@Query("cmd") cmd: String = "getYears"): Response<CarYearsResponse>

    @GET("/api/0.3/")
    suspend fun getModels(@Query("cmd") cmd: String = "getModels", @Query("make") make: String, @Query("year") year: String): Response<CarModelsResponse>
}
