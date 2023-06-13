package com.aldi.storyappdicoding.ui.auth.login

import androidx.lifecycle.ViewModel
import com.aldi.storyappdicoding.data.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) =
        storyRepository.postLogin(email, password)
}