package ru.dyrelosh.tapmessage.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dyrelosh.tapmessage.databinding.ItemContactBinding
import ru.dyrelosh.tapmessage.models.Common

class ContactsAdapter : RecyclerView.Adapter<ContactsViewHolder>() {

    val items = mutableListOf<Common>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun submitList(common: List<Common>) {
        items.clear()
        items.addAll(common)
        notifyDataSetChanged()
    }
}