package com.example.fitletics.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.example.fitletics.R
import com.example.fitletics.models.Constants
import com.example.fitletics.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val c = Calendar.getInstance()

    private var userDOB: String = ""
    private var gender: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        calendarSetup()
        spinnerSetup()

        sign_up_button.setOnClickListener()
        {
            signUpUser()
        }

        auth = FirebaseAuth.getInstance()
    }

    private fun spinnerSetup() {
        gender_spinner_signup.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                gender = "ERROR"
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                gender = p0?.getItemAtPosition(p2).toString()
            }

        }
    }

    private fun calendarSetup(){
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DOB_text_signup.setOnClickListener{
            val datePicker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener{
                        view, mYear, mMonth, mDay ->
                    DOB_text_signup.setText(" $mDay / ${mMonth+1} / $mYear")
                    //userDOB?.set(mYear, mMonth+1, mDay)
                    userDOB = " $mDay / ${mMonth+1} / $mYear"
            }, year, month, day)
            datePicker.show()
        }

    }

    private fun signUpUser(){

        //Data Validation
        if (name_edit_text.text.toString().isEmpty()) {
            name_edit_text.error = "Please enter a name!"
            name_edit_text.requestFocus()
            return
        }

        if (email_edit_text_signup.text.toString().isEmpty()) {
            email_edit_text_signup.error = "Please enter email!"
            email_edit_text_signup.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email_edit_text_signup.text.toString()).matches()){
            email_edit_text_signup.error = "Please enter a valid email!"
            email_edit_text_signup.requestFocus()
            return
        }

        if (passowrd_edit_text_signup.text.toString().isEmpty()) {
            passowrd_edit_text_signup.error = "Please enter a password!"
            passowrd_edit_text_signup.requestFocus()
            return
        }

        if (re_enter_passowrd_edit_text_signup.text.toString().isEmpty()) {
            re_enter_passowrd_edit_text_signup.error = "Please re-enter the password!"
            re_enter_passowrd_edit_text_signup.requestFocus()
            return
        }

        if (re_enter_passowrd_edit_text_signup.text.toString() != passowrd_edit_text_signup.text.toString()) {
            re_enter_passowrd_edit_text_signup.error = "Please enter the same password!"
            re_enter_passowrd_edit_text_signup.requestFocus()
            return
        }

        if (DOB_text_signup.text.toString().isEmpty()) {
            DOB_text_signup.error = "Please re-enter the password!"
            DOB_text_signup.requestFocus()
            return
        }

        if (gender == "Error"){
            gender_spinner_signup.requestFocus()
            return
        }

        /*==========================END OF DATA VALIDATION=================================*/

        //User Sign up
        auth.createUserWithEmailAndPassword(email_edit_text_signup.text.toString(), re_enter_passowrd_edit_text_signup.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val newUser: User = User(
                        name = name_edit_text.text.toString(),
                        email = email_edit_text_signup.text.toString(),
                        DOB = userDOB,
                        gender = gender,
                        weight = Integer.parseInt(weight_text_signup.text.toString()),
                        height = Integer.parseInt(height_text_signup.text.toString())
                    )

                    Log.d("DOB", userDOB.toString())

                    val user = auth.currentUser
                    Constants.CURRENT_FIREBASE_USER = user


                    //firebase code
//                    FirebaseDatabase.getInstance().getReference("Users")
//                        .child(Constants.CURRENT_FIREBASE_USER!!.uid)
//                        .child("Details")
//                        .setValue(newUser)
//                        .addOnCompleteListener {
////                            Toast.makeText(baseContext, "User Details Added!",
////                                Toast.LENGTH_SHORT).show()
//                            Log.d("FB_SIGNUP", "createUserWithEmail:success")
//                            startActivity(Intent(this, ConnectPCQRActivity::class.java))
//                        }

                    //firestore code

                    FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(Constants.CURRENT_FIREBASE_USER!!.uid)
                        .collection("Details")
                        .document("details")
                        .set(newUser)
                        .addOnSuccessListener {
                            Log.d("FB_SIGNUP", "createUserWithEmail:success")
                            startActivity(Intent(this, ConnectPCQRActivity::class.java))
                        }
                }

                else {
                    // If sign in fails, display a message to the user.
                    Log.w("FB_SIGNUP", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}