package com.intoverflown.usersauthkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.intoverflown.usersauthkt.users.LoginActivity
import com.intoverflown.usersauthkt.users.ProfileActivity

class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
    }

    // Create the menu on the screen
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // handle click events
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.user_profile -> {
                intentToProfile()
                true
            }
            R.id.logout -> {
                logOutCurrentUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun intentToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun logOutCurrentUser() {
        mAuth = FirebaseAuth.getInstance()
        mAuth!!.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}