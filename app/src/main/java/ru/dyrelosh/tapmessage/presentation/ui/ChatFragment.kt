package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentChatBinding
import ru.dyrelosh.tapmessage.models.User
import ru.dyrelosh.tapmessage.utils.AppStates
import ru.dyrelosh.tapmessage.utils.CHILD_FULLNAME
import ru.dyrelosh.tapmessage.utils.FirebaseUtils
import ru.dyrelosh.tapmessage.utils.NODE_USERS

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.materialToolbar4.title = User().username.toString()
        binding.fabChat.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_openChatFragment)
        }
        return binding.root
    }


}