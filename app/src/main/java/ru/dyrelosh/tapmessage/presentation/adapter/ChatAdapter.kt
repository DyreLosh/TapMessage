package ru.dyrelosh.tapmessage.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dyrelosh.tapmessage.databinding.ItemMessageBinding
import ru.dyrelosh.tapmessage.models.Common
import ru.dyrelosh.tapmessage.utils.FirebaseUtils
import ru.dyrelosh.tapmessage.utils.asTime
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : RecyclerView.Adapter<ChatViewHolder>() {

    private var listMessagesCash = mutableListOf<Common>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ItemMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        if (listMessagesCash[position].from == FirebaseUtils.userUid) {
            holder.blocUserMessage.visibility = View.VISIBLE
            holder.blocReceiverUserMessage.visibility = View.GONE
            holder.chatUserMessage.text = listMessagesCash[position].text
            holder.chatUserMessageTime.text =
                listMessagesCash[position].timeStamp.toString().asTime()
        } else {
            holder.blocReceiverUserMessage.visibility = View.VISIBLE
            holder.blocUserMessage.visibility = View.GONE
            holder.chatReceiverUserMessage.text = listMessagesCash[position].text
            holder.chatReceiverUserMessageTime.text =
                listMessagesCash[position].timeStamp.toString().asTime()
        }
    }

    override fun getItemCount(): Int = listMessagesCash.size

    fun submitList(list: List<Common>) {
        listMessagesCash.clear()
        listMessagesCash.addAll(list)
        notifyDataSetChanged()
    }
}


