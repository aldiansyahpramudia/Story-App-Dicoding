package com.aldi.storyappdicoding.api

import android.content.Context
import com.aldi.storyappdicoding.utils.Preference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private fun getInterceptor(token: String?): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        return if (token.isNullOrEmpty()) {
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(token))
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }

    fun getApiService(context: Context): ApiService {

        val sharedPref = Preference.initPref(context, "onSignIn")
        val token = sharedPref.getString("token", null).toString()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getInterceptor(token))
            .build()
        return retrofit.create(ApiService::class.java)
    }
}