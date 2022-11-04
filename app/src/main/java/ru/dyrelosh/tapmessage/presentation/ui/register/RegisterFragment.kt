package ru.dyrelosh.tapmessage.presentation.ui.register

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
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.firebaseAuth
import ru.dyrelosh.tapmessage.databinding.FragmentRegisterBinding
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.firebaseUser

class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.signUpWithPasswordButton.setOnClickListener {
            validateRegister()
        }
        return binding.root
    }

    private fun validateRegister() {
        val emailInputLayout = binding.emailTextInputRegister
        val passwordInputLayout = binding.passwordTextInputRegister
        val confirmPasswordInputLayout = binding.confirmPasswordInputLayout
        val validator = Validator(requireContext())

        emailInputLayout.error = validator.validateEmail(binding.emailTextEditRegister)
        passwordInputLayout.error = validator.validatePassword(binding.passwordTextEditRegister)
        confirmPasswordInputLayout.error = validator.confirmPassword(
            binding.passwordTextEditRegister,
            binding.confirmPasswordInputText
        )

        if (emailInputLayout.error == null
            && passwordInputLayout.error == null
            && confirmPasswordInputLayout.error == null
        ) {
            signIn()
        }
    }

    private fun signIn() {
        firebaseAuth.createUserWithEmailAndPassword(
            binding.emailTextEditRegister.text.toString(),
            binding.passwordTextEditRegister.text.toString()
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                sendEmailVerification()
                PreferenceManager(requireContext()).writeUserId(it.result.user?.uid.toString())
                findNavController().navigate(R.id.action_registerFragment_to_fillProfileFragment)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Email sent to ${firebaseAuth.currentUser?.email.toString()}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }
}