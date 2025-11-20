package com.example.disasterbuster.services.network_services

import com.example.disasterbuster.model.DisasterResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class GdacsNetworkService {

    private val api: GdacsApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(GdacsApi::class.java)
    }

    // Retrofit API interface
    private interface GdacsApi {
        @GET("gdacsapi/api/events/geteventlist/events4app")
        suspend fun getDisasters(
            @Query("pagenumber") pageNumber: Int = 1,
            @Query("pagesize") pageSize: Int = 100
        ): DisasterResponse
    }

    suspend fun fetchDisasters(pageNumber: Int = 1, pageSize: Int = 100): DisasterResponse {
        return api.getDisasters(pageNumber, pageSize)
    }

    companion object {
        private const val BASE_URL = "https://www.gdacs.org/"
    }
}
