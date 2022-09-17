package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentFillProfileBinding
import ru.dyrelosh.tapmessage.utils.*
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.UID
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.databaseRef

class FillProfileFragment : Fragment() {

    lateinit var binding: FragmentFillProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFillProfileBinding.inflate(inflater, container, false)
        val dateMap = mutableMapOf<String, Any>()
        val userName = binding.fullNameFillEditText.text.toString()
        val email = FirebaseUtils.firebaseAuth.currentUser?.email.toString()
        val uid = FirebaseUtils.firebaseAuth.currentUser?.uid.toString()
        val fullName = binding.fullNameFillEditText.text.toString()
        val phone = binding.phoneFillEditText.text.toString()

        binding.saveFillProfileButton.setOnClickListener {

            databaseRef.child(NODE_USERS).child(uid).child(CHILD_FULLNAME)
                .setValue(binding.fullNameFillEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "ok", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(requireContext(), "bad", Toast.LENGTH_SHORT).show()
                    }
                }
            databaseRef.child(NODE_USERS).child(uid).child(CHILD_PHONE)
                .setValue(binding.phoneFillEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "ok", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(requireContext(), "bad", Toast.LENGTH_SHORT).show()
                    }
                }
            databaseRef.child(NODE_USERS).child(uid).child(CHILD_USERNAME)
                .setValue(binding.nicknameFillEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "ok", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(requireContext(), "bad", Toast.LENGTH_SHORT).show()
                    }
                }
            val dateMap = mutableMapOf<String, Any>()
            val uid = FirebaseUtils.firebaseAuth.currentUser?.uid.toString()
            val email = FirebaseUtils.firebaseAuth.currentUser?.email.toString()
            dateMap[CHILD_ID] = uid
            dateMap[CHILD_EMAIL] = email

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
        }


        return binding.root
    }


}