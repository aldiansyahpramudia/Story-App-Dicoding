package com.aldi.storyappdicoding.ui.auth.signup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.aldi.storyappdicoding.R
import com.aldi.storyappdicoding.data.Result
import com.aldi.storyappdicoding.data.model.signup.SignUpResponse
import com.aldi.storyappdicoding.databinding.FragmentSignUpBinding
import com.aldi.storyappdicoding.utils.ViewModelFactory

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("View binding is null")

    private val signUpViewModel: SignUpViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSignupHaveAccount.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.loginFragment))

        binding.btSignUp.setOnClickListener { it ->
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            signUpViewModel.signUp(name, email, password).observe(requireActivity()) {
                if (it != null) {
                    when (it) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            processSignUp(it.data)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(context, it.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun processSignUp(data: SignUpResponse) {
        if (data.error) {
            Toast.makeText(requireContext(), getString(R.string.failed_register), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.success_register),
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigate(
                SignUpFragmentDirections.actionSignUpFragmentToLoginFragment(
                    isFromSignUp = true
                )
            )
        }
    }

    private fun showLoading(state: Boolean) {
        with(binding) {
            pbCreateSignup.isVisible = state
            edRegisterEmail.isInvisible = state
            edRegisterName.isInvisible = state
            edRegisterPassword.isInvisible = state
            tvRegisterDesc.isInvisible = state
            tvSignupHaveAccount.isInvisible = state
            btSignUp.isInvisible = state
            imgLogo.isInvisible = state
            tvRegisterTitle.isInvisible = state
            tvNameTitle.isInvisible = state
            tvEmailTitle.isInvisible = state
            tvPasswordTitle.isInvisible = state
            tvHaveAccount.isInvisible = state
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}