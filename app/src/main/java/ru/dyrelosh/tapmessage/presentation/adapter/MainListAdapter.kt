package ru.dyrelosh.tapmessage.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.dyrelosh.tapmessage.databinding.ItemChatBinding
import ru.dyrelosh.tapmessage.models.Common
import ru.dyrelosh.tapmessage.utils.APP_ACTIVITY
import ru.dyrelosh.tapmessage.utils.asTime

class MainListAdapter() : RecyclerView.Adapter<MainListViewHolder>() {

    val listItems = mutableListOf<Common>()
    var onItemClick: (String) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListViewHolder {

        return MainListViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))

    }

    override fun onBindViewHolder(holder: MainListViewHolder, position: Int) {
        holder.itemName.text = listItems[position].fullName
        holder.itemTime.text = listItems[position].timeStamp.toString().asTime()
        holder.itemLastMessage.text = listItems[position].lastMessage
        Glide.with(APP_ACTIVITY)
            .load(listItems[position].photoUrl)
            .into(holder.itemImage)

        holder.itemView.setOnClickListener {
            onItemClick.invoke(Common().id)
        }


    }

    override fun getItemCount(): Int = listItems.size

    fun updateListItems(item: Common) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }

}