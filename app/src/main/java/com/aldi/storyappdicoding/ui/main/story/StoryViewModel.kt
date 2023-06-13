package com.aldi.storyappdicoding.ui.main.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aldi.storyappdicoding.data.StoryRepository
import com.aldi.storyappdicoding.data.model.stories.Story

class StoryViewModel(storyRepository: StoryRepository) : ViewModel() {
    val stories: LiveData<PagingData<Story>> = storyRepository.getStories().cachedIn(viewModelScope)
}