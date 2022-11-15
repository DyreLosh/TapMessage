package ru.dyrelosh.tapmessage.presentation.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.dyrelosh.tapmessage.utils.PreferenceManager
import ru.dyrelosh.tapmessage.databinding.FragmentSettingsBinding
import ru.dyrelosh.tapmessage.utils.AppStates
import ru.dyrelosh.tapmessage.utils.FirebaseUtils

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val preferenceManager = PreferenceManager(requireContext())

        binding.exitButton.setOnClickListener {
            AppStates.updateState(AppStates.OFFLINE, requireContext())
            PreferenceManager(requireContext()).deleteUserId()
            FirebaseUtils.firebaseAuth.signOut()

        }

        return binding.root
    }

}