package ru.dyrelosh.tapmessage.presentation.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
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
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.getMainLooper()).postDelayed({
            val user: FirebaseUser? = FirebaseUtils.firebaseAuth.currentUser
            if (user != null) {
                user.let {
                    findNavController().navigate(R.id.action_splashFragment_to_chatFragment)
                }
            }
            else {
                findNavController().navigate(R.id.action_splashFragment_to_loginMethodFragment)
            }
        }, 3000)
    }
}