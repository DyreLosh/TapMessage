package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentChatBinding
import ru.dyrelosh.tapmessage.utils.FirebaseUtils

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.emailveri.setOnClickListener {
            FirebaseUtils.firebaseAuth.signOut()
            findNavController().popBackStack()
        }
        return binding.root
    }

}