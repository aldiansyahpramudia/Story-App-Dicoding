package com.aldi.storyappdicoding.ui.main.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.aldi.storyappdicoding.data.StoryRepository
import com.aldi.storyappdicoding.data.model.stories.Story
import com.aldi.storyappdicoding.utils.DataDummy
import com.aldi.storyappdicoding.utils.MainDispatcherRule
import com.aldi.storyappdicoding.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private val dummyStoriesResponse = DataDummy.generateDummyStoryList()

    @Test
    fun `when successfully load stories data`() = runTest {
        val stories: PagingData<Story> = StoryPagingSource.snapshot(dummyStoriesResponse.listStory)
        val expectedStoriesLiveData = MutableLiveData<PagingData<Story>>()
        expectedStoriesLiveData.value = stories
        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStoriesLiveData)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStoriesLiveData: PagingData<Story> = storyViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStoriesLiveData)

        // Memastikan data tidak null.
        Assert.assertNotNull(differ.snapshot())

        // Memastikan jumlah data sesuai dengan yang diharapkan.
        Assert.assertEquals(dummyStoriesResponse.listStory.size, differ.snapshot().size)

        // Memastikan data pertama yang dikembalikan sesuai.
        Assert.assertEquals(
            dummyStoriesResponse.listStory[0].id,
            differ.snapshot().firstOrNull()?.id
        )
    }

    @Test
    fun `when there is no stories data`() = runTest {
        val emptyStoriesLiveData = MutableLiveData<PagingData<Story>>()
        emptyStoriesLiveData.value = PagingData.empty()
        Mockito.`when`(storyRepository.getStories()).thenReturn(emptyStoriesLiveData)

        val storyViewModel = StoryViewModel(storyRepository)
        val actualStoriesLiveData: PagingData<Story> = storyViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStoriesLiveData)

        // Memastikan jumlah data yang dikembalikan nol.
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(
        state: PagingState<Int, LiveData<List<Story>>>,
    ): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
