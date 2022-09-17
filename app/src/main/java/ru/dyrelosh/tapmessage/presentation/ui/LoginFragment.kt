package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentLoginBinding
import ru.dyrelosh.tapmessage.utils.*
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.databaseRef
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.firebaseAuth

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        signInInputsArray = arrayOf(binding.emailTextEditLogin, binding.passwordTextEditLogin)
        binding.signInWithPasswordButton.setOnClickListener {
            signInUser()
        }
        return binding.root
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signInUser() {
        signInEmail = binding.emailTextEditLogin.text.toString().trim()
        signInPassword = binding.passwordTextEditLogin.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {

                        val dateMap = mutableMapOf<String, Any>()
                        val uid = firebaseAuth.currentUser?.uid.toString()
                        val email = firebaseAuth.currentUser?.email.toString()
                        dateMap[CHILD_ID] = uid
                        dateMap[CHILD_EMAIL] = email
                        dateMap[CHILD_USERNAME] = uid

                        databaseRef.child(NODE_USERS).child(uid).updateChildren(dateMap)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    findNavController()
                                        .navigate(R.id.action_loginFragment_to_chatFragment)
                                    Toast.makeText(
                                        requireContext(),
                                        "signed in successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        task2.exception?.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(requireContext(), "sign in failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }

}