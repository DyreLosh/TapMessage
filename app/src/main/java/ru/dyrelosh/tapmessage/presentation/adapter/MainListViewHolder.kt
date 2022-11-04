package ru.dyrelosh.tapmessage.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.dyrelosh.tapmessage.databinding.ItemChatBinding

class MainListViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {


    val itemName = binding.chatListName
    val itemImage = binding.chatListImage
    val itemLastMessage = binding.chatListLastMessage
    val itemTime = binding.chatListTime


}
