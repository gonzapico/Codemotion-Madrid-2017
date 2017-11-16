package xyz.gonzapico.codemotion2017.data

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


/**
 * Created by gonzapico on 30/10/2017.
 */
interface CodemotionAPI {
  // https://www.koliseo.com/codemotion/codemotion-madrid/r4p/5632002325741568/agenda
  @GET("r4p/5632002325741568/agenda")
  fun agenda(): Call<CodemotionAPIResponse>

  companion object {
    const val END_POINT = "https://www.koliseo.com/codemotion/codemotion-madrid/"

    const val DATE_FORMAT = "dd-MM-yyyy HH:mm:ss"
    fun create(): CodemotionAPI {
      val gson = GsonBuilder()
          .setDateFormat(DATE_FORMAT)
          .create()

      val logging = HttpLoggingInterceptor()
      logging.level = BODY

      val client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Referer", "https://2017.codemotion.es/codemotion-agenda.html")
            .addHeader("Origin", "https://2017.codemotion.es")
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(newRequest)
      }.addInterceptor(logging).build()

      val retrofit = Retrofit.Builder()
          .client(client)
          .baseUrl(CodemotionAPI.END_POINT)
          .addConverterFactory(GsonConverterFactory.create(gson))
          .build()

      return retrofit.create(CodemotionAPI::class.java)
    }
  }
}