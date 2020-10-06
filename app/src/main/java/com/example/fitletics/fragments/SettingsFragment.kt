package com.example.fitletics.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.fitletics.R
import com.example.fitletics.activities.CreateWorkoutActivity
import com.example.fitletics.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_settings, container, false)

        rootView.findViewById<Button>(R.id.logout_button).setOnClickListener()
        {
            auth.signOut()
            startActivity(Intent(this.activity!!, LoginActivity::class.java))
            this.activity!!.finish()
        }

        return rootView
    }
}