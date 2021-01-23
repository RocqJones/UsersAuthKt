package com.intoverflown.usersauthkt.users

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.intoverflown.usersauthkt.MainActivity
import com.intoverflown.usersauthkt.R

class CreateAccountActivity : AppCompatActivity() {
    //UI elements
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null

    // Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountActivity"

    // Global variables
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        initializeReferences()
    }

    private fun initializeReferences() {
        // from layout
        etFirstName = findViewById<View>(R.id.et_first_name) as EditText
        etLastName = findViewById<View>(R.id.et_last_name) as EditText
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnCreateAccount = findViewById<View>(R.id.btn_register) as Button

        mProgressBar = ProgressDialog(this)

        // firebase
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

        // action onClick
        btnCreateAccount!!.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {
        // we will first get current String values present in EditText
        firstName = etFirstName?.text.toString()
        lastName = etLastName?.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        // Then, we will validate this text and show appropriate error message if any value is empty
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            // As Firebase connection and validation can take a little time, we will show the Progress bar with appropriate message
            mProgressBar!!.setMessage("Registering User...")
            mProgressBar!!.show()

            // using addOnCompleteListener, we get to know if the task was successful or not
            mAuth!!.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val userId = mAuth!!.currentUser!!.uid

                        //Verify Email
                        verifyEmail();

                        //update user profile information
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("firstName").setValue(firstName)
                        currentUserDb.child("lastName").setValue(lastName)

                        // we just start a new Activity once user is authenticated
                        updateUserInfoAndUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@CreateAccountActivity,
                                "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfoAndUI() {
        // "FLAG_ACTIVITY_CLEAR_TOP" - flag clears the CreateAccountActivity from stack so that if user press back from MainActivity,
        // he should not be taken back to CreateAccountActivity.
        val intent = Intent(this@CreateAccountActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun verifyEmail() {
        // As we have also made a call to verifyEmail method, we will add a new method in which we will verify user’s email address
        val mUser = mAuth!!.currentUser;
        mUser!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@CreateAccountActivity, "" +
                                "Verification email sent to " + mUser.getEmail(),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        Toast.makeText(this@CreateAccountActivity,
                                "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}