package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentChatBinding
import ru.dyrelosh.tapmessage.utils.AppStates
import ru.dyrelosh.tapmessage.utils.FirebaseUtils

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.emailveri.setOnClickListener {
            AppStates.updateState(AppStates.OFFLINE)
            FirebaseUtils.firebaseAuth.signOut()
            findNavController().popBackStack()

        }

        binding.fabChat.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_openChatFragment)
        }
        return binding.root
    }



}