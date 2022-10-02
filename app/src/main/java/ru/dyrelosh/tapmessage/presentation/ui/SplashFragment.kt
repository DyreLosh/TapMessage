package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import ru.dyrelosh.tapmessage.utils.PreferenceManager
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentSplashBinding
import ru.dyrelosh.tapmessage.utils.FirebaseUtils

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        Toast.makeText(
            requireContext(),
            PreferenceManager(requireContext()).readUserId(),
            Toast.LENGTH_SHORT
        ).show()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).postDelayed({
            val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
            if (user != null) {
                user?.let {
                    Toast.makeText(requireContext(), user.email.toString(), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_splashFragment_to_chatFragment)
                }
            }
            else {
                findNavController().navigate(R.id.action_splashFragment_to_loginMethodFragment)
            }

        }, 3000)

    }
}