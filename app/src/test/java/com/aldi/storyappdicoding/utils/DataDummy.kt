package com.aldi.storyappdicoding.utils

import com.aldi.storyappdicoding.data.model.stories.StoriesResponse
import com.aldi.storyappdicoding.data.model.stories.Story

object DataDummy {
    fun generateDummyStoryList(): StoriesResponse {
        val listStory = ArrayList<Story>()
        for (i in 1..10) {
            val story = Story(
                createdAt = "2023-05-23T09:00:00Z",
                description = "Story $i",
                id = "$i",
                lat = i.toDouble() * 5,
                lon = i.toDouble() * 5,
                name = "Story $i",
                photoUrl = "https://cdn.pixabay.com/photo/2023/05/15/01/44/flower-7993995_960_720.jpg"
            )
            listStory.add(story)
        }

        return StoriesResponse(
            error = false,
            message = "Success",
            listStory = listStory
        )
    }
}