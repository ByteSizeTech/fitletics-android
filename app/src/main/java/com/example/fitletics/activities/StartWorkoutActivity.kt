package com.example.fitletics.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.dialogs.ExerciseDescriptionDialog
import com.example.fitletics.models.Constants
import com.example.fitletics.models.Constants.Companion.CURRENT_FIREBASE_USER
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_body_analysis_ongoing.*
import kotlinx.android.synthetic.main.activity_start_workout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartWorkoutActivity : AppCompatActivity() {

    private var workoutObject: Workout? = null
    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences

    val TAG = "START_WORKOUT_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_workout)

        begin_workout_button.isEnabled = false

        begin_workout_button.setOnClickListener {
            sendstartSession()
            startActivity(Intent(this, ActiveSessionActivity::class.java))
        }


        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)

        workoutObject = intent.getSerializableExtra("Workout_object") as Workout?
        Log.d(TAG, "workoutObject from intent: ${workoutObject?.name}")

        setupSendButton()
        setupArrayList()
        setupActivityText()

        listView = findViewById(R.id.workout_exercise_list)
        listAdapter = WorkoutExerciseListAdapter(this, workoutObject?.exerciseList!!)
        listView?.adapter = listAdapter

        listView?.setOnItemClickListener{parent, view, position, id ->
            val entry= parent.getItemAtPosition(position) as Exercise
            Log.d(TAG, "desc: ${entry.name}");
            val dialog = ExerciseDescriptionDialog(entry)
            dialog.show(supportFragmentManager, "exerciseDescription")
        }

        startSession()
    }

    private fun sendstartSession() {

    }

    private fun bundleWorkout(){

    }


    private fun startSession() {
        //check if session is already populated
        //val UserUID = hashMapOf("UID" to Constants.CURRENT_USER!!.userID )
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .set(
                mapOf(
                    "uid" to Constants.CURRENT_FIREBASE_USER!!.uid,
                    "active_task" to "SD",
                    "task_state" to "requested",
                    "task_message" to mapOf("workout_obj" to workoutObject?.getDBFriendlyResult())
                )
                , SetOptions.merge()
            ).addOnSuccessListener {
//                sendWorkout();
                FirebaseFirestore.getInstance()
                    .collection("Sessions")
                    .document(sessionUID!!)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null){
                            Log.d(TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            if (snapshot.data?.get("task_state") == "ongoing") {
                                Log.d(TAG, "Capture button enabled!")
                                begin_workout_button.isEnabled = true
                            }
                            if (snapshot.data?.get("task_state") == "cancelled") {

                                sessionCancelled()
                                return@addSnapshotListener
                            }
                        }
                        else{
                            Log.d(TAG, "Session UID obj is null!")
                        }
                    }
            }.addOnFailureListener { e ->
                Log.d(TAG, "Error updating document")
                startActivity(Intent(this, ConnectPCQRActivity::class.java));
                Toast.makeText(baseContext, "Error! Scan QR", Toast.LENGTH_SHORT).show()

                //TODO: delete share pref if exists
            }
    }

    private fun sessionCancelled() {
        finish()
    }

    private fun setupSendButton()
    {
        create_workout_send_workout.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
            val usernameText = dialogView.findViewById<EditText>(R.id.recipient_username_edit_text)
            dialog.setView(dialogView)
            dialog.setPositiveButton( "Send") { _: DialogInterface, _: Int ->}
            val customDialog = dialog.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                 var canContinue = true
                if (usernameText.text.toString() == Constants.CURRENT_USER?.userID){
                    Log.d("SENDING_WORKOUT", "Over smart user and canContinue: $canContinue")
                    canContinue = false
                    usernameText.error = "Not so fast!"
                    usernameText.requestFocus()
                }
                else {
                    FirebaseFirestore.getInstance()
                        .collection("Users")
                        .whereEqualTo("userID", usernameText.text.toString())
                        .get()
                        .addOnCompleteListener { task ->
                            Log.d("SENDING_WORKOUT","result before if: ${task.result!!.size()} and canContinue: $canContinue")
                            if (task.result!!.size() == 0) {
                                Log.d("SENDING_WORKOUT","result after if: ${task.result!!.size()}, and canContinue: $canContinue")
                                canContinue = false
                                usernameText.error = "Enter a valid username!"
                                usernameText.requestFocus()
                            }
                            if (canContinue) {
                                Log.d("SENDING_WORKOUT", "can proceed to sending!")
                                Log.d("SENDING_WORKOUT","uID is ${task.result?.documents!![0].id}!")

                                var tempWorkoutObject = Workout(
                                    name = workoutObject?.name,
                                    exerciseList = workoutObject?.exerciseList!!,
                                    difficulty = workoutObject?.difficulty,
                                    time = workoutObject?.time
                                )

                                FirebaseFirestore.getInstance()
                                    .collection("Users")
                                    .document(task.result?.documents!![0].id)
                                    .collection("Workouts")
                                    .document("Pending")
                                    .collection("workouts")
                                    .document()
                                    .set(tempWorkoutObject, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d("SENDING_WORKOUT", "Workout added to database")
                                        customDialog.dismiss()
                                        Toast.makeText(baseContext, "Workout Sent to ${usernameText.text}!", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                }
            }
        }
    }

    private fun setupActivityText() {
        Log.d(TAG, "name: ${workoutObject?.name}, time: ${workoutObject?.time}, diff: ${workoutObject?.difficulty}")

        val nameText: TextView = findViewById(R.id.workout_title_text)
        val timeText: TextView = findViewById(R.id.workout_time_text)
        val diffText: TextView = findViewById(R.id.workout_difficulty_text)

        nameText.text = workoutObject?.name
        timeText.text = workoutObject?.time
        diffText.text = workoutObject?.difficulty
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CoroutineScope(IO).launch {
            longDBTask()
        }
    }

    private suspend fun longDBTask() {
        logThread("longDBTask")
        FirebaseFirestore.getInstance()
            .collection("Sessions")
            .document(sessionUID!!)
            .update(mapOf("task_message" to "cancelled"))
            .addOnSuccessListener {
                Log.d(TAG, "Sent cancel message to session")
            }.addOnFailureListener {
                Log.d(TAG, "Error sending cancel message")
            }
        Log.d(TAG, "Back button pressed")
    }

    private fun logThread(methodName: String){
        Log.d(TAG, "$methodName from ${Thread.currentThread().name}")
    }

    private fun setupArrayList() {
        if (workoutObject?.exerciseList?.isEmpty()!!){
            Log.d(TAG, "Exercise list was empty!")
            workoutObject?.exerciseList = ArrayList()
            workoutObject?.exerciseList?.add(Exercise(name="Crunches", value="12x"))
            workoutObject?.exerciseList?.add(Exercise(name="Sit ups", value="14x"))
            workoutObject?.exerciseList?.add(Exercise(name="Stretches", value="40s"))
            workoutObject?.exerciseList?.add(Exercise(name="Squats", value="14x"))
            workoutObject?.exerciseList?.add(Exercise(name="Pullups", value="12x"))
            workoutObject?.exerciseList?.add(Exercise(name="Crunches", value="14x"))
            workoutObject?.exerciseList?.add(Exercise(name="Sit ups", value="8x"))
            workoutObject?.exerciseList?.add(Exercise(name="Stretches", value="40s"))
        }
        else
            return
    }
}