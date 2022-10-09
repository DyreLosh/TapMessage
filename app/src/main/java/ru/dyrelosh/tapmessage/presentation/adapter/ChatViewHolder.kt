package ru.dyrelosh.tapmessage.presentation.adapter

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.dyrelosh.tapmessage.databinding.ItemMessageBinding

class ChatViewHolder(private val binding: ItemMessageBinding): ViewHolder(binding.root) {

    val blocUserMessage = binding.blocUserMessage
    val chatUserMessage = binding.chatUserMessage
    val chatUserMessageTime = binding.chatUserMessageTime

    val blocReceiverUserMessage = binding.blocReceivedMessage
    val chatReceiverUserMessage = binding.chatReceivedMessage
    val chatReceiverUserMessageTime = binding.chatReceivedMessageTime
}