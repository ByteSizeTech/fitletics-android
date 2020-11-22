package com.example.fitletics.activities

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fitletics.R
import com.example.fitletics.adapters.WorkoutExerciseListAdapter
import com.example.fitletics.models.Constants
import com.example.fitletics.models.Exercise
import com.example.fitletics.models.Workout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_start_workout.*

class StartWorkoutActivity : AppCompatActivity() {

    private var arrayList: ArrayList<Exercise>? = null
    private var listView: ListView? = null
    private var listAdapter: WorkoutExerciseListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_workout)

//        var original: ArrayList<Exercise>? = null
//        original = ArrayList()
//        original?.add(Exercise("test1", "lol"))
//        original?.add(Exercise("test2", "30a"))
//        var copy: ArrayList<Exercise>? = null
//        Log.d("AL_TEST_TEST", "AL: ${original[0].name}")
//        copy = original
//        Log.d("AL_TEST_TEST", "AL: ${copy[0].name}")



        //arrayList = ArrayList()
        arrayList = intent.getSerializableExtra("Workout_ex_list") as ArrayList<Exercise>
        Log.d("AL_TEST", "AL: ${arrayList!!.size}")

        setupSendButton(
            intent.getStringExtra("Workout_name")!!,
            intent.getStringExtra("Workout_time")!!,
            intent.getStringExtra("Workout_diff")!!
        )

        val intent = intent
        setupArrayList()
        setupActivityText(
            intent.getStringExtra("Workout_name")!!,
            intent.getStringExtra("Workout_time")!!,
            intent.getStringExtra("Workout_diff")!!
        )

        listView = findViewById(R.id.workout_exercise_list)
        listAdapter = WorkoutExerciseListAdapter(this, arrayList!!)
        listView?.adapter = listAdapter


        listView?.setOnItemClickListener{parent, view, position, id ->
            //TODO: Finish this
        }
    }

    private fun setupSendButton(name: String, time: String, diff: String) {
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
                                    name = name,
                                    exerciseList = this.arrayList!!,
                                    difficulty = time,
                                    time = diff
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

    private fun setupActivityText(name: String, time: String, diff: String) {
        Log.d("WORKOUT_INFO", "name: ${name}, time: ${time}, diff: ${diff}")

        val nameText: TextView = findViewById(R.id.workout_title_text)
        val timeText: TextView = findViewById(R.id.workout_time_text)
        val diffText: TextView = findViewById(R.id.workout_difficulty_text)

        nameText.text = name
        timeText.text = time
        diffText.text = diff
    }

    private fun setupArrayList() {
        if (arrayList?.isEmpty()!!){
            arrayList = ArrayList()
            arrayList?.add(Exercise(name="Crunches", value="12x"))
            arrayList?.add(Exercise(name="Sit ups", value="14x"))
            arrayList?.add(Exercise(name="Stretches", value="40s"))
            arrayList?.add(Exercise(name="Squats", value="14x"))
            arrayList?.add(Exercise(name="Pullups", value="12x"))
            arrayList?.add(Exercise(name="Crunches", value="14x"))
            arrayList?.add(Exercise(name="Sit ups", value="8x"))
            arrayList?.add(Exercise(name="Stretches", value="40s"))
        }
        else
            return
    }
}