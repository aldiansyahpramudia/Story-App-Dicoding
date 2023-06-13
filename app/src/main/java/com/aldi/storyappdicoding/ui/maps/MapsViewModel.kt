package com.aldi.storyappdicoding.ui.maps

import androidx.lifecycle.ViewModel
import com.aldi.storyappdicoding.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}