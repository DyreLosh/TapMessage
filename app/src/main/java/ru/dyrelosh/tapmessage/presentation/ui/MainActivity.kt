package ru.dyrelosh.tapmessage.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import ru.dyrelosh.tapmessage.R
import ru.dyrelosh.tapmessage.databinding.ActivityMainBinding
import ru.dyrelosh.tapmessage.models.User
import ru.dyrelosh.tapmessage.utils.*
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.UID
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.USER
import ru.dyrelosh.tapmessage.utils.FirebaseUtils.databaseRef

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP_ACTIVITY = this

        initUser()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as
                    NavHostFragment
        val navController = navHostFragment.navController
        val bottomBar = binding.bottomNavigationView
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_menu)
        val menu = popupMenu.menu
        binding.bottomNavigationView.setupWithNavController(menu, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.chatFragment
                || destination.id == R.id.profileFragment
                || destination.id == R.id.settingsFragment
                || destination.id == R.id.contactsFragment
            ) {
                bottomBar.visibility = View.VISIBLE
            } else {
                bottomBar.visibility = View.GONE
            }
        }

        CoroutineScope(IO).launch {
            initContacts()
        }


    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE, this)
    }

    private fun initUser() {
        databaseRef.child(NODE_USERS).child(UID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    USER = snapshot.getValue(User::class.java) ?: User()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}