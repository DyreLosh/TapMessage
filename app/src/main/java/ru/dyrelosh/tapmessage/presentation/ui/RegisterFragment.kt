package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.firebaseAuth
import ru.dyrelosh.tapmessage.databinding.FragmentRegisterBinding
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.firebaseUser

class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var userEmail: String
    lateinit var userPassword: String
    lateinit var createAccountInputsArray: Array<EditText>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.signInWithPasswordButton.setOnClickListener {
            signIn()
        }
        etEmail = binding.emailTextEditLogin
        etPassword = binding.passwordTextEditLogin
        etConfirmPassword = binding.confirmPasswordInputText


        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }

    private fun notEmpty(): Boolean = etEmail.text.toString().trim().isNotEmpty() &&
            etPassword.text.toString().trim().isNotEmpty() &&
            etConfirmPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        } else {
            Toast.makeText(requireContext(), "password not matching", Toast.LENGTH_SHORT).show()
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = etEmail.text.toString().trim()
            userPassword = etPassword.text.toString().trim()

            /*create a user*/
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "created account successfully !", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_fillProfileFragment)
                        sendEmailVerification()

                    } else {
                        Toast.makeText(requireContext(), "failed to Authenticate !", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    /* send verification email to the new user. This will only
    *  work if the firebase user is not null.
    */

    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "email sent to $userEmail", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}