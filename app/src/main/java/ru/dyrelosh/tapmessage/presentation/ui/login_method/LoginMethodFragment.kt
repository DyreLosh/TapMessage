package ru.dyrelosh.tapmessage.presentation.ui.login_method

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentLoginMethodBinding

class LoginMethodFragment : Fragment() {

    lateinit var binding: FragmentLoginMethodBinding
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginMethodBinding.inflate(inflater, container, false)

        binding.signInWithGoogleInLoginMethod.setOnClickListener {
            findNavController().navigate(R.id.action_loginMethodFragment_to_registerFragment)
        }
        binding.signInWithPasswordMethodButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginMethodFragment_to_loginFragment)
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null ) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (a: ApiException) {
                Toast.makeText(requireContext(), a.message, Toast.LENGTH_SHORT).show()
            }
        }
        auth = Firebase.auth
        return binding.root
    }
    private fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val cridencial = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cridencial).addOnCompleteListener {
            if (it.isSuccessful) {
                findNavController().navigate(R.id.action_loginMethodFragment_to_loginFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "???????????? ?????????? ?????????? Google ??????????????",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}