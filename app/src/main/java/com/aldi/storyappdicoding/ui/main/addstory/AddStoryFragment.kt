package com.aldi.storyappdicoding.ui.main.addstory

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.aldi.storyappdicoding.R
import com.aldi.storyappdicoding.data.Result
import com.aldi.storyappdicoding.databinding.FragmentAddStoryBinding
import com.aldi.storyappdicoding.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Suppress("DEPRECATION")
class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("View binding is null")

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btOpenCamera.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.cameraFragment))
        binding.btOpenGallery.setOnClickListener {
            startGallery()
        }
        val fileUri = arguments?.get("selected_image")
        binding.buttonAdd.setOnClickListener {
            if (fileUri != null) {
                uploadImageCameraX()
            } else {
                uploadImage()
            }
        }
        if (fileUri != null) {
            val uri: Uri = fileUri as Uri
            val isBackCamera = arguments?.get("isBackCamera") as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(uri.path),
                isBackCamera
            )
            getFile = uri.toFile()
            binding.ivImagePreview.setImageBitmap(result)
        }
    }

    private var getFile: File? = null
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding.ivImagePreview.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val descriptionText = binding.edAddDescription.text.toString()
            if (descriptionText.isNotEmpty()) {
                showLoading(true)
                val file = reduceFileImage(getFile!!)
                val description = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                addStoryViewModel.postStory(imageMultipart, description)
                    .observe(viewLifecycleOwner) {
                        when (it) {
                            is Result.Success -> {
                                showLoading(false)
                                Toast.makeText(context, it.data.message, Toast.LENGTH_LONG).show()
                                findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToStoryFragment())
                            }
                            is Result.Loading -> {
                                // Do nothing or handle loading state if needed
                            }
                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.warning_description),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.warning_image),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadImageCameraX() {
        if (getFile != null) {
            val descriptionText = binding.edAddDescription.text.toString()
            if (descriptionText.isNotEmpty()) {
                showLoading(true)
                val isBackCamera = arguments?.get("isBackCamera") as Boolean
                val file = reduceFileImageCameraX(getFile!!, isBackCamera)
                val description = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                addStoryViewModel.postStory(imageMultipart, description)
                    .observe(viewLifecycleOwner) {
                        when (it) {
                            is Result.Success -> {
                                showLoading(false)
                                Toast.makeText(context, it.data.message, Toast.LENGTH_LONG).show()
                                findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToStoryFragment())
                            }
                            is Result.Loading -> {
                                // Do nothing or handle loading state if needed
                            }
                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.warning_description),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.warning_image),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(state: Boolean) {
        with(binding) {
            pbCreateStory.isVisible = state
            edAddDescription.isInvisible = state
            ivImagePreview.isInvisible = state
            btOpenCamera.isInvisible = state
            btOpenGallery.isInvisible = state
            buttonAdd.isInvisible = state
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}