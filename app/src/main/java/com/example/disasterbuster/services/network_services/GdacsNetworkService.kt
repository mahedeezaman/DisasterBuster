package com.example.disasterbuster.services.network_services

import com.example.disasterbuster.model.DisasterResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object GdacsNetworkService {

    private const val BASE_URL = "https://www.gdacs.org/"

    // Retrofit API interface
    private interface GdacsApi {
        @GET("gdacsapi/api/events/geteventlist/events4app")
        suspend fun getDisasters(): DisasterResponse
    }

    private val api: GdacsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GdacsApi::class.java)
    }

    // Public suspend function to fetch disasters
    suspend fun fetchDisasters(): DisasterResponse {
        return api.getDisasters()
    }
}
