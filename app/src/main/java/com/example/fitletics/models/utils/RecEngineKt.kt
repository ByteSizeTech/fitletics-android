package com.example.fitletics.models.utils

import android.content.Context
import android.util.Log
import com.example.fitletics.models.support.Constants
import com.example.fitletics.models.support.Exercise
import com.example.fitletics.models.support.Session
import com.example.fitletics.models.support.Workout
import com.example.fitletics.models.utils.FirebaseQueries.Companion.getUserInfo
import com.google.firebase.firestore.FirebaseFirestore
import net.sourceforge.jFuzzyLogic.FIS
import java.io.IOException
import java.util.*

class RecEngineKt {
    //Outputs the priorities for every supported workout sorted in descending order
    @Throws(IOException::class)
    fun getWorkoutPriorities(
        ctx: Context,
        bodyType: String,
        upperBodyScore: Double,
        lowerBodyScore: Double,
        coreScore: Double
    ): Array<WorkoutPriority> {

        //bodyType should be Ectomorph, Endomorph or Mesomorph (exact same spelling)
        val filename = "fcl/$bodyType.fcl"
        val `is` = ctx.applicationContext.assets.open(filename)
        val fis = FIS.load(`is`, true)
        if (fis == null) {
            System.err.println("Can't load file: '$filename'")
            System.exit(1)
        }
        val fb = fis!!.getFunctionBlock(null)
        fb.setVariable("UpperBodyScore", upperBodyScore)
        fb.setVariable("LowerBodyScore", lowerBodyScore)
        fb.setVariable("CoreScore", coreScore)
        fb.evaluate()
        val workoutPriorities = arrayOf(
            WorkoutPriority("Upper"),
            WorkoutPriority("Lower"),
            WorkoutPriority("Core"),
            WorkoutPriority("FullBody")
        )
        for (workoutPriority in workoutPriorities) workoutPriority.workoutPriority =
            fb.getVariable(workoutPriority.workoutCategory + "Priority").value
        Arrays.sort(workoutPriorities)
        return workoutPriorities
    }//TODO: @Vishal replace this with code that actually gets the names from the DB.
    // Names should be in the following set: {Upper, Lower, Core, FullBody}

    // Returns the names of the last 2 workouts that the user did
    val lastTwoWorkouts: Array<String>
        get() =//TODO: @Vishal replace this with code that actually gets the names from the DB.
            // Names should be in the following set: {Upper, Lower, Core, FullBody}
            arrayOf("Upper", "Lower")

    // Recommends one workout category to the user
    fun recommendWorkoutCategory(workoutPriorities: Array<WorkoutPriority>): String {
        val lastTwoWorkouts = lastTwoWorkouts
        var firstWorkoutIsAllowed = true
        if (lastTwoWorkouts[0] == lastTwoWorkouts[1] && workoutPriorities[0]
                .workoutCategory == lastTwoWorkouts[0]
        ) firstWorkoutIsAllowed = false
        return if (firstWorkoutIsAllowed) workoutPriorities[0]
            .workoutCategory else workoutPriorities[1].workoutCategory
    }

    // Returns a Session object with details pertaining to the user's last session for that category
    fun getLatestSession(category: String?): Session {
        //TODO: @Vishal find the last workout session from this category. Return a null object if it doesn't exist
        return Session()
    }

    //TODO: @Vishal Store Milestones in the DB as a workout. Workout name should be Core, Upper, Lower, Full and
    // level should be Beginner, Intermediate or Advanced
    //Returns a milestone workout based on the workout category and level
    fun returnMilestone(category: String?, level: String?): Workout {

        //TODO: @Vishal find the milestone by looking for the category as the milestone name and the correct level in the database
        return Workout()
    }

    //Checks if the user did every recommended exercise correctly in their last session
    @Throws(Exception::class)
    fun checkWhetherExercisesWereDonePerfectly(session: Session): Boolean {
        val workout = session.workout
        for (i in workout!!.exerciseList!!.indices) {
            val exercise = workout.exerciseList!![i]
            val exerciseStat = session.CompletedStats!![i]
            if (exercise.name == exerciseStat.name) {
                if (exercise.unit === Exercise.Unit.SECS) {
                    val difference = exercise.value!! - exerciseStat.timeTaken!!
                    if (difference > 0) return false
                } else {
                    if (exercise.value!! > exerciseStat.repsDone!!) return false
                }
            } else {
                throw Exception("Order of exercises is wrong!")
            }
        }
        return true
    }

