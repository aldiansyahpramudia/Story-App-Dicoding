package com.aldi.storyappdicoding.ui.main.story

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aldi.storyappdicoding.R
import com.aldi.storyappdicoding.databinding.FragmentStoryBinding
import com.aldi.storyappdicoding.utils.ViewModelFactory

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("View binding is null")

    private lateinit var adapter: StoriesAdapter
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        storyViewModel.stories.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                adapter.submitData(viewLifecycleOwner.lifecycle, data)
            }
        }

        setupAdapter()
        binding.fabCreateStory.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.addStoryFragment))
        onBackPressed()
    }

    private fun setupAdapter() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvStories.layoutManager = layoutManager

        adapter = StoriesAdapter { story, imageView, nameView, descView ->
            val action = StoryFragmentDirections.actionStoryFragmentToDetailStoryFragment(
                id = story.id,
                name = story.name,
                description = story.description,
                photoUrl = story.photoUrl
            )

            findNavController()
                .navigate(
                    action,
                    FragmentNavigator.Extras.Builder()
                        .addSharedElements(
                            mapOf(
                                imageView to imageView.transitionName,
                                nameView to nameView.transitionName,
                                descView to descView.transitionName,
                            )
                        ).build()
                )
        }

        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        binding.rvStories.viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}