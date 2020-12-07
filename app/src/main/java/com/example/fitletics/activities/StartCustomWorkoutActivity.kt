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
import com.example.fitletics.fragments.homepage.dialogs.ExerciseDescriptionDialog
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.support.Exercise
import com.example.fitletics.models.utils.WebsiteSession
import com.example.fitletics.models.support.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_start_custom_workout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class StartCustomWorkoutActivity : AppCompatActivity() {

    private var workoutObject: Workout? = null
    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    var sessionUID: String? = null
    lateinit var sharedPref: SharedPreferences

    val TAG = "START_WORKOUT_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_custom_workout)

        begin_workout_button.isEnabled = false




        sharedPref = getSharedPreferences("session_uid_data", Context.MODE_PRIVATE)
        sessionUID = sharedPref.getString("UID", null)

        workoutObject = intent.getSerializableExtra("Workout_object") as Workout?
        Log.d(TAG, "workoutObject from intent: ${workoutObject?.name}")

        setupSendButton()
        setupArrayList()
        setupActivityText()
        populateListView()

        begin_workout_button.setOnClickListener {
            startActiveSessionActivity()
        }

        startSession()
    }

    private fun populateListView() {
        listView = findViewById(R.id.workout_exercise_list)
        listAdapter = WorkoutExerciseListAdapter(this, workoutObject?.exerciseList!!)
        listView?.adapter = listAdapter

        listView?.setOnItemClickListener{parent, view, position, id ->
            val entry= parent.getItemAtPosition(position) as Exercise
            Log.d(TAG, "desc: ${entry.name}")
            val dialog =
                ExerciseDescriptionDialog(
                    this,
                    entry.name!!,
                    entry.description,
                    entry.link!!,
                    "Cancel",
                    true
                )
            dialog.show(supportFragmentManager, "exerciseDescription")
        }
    }


    lateinit var sessionTracker: ListenerRegistration
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
                    "task_message" to
                            mapOf(
                                "workout_obj" to workoutObject?.firebaseFriendlyWorkout(),
                                "active_session_listeners" to
                                        mapOf(
                                            "curr_ex_name" to "null",
                                            "curr_ex_unit" to "null",
                                            "curr_ex_progress" to "null",
                                            "curr_ex_goal" to "null",
                                            "skip_request" to "null"
                                        ))), SetOptions.merge()
            ).addOnSuccessListener {
//                sendWorkout();
                sessionTracker =
                FirebaseFirestore.getInstance()
                    .collection("Sessions")
                    .document(sessionUID!!)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null){
                            Log.d(TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            if (snapshot.data?.get("task_state") == "waiting") {
                                Log.d(TAG, "Begin workout button enabled!")
                                begin_workout_button.isEnabled = true
                            }
                            if (snapshot.data?.get("task_state") == "cancelled") {
                                sessionCancelled()
                                return@addSnapshotListener
                            }
                            if (snapshot.data?.get("active_task") == "AS"  && snapshot.data?.get("task_state") == "requested") {
                                startActiveSessionActivity()
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
            }
    }

    private fun startActiveSessionActivity() {
//        FirebaseFirestore.getInstance()
//            .collection("Sessions")
//            .document(sessionUID!!)
//            .set(
//                mapOf("active_task" to "AS",
//                        "task_state" to "requested"
//            ),SetOptions.merge())
//            .addOnCompleteListener {
//                WebsiteSession(this, ActiveSessionActivity::class.java, workoutObject);
//                finish()
//            }.addOnFailureListener {
//                Log.d(TAG, "Start active session request failed")
//            }

        sessionTracker.remove()
        WebsiteSession(
            this,
            ActiveSessionActivity::class.java,
            workoutObject
        );
        finish()
    }

    private fun sessionCancelled() {
        //TODO: Pass cancel functions
        finish()
    }

    private fun setupSendButton() {
        create_workout_send_workout.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_template, null)
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

                                var tempWorkoutObject =
                                    Workout(
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
            .update(mapOf(
                "active_task" to "DB",
                "task_message" to "cancel"))
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
            workoutObject?.exerciseList?.add(
                Exercise(
                    name = "Crunches",
                    value = 12
                )
            )
            workoutObject?.exerciseList?.add(
                Exercise(
                    name = "Sit ups",
                    value = 14
                )
            )
            workoutObject?.exerciseList?.add(
                Exercise(
                    name = "Stretches",
                    value = 40
                )
            )
            workoutObject?.exerciseList?.add(
                Exercise(
                    name = "Squats",
                    value = 14
                )
            )
            workoutObject?.exerciseList?.add(
                Exercise(
                    name = "Pullups",
                    value = 12
                )
            )
            workoutObject?.exerciseList?.add(
                Exercise(
                    name = "Crunches",
                    value = 14
                )
            )
            workoutObject?.exerciseList?.add(
                Exercise(
                    name = "Sit ups",
                    value = 8
                )
            )
            workoutObject?.exerciseList?.add(
                Exercise(
                    name = "Stretches",
                    value = 40
                )
            )
        }
        else
            return
    }
}