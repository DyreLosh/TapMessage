package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentProfileBinding
import ru.dyrelosh.tapmessage.models.User
import ru.dyrelosh.tapmessage.utils.FirebaseUtils
import ru.dyrelosh.tapmessage.utils.NODE_USERS

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        FirebaseUtils.databaseRef.child(NODE_USERS).child(FirebaseUtils.userUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileInfo = snapshot.getValue(User::class.java) ?: User()
                    binding.profileFullNameText.text = profileInfo.fullName
                    Glide.with(binding.root)
                        .load(profileInfo.photoUrl)
                        .into(binding.profileImage)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        return binding.root
    }

}