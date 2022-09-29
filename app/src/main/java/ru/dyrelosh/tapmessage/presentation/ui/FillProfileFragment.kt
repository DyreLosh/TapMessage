package ru.dyrelosh.tapmessage.presentation.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentFillProfileBinding
import ru.dyrelosh.tapmessage.utils.*
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.UID
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.databaseRef
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.storageRootRef

class FillProfileFragment : Fragment() {

    lateinit var binding: FragmentFillProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFillProfileBinding.inflate(inflater, container, false)

        binding.saveFillProfileButton.setOnClickListener {

            databaseRef.child(NODE_USERS).child(UID).child(CHILD_FULLNAME)
                .setValue(binding.fullNameFillEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "ok", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "bad", Toast.LENGTH_SHORT).show()
                    }
                }
            databaseRef.child(NODE_USERS).child(UID).child(CHILD_PHONE)
                .setValue(binding.phoneFillEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "ok", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "bad", Toast.LENGTH_SHORT).show()
                    }
                }
            databaseRef.child(NODE_USERS).child(UID).child(CHILD_USERNAME)
                .setValue(binding.nicknameFillEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "ok", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "bad", Toast.LENGTH_SHORT).show()
                    }
                }
            val dateMap = mutableMapOf<String, Any>()
            val uid = FirebaseUtils.firebaseAuth.currentUser?.uid.toString()
            val email = FirebaseUtils.firebaseAuth.currentUser?.email.toString()
            dateMap[CHILD_ID] = uid
            dateMap[CHILD_EMAIL] = email

            databaseRef.child(NODE_USERS).child(UID).updateChildren(dateMap)
                .addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        findNavController()
                            .navigate(R.id.action_fillProfileFragment_to_registerResultFragment)
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
        binding.addPhotoToFill.setOnClickListener {
            changePhotoUser()
        }
        return binding.root
    }



    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity as MainActivity)
    }


}