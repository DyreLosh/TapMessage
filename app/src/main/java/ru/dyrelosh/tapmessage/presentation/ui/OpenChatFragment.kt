package ru.dyrelosh.tapmessage.presentation.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentOpenChatBinding
import ru.dyrelosh.tapmessage.models.Common
import ru.dyrelosh.tapmessage.models.User
import ru.dyrelosh.tapmessage.presentation.adapter.ChatAdapter
import ru.dyrelosh.tapmessage.utils.*

class OpenChatFragment : Fragment() {

    lateinit var binding: FragmentOpenChatBinding
    private val bundleArgs: OpenChatFragmentArgs by navArgs()
    private lateinit var mListenerInfoToolbar: ValueEventListener
    private lateinit var mReceivingUser: User
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: ChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: ValueEventListener
    private var mListMessages = emptyList<Common>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpenChatBinding.inflate(inflater, container, false)

        binding.openChatBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.attachMessageButton.setOnClickListener {
            changePhotoUser()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
       // initFields()
        initToolbar()
        initRecyclerView()
    }

    private fun initFields() {
        binding.sendMessageEditText.addTextChangedListener(AppTextWatcher {
            val string = binding.sendMessageEditText.text.toString()
            if (string.isEmpty()) {
                binding.sendMessageEditText.visibility = View.GONE
                binding.voiceAndAttachLayout.visibility = View.VISIBLE
            } else {
                binding.sendMessageEditText.visibility = View.VISIBLE
                binding.voiceAndAttachLayout.visibility = View.GONE
            }
        })
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.openChatRecyclerView
        mAdapter = ChatAdapter()
        mRefMessages = FirebaseUtils.databaseRef
            .child(NODE_MESSAGES)
            .child(FirebaseUtils.userUid)
            .child(bundleArgs.common.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mListMessages = snapshot.children.map { it.getCommonModel() }
                mAdapter.submitList(mListMessages)
                mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        mRefMessages.addValueEventListener(mMessagesListener)

    }

    private fun initToolbar() {
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
        binding.sendMessageButton.setOnClickListener {
            val message = binding.sendMessageEditText.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "sd", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(message, bundleArgs.common.id, TEXT_TYPE) {
                    binding.sendMessageEditText.setText("")
                }
            }
        }
    }

    private fun sendMessage(
        message: String,
        receivingUserId: String,
        typeText: String,
        function: () -> Unit
    ) {
        val refDialogUser = "$NODE_MESSAGES/${FirebaseUtils.userUid}/$receivingUserId"
        val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/${FirebaseUtils.userUid}"
        val messageKey = FirebaseUtils.databaseRef.child(refDialogUser).push().key
        val mapMessage = hashMapOf<String, Any>()
        mapMessage[CHILD_FROM] = FirebaseUtils.userUid
        mapMessage[CHILD_TYPE] = typeText
        mapMessage[CHILD_TEXT] = message
        mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

        val mapDialog = hashMapOf<String, Any>()
        mapDialog["$refDialogUser/$messageKey"] = mapMessage
        mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

        FirebaseUtils.databaseRef.updateChildren(mapDialog).addOnSuccessListener {
            function()
        }
    }

    override fun onPause() {
        super.onPause()
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            val url = CropImage.getActivityResult(data).uri
            val path =
                FirebaseUtils.storageRootRef.child(FOLDER_PROFILE_IMAGE).child(FirebaseUtils.userUid)
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

                    }
                }
            }
        }
    }


    fun putUrlToDatabase(url: String, function: () -> Unit) {
        FirebaseUtils.databaseRef.child(NODE_USERS).child(FirebaseUtils.userUid)
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

}