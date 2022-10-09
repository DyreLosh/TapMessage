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
import android.widget.AbsListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
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
    private lateinit var mMessagesListener: ChildEventListener
    private var mCountMessages = 15
    private var nIsScrolling = false
    private var nSmoothScrollToPosition = true
    private lateinit var mSwipeRefresh: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager

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
        mSwipeRefresh = binding.chatSwipeRefresh
        mLayoutManager = LinearLayoutManager(this.context)
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
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager
        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()
            if (nSmoothScrollToPosition) {
                mAdapter.addItemToBottom(message) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }

            } else {
                mAdapter.addItemToTop(message) {
                    mSwipeRefresh.isRefreshing = false
                }
            }

        }
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (nIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    nIsScrolling = true
                }
            }
        })
        mSwipeRefresh.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        nSmoothScrollToPosition = false
        nIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)

    }

    private fun initToolbar() {
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }
        mRefUser = FirebaseUtils.databaseRef.child(NODE_USERS).child(bundleArgs.common.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        binding.sendMessageButton.setOnClickListener {
            nSmoothScrollToPosition = true
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
        mapMessage[CHILD_ID] = messageKey.toString()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            val url = CropImage.getActivityResult(data).uri
            val messageKey =
                FirebaseUtils.databaseRef.child(NODE_MESSAGES).child(FirebaseUtils.userUid)
                    .child(bundleArgs.common.id).push().key.toString()
            val path =
                FirebaseUtils.storageRootRef.child(FOLDER_MESSAGES_IMAGES)
                    .child(messageKey)
            putImageToStorage(url, path) {
                getUrlFromStorage(path) {
                    sendMessageAsImage(bundleArgs.common.id, it, messageKey)
                    nSmoothScrollToPosition = true
                }
            }
        }
    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .start(activity as MainActivity, this)
    }

}