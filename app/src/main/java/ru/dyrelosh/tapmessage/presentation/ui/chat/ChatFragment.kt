package ru.dyrelosh.tapmessage.presentation.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentChatBinding
import ru.dyrelosh.tapmessage.models.Common
import ru.dyrelosh.tapmessage.presentation.adapter.MainListAdapter
import ru.dyrelosh.tapmessage.utils.*

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainList =
        FirebaseUtils.databaseRef.child(NODE_MAIN_LIST).child(FirebaseUtils.userUid)
    private val mRefUser = FirebaseUtils.databaseRef.child(NODE_USERS)
    private val mRefMessages =
        FirebaseUtils.databaseRef.child(NODE_MESSAGES).child(FirebaseUtils.userUid)
    private var mListItems = listOf<Common>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.fabChat.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_openChatFragment)
        }
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.chatListRecyclerView
        mAdapter = MainListAdapter()
        mAdapter.onItemClick = { id ->


        }

        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
            mListItems = dataSnapshot.children.map { it.getCommonModel() }
            mListItems.forEach { model ->
                mRefUser.child(model.id).addListenerForSingleValueEvent(AppValueEventListener {
                    val newModel = it.getCommonModel()
                    mRefMessages.child(model.id).limitToLast(1)
                        .addListenerForSingleValueEvent(AppValueEventListener {
                            val tempList = it.children.map { it.getCommonModel() }
                            newModel.lastMessage = tempList[0].text
                            newModel.timeStamp = tempList[0].timeStamp
                            if(newModel.fullName.isEmpty()) {
                                newModel.fullName = newModel.phone
                            }
                            mAdapter.updateListItems(newModel)
                        })

                })
            }
        })
        mRecyclerView.adapter = mAdapter
    }




}