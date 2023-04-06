package com.dinia.message

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class App : Application() {
    private lateinit var retrofit: Retrofit
    private lateinit var api: Api

    override fun onCreate() {
        super.onCreate()
        instance = this
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        retrofit = Retrofit.Builder()
            .baseUrl("http://213.189.221.170:8008")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        api = retrofit.create(Api::class.java)
    }

    fun getApi(): Api {
        return api
    }

    companion object {
        const val BASE_URL = "http://213.189.221.170:8008"
        const val NAME = "Dinia"
        lateinit var instance: App
            private set
    }
}