    //Gets the appropriate milestone workout for the user in order to determine what exercises to recommend
    fun getMilestoneWorkout(category: String?): Workout {
        val lastSession =
            getLatestSession(category)
        val level: String
        return if (lastSession == null) {
            level = "Beginner"
            returnMilestone(category, level)
        } else {
            //get last workout
            val lastWorkout = lastSession.workout
            level = lastWorkout!!.level!!

            //find matching milestone
            val lastWorkoutName = lastWorkout.name
            val lastWorkoutLevel = lastWorkout.level
            val milestone = returnMilestone(lastWorkoutName, lastWorkoutLevel)

            //check if user was asked to do the same exercises and reps as the milestone
            var sameRecommendationAsMilestone = true
            for (i in lastWorkout.exerciseList!!.indices) if (!lastWorkout.exerciseList!![i]
                    .equals(milestone.exerciseList!![i])
            ) {
                sameRecommendationAsMilestone = false
                break
            }

            //if yes, then check if they did all the exercises perfectly
            val exercisesDonePerfectly: Boolean
            if (sameRecommendationAsMilestone) {
                exercisesDonePerfectly = try {
                    checkWhetherExercisesWereDonePerfectly(lastSession)
                } catch (e: Exception) {
                    System.err.println("Handling this exception because Java won't let me go without doing this.")
                    false
                }
                val nextLevel: String
                nextLevel =
                    if (level == "Beginner") "Intermediate" else if (level == "Intermediate") "Advanced" else "Advanced"
                if (exercisesDonePerfectly) returnMilestone(
                    category,
                    nextLevel
                ) else returnMilestone(category, level)
            } else returnMilestone(category, level)
        }
    }

    //Gets the required exercise details from the database and returns it as an Exercise object
    fun getExercise(exerciseName: String?): Exercise {
        //TODO: @Vishal this should find the exercise in the db and return the object
        return Exercise()
    }

