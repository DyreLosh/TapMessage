package ru.dyrelosh.tapmessage.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ru.dyrelosh.tapmessage.PreferenceManager
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.FragmentSettingsBinding
import ru.dyrelosh.tapmessage.utils.AppStates
import ru.dyrelosh.tapmessage.utils.CHILD_FULLNAME
import ru.dyrelosh.tapmessage.utils.FirebaseUtils
import ru.dyrelosh.tapmessage.utils.NODE_USERS

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val preferenceManager = PreferenceManager(requireContext())

        binding.exitText.setOnClickListener {
            AppStates.updateState(AppStates.OFFLINE, requireContext())
            PreferenceManager(requireContext()).deleteUserId()
            FirebaseUtils.firebaseAuth.signOut()

        }

        FirebaseUtils.databaseRef.child(NODE_USERS).child(preferenceManager.readUserId())
            .child(CHILD_FULLNAME)
            .setValue("ok").addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "ok", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "bad", Toast.LENGTH_SHORT).show()
                }
            }
        return binding.root
    }

}