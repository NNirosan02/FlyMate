package com.nnapps.flymate.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

class PlanespottersUserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            // Format: AppName/Version (Contact/BundleId)
            .header("User-Agent", "FlyMate/1.0 (com.nnapps.flymate; contact@flymate.local)")
            .build()
        return chain.proceed(request)
    }
}

class RapidApiAuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("x-rapidapi-key", apiKey)
            .addHeader("x-rapidapi-host", "aerodatabox.p.rapidapi.com")
            .build()
        return chain.proceed(request)
    }
}

class RateLimitRetryInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 429) {
            response.close()
            return chain.proceed(request)
        }

        return response
    }
}

object NetworkClient {
    private const val BASE_URL = "https://aerodatabox.p.rapidapi.com/"

    fun createFlightApiService(
        apiKey: String,
        isDebug: Boolean = true
    ): FlightApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(RapidApiAuthInterceptor(apiKey))
            .addInterceptor(RateLimitRetryInterceptor()) // Auto-retry on 429
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory(contentType))
            .build()
            .create(FlightApiService::class.java)
    }
}

object PhotoClient {
    val service: AircraftPhotoService by lazy {
        val contentType = "application/json".toMediaType()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(PlanespottersUserAgentInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.planespotters.net/pub/")
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory(contentType))
            .build()
            .create(AircraftPhotoService::class.java)
    }
}