package com.example.fitletics.activities.appentry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fitletics.R
import com.example.fitletics.activities.main.MainActivity
import com.example.fitletics.models.support.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()

        /*===========TEMPORARY CODE FOR TESTING===========*/

        //changes the activity from by tapping the preview (for now)

        register_user_button.setOnClickListener()
        {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


        sign_in_button.setOnClickListener()
        {
            loginUser()
        }

        /*===============================================*/


    }

    private fun loginUser() {
        if (email_edit_text.text.toString().isEmpty()) {
            email_edit_text.error = "Please enter email!"
            email_edit_text.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email_edit_text.text.toString()).matches()){
            email_edit_text.error = "Please enter a valid email!"
            email_edit_text.requestFocus()
            return
        }

        if (password_edit_text.text.toString().isEmpty()) {
            password_edit_text.error = "Please enter a password!"
            password_edit_text.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(email_edit_text.text.toString(), password_edit_text.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FB_SIGNIN", "signInWithEmail:success")
                    Constants.CURRENT_FIREBASE_USER = auth.currentUser
                    updateUI(Constants.CURRENT_FIREBASE_USER)
                } else {
                    Log.w("FB_SIGNIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(Constants.CURRENT_FIREBASE_USER)
                }
            }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        Constants.CURRENT_FIREBASE_USER = auth.currentUser
        updateUI(Constants.CURRENT_FIREBASE_USER)
    }

    fun updateUI(currentUser: FirebaseUser?){
        if (currentUser == null) {
            return
        }
        else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}