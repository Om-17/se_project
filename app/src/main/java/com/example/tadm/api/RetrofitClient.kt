package com.example.tadm.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.tadm.api.Config
object RetrofitClient {


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Config.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: NewEntryApiServiceInterface = retrofit.create(NewEntryApiServiceInterface::class.java)
}
