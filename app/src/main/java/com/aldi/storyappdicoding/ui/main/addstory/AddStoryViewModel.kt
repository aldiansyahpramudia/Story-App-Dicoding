package com.aldi.storyappdicoding.ui.main.addstory

import androidx.lifecycle.ViewModel
import com.aldi.storyappdicoding.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun postStory(file: MultipartBody.Part, description: RequestBody) =
        storyRepository.postStory(file, description)
}