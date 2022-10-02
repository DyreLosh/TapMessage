package ru.dyrelosh.tapmessage.presentation.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import ru.dyrelosh.tapmessage.utils.PreferenceManager
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.utils.Validator
import ru.dyrelosh.tapmessage.databinding.FragmentFillProfileBinding
import ru.dyrelosh.tapmessage.utils.*
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.databaseRef
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.firebaseAuth
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.storageRootRef

class FillProfileFragment : Fragment() {

    lateinit var binding: FragmentFillProfileBinding
    lateinit var preferenceManager: PreferenceManager
    lateinit var photoUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFillProfileBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())

        binding.saveFillProfileButton.setOnClickListener {
            validateUserInfo()
        }
        binding.addPhotoToFill.setOnClickListener {
            changePhotoUser()
        }
        return binding.root
    }

    private fun validateUserInfo() {
        val usernameInputLayout = binding.nicknameFillTextInput
        val fullNameInputLayout = binding.fullNameFillTextInput
        val phoneInputLayout = binding.phoneFillTextInput
        val validator = Validator(requireContext())

        usernameInputLayout.error = validator.validateUsername(binding.nicknameFillEditText)
        fullNameInputLayout.error = validator.validateFullName(binding.fullNameFillEditText)
        phoneInputLayout.error = validator.validatePhone(binding.phoneFillEditText)

        if (usernameInputLayout.error == null
            && fullNameInputLayout.error == null
            && phoneInputLayout.error == null
        ) {
            sendUserInformation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null
        ) {
            val url = CropImage.getActivityResult(data).uri
            val path =
                storageRootRef.child(FOLDER_PROFILE_IMAGE).child(preferenceManager.readUserId())
            putImageToStorage(url, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        Toast.makeText(
                            requireContext(),
                            "Фотография успешно загружено",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        FirebaseUtils.USER.photoUrl = it
                        Picasso.get()
                            .load(it)
                            .into(binding.imageFillProfile)
                    }
                }
            }
        }
    }


    fun putUrlToDatabase(url: String, function: () -> Unit) {
        FirebaseUtils.databaseRef.child(NODE_USERS).child(preferenceManager.readUserId())
            .child(CHILD_PHOTO_URL)
            .setValue(url)
            .addOnSuccessListener { function() }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun getUrlFromStorage(
        path: StorageReference,
        function: (url: String) -> Unit
    ) {
        path.downloadUrl.addOnSuccessListener { function(it.toString()) }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun putImageToStorage(
        url: Uri,
        path: StorageReference,
        function: () -> Unit
    ) {
        path.putFile(url).addOnSuccessListener { function() }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }

    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity as MainActivity, this)
    }

    private fun sendUserInformation() {

        val dateMap = mutableMapOf<String, Any>()
        dateMap[CHILD_ID] = preferenceManager.readUserId()
        dateMap[CHILD_EMAIL] = firebaseAuth.currentUser?.email.toString()
        dateMap[CHILD_PHONE] = binding.phoneFillEditText.text.toString()
        dateMap[CHILD_FULLNAME] = binding.fullNameFillEditText.text.toString()
        dateMap[CHILD_USERNAME] = binding.nicknameFillEditText.text.toString()
        dateMap[CHILD_PHOTO_URL] = FirebaseUtils.USER.photoUrl

        databaseRef.child(NODE_USERS).child(preferenceManager.readUserId()).setValue(dateMap)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_fillProfileFragment_to_registerResultFragment)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }

//        databaseRef.child(NODE_USERS).child(preferenceManager.readUserId()).child(CHILD_FULLNAME)
//            .setValue(binding.fullNameFillEditText.text.toString()).addOnFailureListener {
//                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
//            }
//
//        databaseRef.child(NODE_USERS).child(preferenceManager.readUserId()).child(CHILD_PHONE)
//            .setValue(binding.phoneFillEditText.text.toString()).addOnFailureListener {
//                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
//            }
//
//        databaseRef.child(NODE_USERS).child(preferenceManager.readUserId()).child(CHILD_USERNAME)
//            .setValue(binding.nicknameFillEditText.text.toString()).addOnFailureListener {
//                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
//            }
//        databaseRef.child(NODE_USERS).child(preferenceManager.readUserId()).child(CHILD_ID)
//            .setValue(preferenceManager.readUserId()).addOnFailureListener {
//                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
//            }
//        databaseRef.child(NODE_USERS).child(preferenceManager.readUserId()).child(CHILD_EMAIL)
//            .setValue(firebaseAuth.currentUser?.email.toString()).addOnFailureListener {
//                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
//            }.addOnCompleteListener {
//                findNavController().navigate(R.id.action_fillProfileFragment_to_registerResultFragment)
//            }

        databaseRef.child(NODE_PHONES).child(binding.phoneFillEditText.text.toString())
            .setValue(preferenceManager.readUserId())
    }
}