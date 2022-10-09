package ru.dyrelosh.tapmessage.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.dyrelosh.tapmessage.databinding.ItemMessageBinding
import ru.dyrelosh.tapmessage.models.Common
import ru.dyrelosh.tapmessage.utils.*
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
        when(listMessagesCash[position].type) {
            TEXT_TYPE -> drawMessageText(holder, position)
            IMAGE_TYPE -> drawMessageImage(holder, position)
        }

    }

    private fun drawMessageImage(holder: ChatViewHolder, position: Int) {
        if (listMessagesCash[position].from == FirebaseUtils.userUid) {
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceiverUserMessage.visibility = View.GONE
            holder.blocReceiverImageMessage.visibility = View.GONE
            holder.blocImageMessage.visibility = View.VISIBLE

            holder.chatImageTimeMessage.text = listMessagesCash[position].timeStamp.toString().asTime()
            Glide.with(APP_ACTIVITY)
                .load(listMessagesCash[position].imageUrl)
                .into(holder.chatImageMessage)
        } else {
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceiverUserMessage.visibility = View.GONE
            holder.blocReceiverImageMessage.visibility = View.VISIBLE
            holder.blocImageMessage.visibility = View.GONE

            holder.chatReceiverUserMessageTime.text = listMessagesCash[position].timeStamp.toString().asTime()
            Glide.with(APP_ACTIVITY)
                .load(listMessagesCash[position].imageUrl)
                .into(holder.chatReceivedImageMessage)
        }
    }

    private fun drawMessageText(holder: ChatViewHolder, position: Int) {
        if (listMessagesCash[position].from == FirebaseUtils.userUid) {
            holder.blocUserMessage.visibility = View.VISIBLE
            holder.blocReceiverUserMessage.visibility = View.GONE
            holder.blocReceiverImageMessage.visibility = View.GONE
            holder.blocImageMessage.visibility = View.GONE

            holder.chatUserMessage.text = listMessagesCash[position].text
            holder.chatUserMessageTime.text =
                listMessagesCash[position].timeStamp.toString().asTime()
        } else {
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceiverUserMessage.visibility = View.VISIBLE
            holder.blocReceiverImageMessage.visibility = View.GONE
            holder.blocImageMessage.visibility = View.GONE

            holder.chatReceiverUserMessage.text = listMessagesCash[position].text
            holder.chatReceiverUserMessageTime.text =
                listMessagesCash[position].timeStamp.toString().asTime()
        }
    }

    override fun getItemCount(): Int = listMessagesCash.size

    fun addItemToBottom(item: Common, onSuccess: () -> Unit) {
        if (!listMessagesCash.contains(item)) {
            listMessagesCash.add(item)
            notifyItemInserted(listMessagesCash.size)
        }
        onSuccess()
    }
    fun addItemToTop(item: Common, onSuccess: () -> Unit) {
        if (!listMessagesCash.contains(item)) {
            listMessagesCash.add(item)
            listMessagesCash.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}


