package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import ru.dyrelosh.tapmessage.utils.PreferenceManager
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.utils.Validator
import ru.dyrelosh.tapmessage.databinding.FragmentLoginBinding
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.firebaseAuth

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.signInWithPasswordButton.setOnClickListener {
            validateLogin()
        }
        return binding.root
    }

    private fun validateLogin() {
        val emailInputLayout = binding.emailTextInputLogin
        val passwordInputLayout = binding.passwordTextInputLogin
        val validator = Validator(requireContext())
        emailInputLayout.error = validator.validateEmail(binding.emailTextEditLogin)
        passwordInputLayout.error = validator.validatePassword(binding.passwordTextEditLogin)

        if (emailInputLayout.error == null && passwordInputLayout.error == null
        ) {
            signInUser()
        }
    }

    private fun signInUser() {
        firebaseAuth.signInWithEmailAndPassword(
            binding.emailTextEditLogin.text.toString(),
            binding.passwordTextEditLogin.text.toString()
        )
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    findNavController()
                        .navigate(R.id.action_loginFragment_to_chatFragment)
                    Toast.makeText(
                        requireContext(),
                        "signed in successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    PreferenceManager(requireContext()).writeUserId(signIn.result.user?.uid.toString())
                } else {
                    Toast.makeText(requireContext(), "sign in failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

}