    //Recommends a workout that the user should do if they pick a category
    @Throws(Exception::class)
    fun recommendWorkout(category: String?): Workout {
        val milestone = Workout(getMilestoneWorkout(category))
        val lastSession =
            Session(getLatestSession(category))
        val lastWorkout = lastSession.workout
        val recommendedWorkout = Workout()
        recommendedWorkout.name = category
        val recommendedExercises =
            ArrayList<Exercise>() //stores new exercise list to recommend to the user
        var level: String? = ""
        val exercisesDonePerfectly: Boolean
        if (lastWorkout == null) {
            return milestone
        } else {
            if (lastWorkout.level != milestone.level) {
                return milestone
            }
            exercisesDonePerfectly = try {
                checkWhetherExercisesWereDonePerfectly(lastSession)
            } catch (e: Exception) {
                System.err.println("Handling this exception because Java won't let me go without doing this.")
                false
            }
            if (!exercisesDonePerfectly) {
                level = lastWorkout.level
                for (i in lastWorkout.exerciseList!!.indices) {
                    val exercise = lastWorkout.exerciseList!![i]
                    val milestoneExercise = milestone.exerciseList!![i]
                    val exerciseStat = lastSession.CompletedStats!![i]
                    if (exercise.name == exerciseStat.name) {
                        recommendedExercises.add(Exercise(getExercise(exercise.name)))
                        if (exercise.unit === Exercise.Unit.SECS) { //time-based exercises
                            val differenceBetweenRecommendedAndDone =
                                exercise.value!! - exerciseStat.timeTaken!!
                            if (differenceBetweenRecommendedAndDone > 0) { //not done properly because user took less time that recommended
                                val newValue =
                                    Math.round(exerciseStat.timeTaken!! + differenceBetweenRecommendedAndDone / 2)
                                recommendedExercises[i].value = newValue
                            } else { //exercise was done properly
                                val differenceBetweenMilestoneAndRecommended =
                                    milestoneExercise.value!! - exercise.value!!
                                if (exercise.value!! < milestoneExercise.value!!) {
                                    val newValue =
                                        Math.round(exercise.value!! + (differenceBetweenMilestoneAndRecommended / 2).toFloat())
                                    recommendedExercises[i].value = newValue
                                } else {
                                    recommendedExercises[i].value = milestoneExercise.value
                                }
                            }
                        } else { //rep-based exercises
                            val differenceBetweenRecommendedAndDone =
                                exercise.value!! - exerciseStat.repsDone!!.toFloat()
                            if (differenceBetweenRecommendedAndDone > 0) { //user did fewer reps than recommended
                                val newReps =
                                    Math.round(exercise.value!! - differenceBetweenRecommendedAndDone / 2)
                                recommendedExercises[i].value = newReps
                            } else { //user did recommended reps
                                val avgTimeTaken =
                                    lastSession.CompletedStats!![i].timeTaken!!.toDouble()
                                val neededAvgTime =
                                    exercise.timePerRep!! * exercise.value!!
                                val difference = neededAvgTime - avgTimeTaken
                                if (difference < -3.5) {
                                    recommendedExercises[i].value = exercise.value
                                } else {
                                    val differenceBetweenMilestoneAndRecommended =
                                        milestoneExercise.value!! - exercise.value!!
                                    if (exercise.value!! < milestoneExercise.value!!) {
                                        val newValue =
                                            Math.round(exercise.value!! + (differenceBetweenMilestoneAndRecommended / 2).toFloat())
                                        recommendedExercises[i].value = newValue
                                    } else {
                                        recommendedExercises[i].value = milestoneExercise.value
                                    }
                                }
                            }
                        }
                    } else {
                        throw Exception("Order of exercises is wrong!")
                    }
                }
            } else { //recommended exercises were done perfectly
                level = milestone.level
                for (i in lastWorkout.exerciseList!!.indices) {
                    val exercise = lastWorkout.exerciseList!![i]
                    val milestoneExercise = milestone.exerciseList!![i]
                    val exerciseStat = lastSession.CompletedStats!![i]
                    if (exercise.name == exerciseStat.name) {
                        recommendedExercises.add(Exercise(getExercise(exercise.name)))
                        if (exercise.unit === Exercise.Unit.SECS) { //time-based exercises
                            val differenceBetweenMilestoneAndRecommended =
                                milestoneExercise.value!! - exercise.value!!
                            if (exercise.value!! < milestoneExercise.value!!) {
                                val newValue =
                                    Math.round(exercise.value!! + (differenceBetweenMilestoneAndRecommended / 2).toFloat())
                                recommendedExercises[i].value = newValue
                            } else {
                                recommendedExercises[i].value = milestoneExercise.value
                            }
                        } else { //rep-based exercises
                            val differenceBetweenMilestoneAndRecommended =
                                milestoneExercise.value!! - exercise.value!!
                            if (exercise.value!! < milestoneExercise.value!!) {
                                val newValue =
                                    Math.round(exercise.value!! + (differenceBetweenMilestoneAndRecommended / 2).toFloat())
                                recommendedExercises[i].value = newValue
                            } else {
                                recommendedExercises[i].value = milestoneExercise.value
                            }
                        }
                    } else {
                        throw Exception("Order of exercises is wrong!")
                    }
                }
            }
        }
        recommendedWorkout.calculateDuration()
        recommendedWorkout.calculateDifficulty()
        return recommendedWorkout
    }

    //Identifies the ideal category for the user and recommends a workout as well
    @Throws(Exception::class)
    fun recommendWorkout(ctx: Context): Workout {
        //TODO: @Vishal assign the vars from the DB

        var bodyType: String = ""
        var upperBodyScore: Double = 0.0
        var coreScore: Double = 0.0
        var lowerBodyScore: Double = 0.0
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(Constants.CURRENT_FIREBASE_USER!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    bodyType = document.data?.get("bodyType").toString()
                    upperBodyScore = document.data?.get("upperScore").toString().toDouble()
                    coreScore = document.data?.get("coreScore").toString().toDouble()
                    lowerBodyScore = document.data?.get("lowerScore").toString().toDouble()

                }
            }



        return recommendWorkout(recommendWorkoutCategory(getWorkoutPriorities(ctx, bodyType, upperBodyScore, lowerBodyScore, coreScore)));
    }
}