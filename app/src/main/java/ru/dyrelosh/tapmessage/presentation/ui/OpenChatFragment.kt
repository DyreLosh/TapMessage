package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentOpenChatBinding
import ru.dyrelosh.tapmessage.models.User
import ru.dyrelosh.tapmessage.utils.FirebaseUtils
import ru.dyrelosh.tapmessage.utils.NODE_USERS
import ru.dyrelosh.tapmessage.utils.getUserModel

class OpenChatFragment : Fragment() {

    lateinit var binding: FragmentOpenChatBinding
    private val bundleArgs: OpenChatFragmentArgs by navArgs()
    private lateinit var mListenerInfoToolbar: ValueEventListener
    private lateinit var mReceivingUser: User
    private lateinit var mRefUser: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpenChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mListenerInfoToolbar = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mReceivingUser = snapshot.getUserModel()
                initInfoToolbar()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        mRefUser = FirebaseUtils.databaseRef.child(NODE_USERS).child(bundleArgs.common.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
    }

    override fun onPause() {
        super.onPause()
        mRefUser.removeEventListener(mListenerInfoToolbar)
    }

    private fun initInfoToolbar() {
        Glide.with(binding.root)
            .load(mReceivingUser.photoUrl)
            .into(binding.openChatImage)
        binding.openChatName.text = mReceivingUser.fullName
        binding.openChatState.text = mReceivingUser.state
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        bundleArgs.common.let { arg ->
//            arg.photoUrl.let {
//                Glide.with(this)
//                    .load(arg.photoUrl)
//                    .load(binding.openChatImage)
//            }
//            binding.openChatName.text = arg.fullName
//            binding.openChatState.text = arg.state
//        }
//    }


}