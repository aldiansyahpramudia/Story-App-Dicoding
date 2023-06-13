package com.aldi.storyappdicoding.di

import android.content.Context
import com.aldi.storyappdicoding.api.ApiConfig
import com.aldi.storyappdicoding.data.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService(context)
        return StoryRepository(apiService)
    }
}