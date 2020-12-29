
package pl.covid19.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import pl.covid19.util.Constants.BASEAPI_URL
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * A retrofit service to fetch list.
 */

interface govplxService {

    @GET("getPLGOVPLX.php")
    suspend fun getbyDate(@Query("date") type: String, @Query("uid") uid: String=""): List<Networkgovplx>

    @GET("getPLGOVPLX.php")
    suspend fun getbyId(@Query("id") type: String, @Query("uid") uid: String=""): List<Networkgovplx>


    @GET("getPLGOVPLX.php")
    suspend fun getAll(@Query("uid") uid: String=""): List<Networkgovplx>

}


interface fazyService {
    @GET("getPLFazy.php")
    suspend fun getAll(@Query("uid") userid: String=""): List<NetworkFazy>
}

interface versionService {
    @GET("getPLVersion.php")
    suspend fun getAll(@Query("uid") userid: String=""): List<NetworkVersion>
}

//TODO get from localization name of powiat
interface localizationService {
    @GET("xxxxx.json")
    suspend fun getGusId(@Query("localization") type: String): String
    @GET("Powiat.json")
    fun getGusAll(): Deferred<String>

}


/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

/**
 * Main entry point for network access. Call like `Network.devbytes.getPlaylist()`
 */
object Network {
      var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
            .baseUrl(BASEAPI_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    val govplxList = retrofit.create(govplxService::class.java)
    val fazyList = retrofit.create(fazyService::class.java)
    val versionList = retrofit.create(versionService::class.java)
}
