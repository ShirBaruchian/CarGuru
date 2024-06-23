import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface CarQueryService {
    @GET("/api/0.3/?callback=?&cmd=getYears")
    suspend fun getYears(): String

    @GET("/api/0.3/?callback=?&cmd=getMakes")
    suspend fun getMakes(@Query("year") year: Int): String

    @GET("/api/0.3/?callback=?&cmd=getModels")
    suspend fun getModels(@Query("make") make: String, @Query("year") year: Int): String

    @GET("/api/0.3/?callback=?&cmd=getTrims")
    suspend fun getTrims(@Query("make") make: String, @Query("model") model: String, @Query("year") year: Int): String
}
