package ru.dyrelosh.tapmessage.presentation.ui.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentContactsBinding
import ru.dyrelosh.tapmessage.databinding.ItemContactBinding
import ru.dyrelosh.tapmessage.models.Common
import ru.dyrelosh.tapmessage.utils.*

class ContactsFragment : Fragment() {

    lateinit var binding: FragmentContactsBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<Common, ContactHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUserListener: ValueEventListener
    private var mapListener = hashMapOf<DatabaseReference, ValueEventListener>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactsBinding.inflate(inflater, container, false)

        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.contactsRecycler
        mRefContacts =
            FirebaseUtils.databaseRef.child(NODE_PHONES_CONTACTS).child(FirebaseUtils.userUid)

        val option = FirebaseRecyclerOptions.Builder<Common>()
            .setQuery(mRefContacts, Common::class.java)
            .build()

        mAdapter = object : FirebaseRecyclerAdapter<Common, ContactHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
                return ContactHolder(
                    ItemContactBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            override fun onBindViewHolder(holder: ContactHolder, position: Int, model: Common) {
                mRefUsers = FirebaseUtils.databaseRef.child(NODE_USERS).child(model.id)
                mRefUserListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val contact = snapshot.getCommonModel()
                        if (contact.fullName.isEmpty()) {
                            holder.name.text = model.fullName
                        }
                        else {
                            holder.name.text = contact.fullName

                        }
                        holder.status.text = contact.state
                        Glide.with(binding.root)
                            .load(contact.photoUrl)
                            .into(holder.photo)
                        val bundle = bundleOf("common" to contact)
                        holder.itemView.setOnClickListener {
                            findNavController().navigate(
                                R.id.action_contactsFragment_to_openChatFragment,
                                bundle
                            )
                        }
                    }


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
                mRefUsers.addValueEventListener(mRefUserListener)
                mapListener[mRefUsers] = mRefUserListener
            }

        }
        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }


    class ContactHolder(binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.contactItemName
        val status: TextView = binding.contactItemState
        val photo: CircleImageView = binding.contactItemPhoto

    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
        mapListener.forEach {
            it.key.removeEventListener(it.value)
        }
    }

}


