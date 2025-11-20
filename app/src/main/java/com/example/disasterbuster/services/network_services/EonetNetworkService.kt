package com.example.disasterbuster.services.network_services

import com.example.disasterbuster.model.EonetResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface EonetApi {
    @GET("events")
    suspend fun getEvents(): EonetResponse
}

class EonetNetworkService {

    private val api: EonetApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://eonet.gsfc.nasa.gov/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(EonetApi::class.java)
    }

    suspend fun fetchEvents(): EonetResponse {
        return api.getEvents()
    }
}
