package ru.dyrelosh.tapmessage.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.dyrelosh.tapmessage.databinding.ItemContactBinding
import ru.dyrelosh.tapmessage.models.Common

class ContactsViewHolder(private val binding: ItemContactBinding) :
    RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Common) = with(binding) {
            contactItemName.text = contact.fullName
            contactItemState.text = contact.state
            Glide.with(binding.root)
                .load(contact.photoUrl)
                .into(contactItemPhoto)
        }
}