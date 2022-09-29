package ru.dyrelosh.tapmessage.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
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
            if (destination.id == R.id.chatFragment || destination.id == R.id.profileFragment || destination.id == R.id.settingsFragment) {
                bottomBar.visibility = View.VISIBLE
            } else {
                bottomBar.visibility = View.GONE
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val url = CropImage.getActivityResult(data).uri
            val path = FirebaseUtils.storageRootRef.child(FOLDER_PROFILE_IMAGE).child(UID)
            path.putFile(url).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    path.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val photoUrl = it.result.toString()
                            databaseRef.child(NODE_USERS).child(UID).child(CHILD_PHOTO_URL)
                                .setValue(photoUrl).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Фотография успешно загружено",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        USER.photoUrl = photoUrl
                                    }
                                }
                        }
                    }
                } else {
                    Toast.makeText(
                        this, task.exception?.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }


//    override fun onStart() {
//        super.onStart()
//        AppStates.updateState(AppStates.ONLINE)
//    }
//
//
//    override fun onStop() {
//        super.onStop()
//        AppStates.updateState(AppStates.OFFLINE)
//    }

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