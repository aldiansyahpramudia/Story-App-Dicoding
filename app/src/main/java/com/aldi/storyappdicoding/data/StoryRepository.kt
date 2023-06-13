package com.aldi.storyappdicoding.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.aldi.storyappdicoding.api.ApiService
import com.aldi.storyappdicoding.data.model.login.LoginResponse
import com.aldi.storyappdicoding.data.model.signup.SignUpResponse
import com.aldi.storyappdicoding.data.model.stories.PostStoryResponse
import com.aldi.storyappdicoding.data.model.stories.StoriesResponse
import com.aldi.storyappdicoding.data.model.stories.Story
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val apiService: ApiService) {
    fun getStories(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    fun postStory(
        file: MultipartBody.Part,
        description: RequestBody,
    ): LiveData<Result<PostStoryResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.postStory(file, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("CreateStoryViewModel", "postStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postSignUp(
        name: String,
        email: String,
        password: String,
    ): LiveData<Result<SignUpResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.postSignUp(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("SignUpViewModel", "postSignUp: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.postLogin(email, password)
                emit(Result.Success(response))
            } catch (e: Exception) {
                Log.e("LoginViewModel", "postLogin: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getStoriesWithLocation(): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation(1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("ListStoryViewModel", "getStoriesWithLocation: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }
}