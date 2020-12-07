package com.example.fitletics.models.utils;

import android.content.Context;
import android.util.Log;

import com.example.fitletics.models.support.Exercise;
import com.example.fitletics.models.support.ExerciseStat;
import com.example.fitletics.models.support.Muscle;
import com.example.fitletics.models.support.Session;
import com.example.fitletics.models.support.Workout;
import com.example.fitletics.models.utils.WorkoutPriority;
import com.google.firebase.firestore.DocumentReference;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Math.round;

public class RecEngine {

    //Outputs the priorities for every supported workout sorted in descending order
    static WorkoutPriority[] getWorkoutPriorities(Context ctx, String bodyType, double upperBodyScore, double lowerBodyScore, double coreScore) throws IOException {

        //bodyType should be Ectomorph, Endomorph or Mesomorph (exact same spelling)
        String filename = "fcl/" + bodyType + ".fcl";

        InputStream is = ctx.getApplicationContext().getAssets().open(filename);

        FIS fis = FIS.load(is, true);

        if (fis == null) {
            System.err.println("Can't load file: '" + filename + "'");
            System.exit(1);
        }

        FunctionBlock fb = fis.getFunctionBlock(null);

        fb.setVariable("UpperBodyScore", upperBodyScore);
        fb.setVariable("LowerBodyScore", lowerBodyScore);
        fb.setVariable("CoreScore", coreScore);

        fb.evaluate();

        WorkoutPriority[] workoutPriorities = {
                new WorkoutPriority("Upper"),
                new WorkoutPriority("Lower"),
                new WorkoutPriority("Core"),
                new WorkoutPriority("FullBody")
        };

        for (WorkoutPriority workoutPriority : workoutPriorities)
            workoutPriority.setWorkoutPriority(fb.getVariable(workoutPriority.getWorkoutCategory() + "Priority").getValue());

        Arrays.sort(workoutPriorities);

        return workoutPriorities;
    }

    // Returns the names of the last 2 workouts that the user did
    static String[] getLastTwoWorkouts(){
        //TODO: @Vishal replace this with code that actually gets the names from the DB.
        // Names should be in the following set: {Upper, Lower, Core, FullBody}
        String [] workoutNames = {"Upper", "Lower"};
        return workoutNames;
    }

    // Recommends one workout category to the user
    static String recommendWorkoutCategory(WorkoutPriority[] workoutPriorities){

        String[] lastTwoWorkouts = getLastTwoWorkouts();

        boolean firstWorkoutIsAllowed = true;

        if (lastTwoWorkouts[0].equals(lastTwoWorkouts[1]) && workoutPriorities[0].getWorkoutCategory().equals(lastTwoWorkouts[0]))
            firstWorkoutIsAllowed = false;

        if (firstWorkoutIsAllowed)
            return workoutPriorities[0].getWorkoutCategory();
        else
            return workoutPriorities[1].getWorkoutCategory();

    }

    // Returns a Session object with details pertaining to the user's last session for that category
    static Session getLatestSession(String category){
        //TODO: @Vishal find the last workout session from this category. Return a null object if it doesn't exist
        Session lastSession = new Session();

        return lastSession;
    }

    //TODO: @Vishal Store Milestones in the DB as a workout. Workout name should be Core, Upper, Lower, Full and
    // level should be Beginner, Intermediate or Advanced
    //Returns a milestone workout based on the workout category and level
    static Workout returnMilestone(String category, String level){

        //TODO: @Vishal find the milestone by looking for the category as the milestone name and the correct level in the database
        Workout milestone = new Workout();

        return milestone;
    }

    //Checks if the user did every recommended exercise correctly in their last session
    static boolean checkWhetherExercisesWereDonePerfectly(Session session) throws Exception{

        Workout workout = session.getWorkout();

        for (int i=0; i<workout.getExerciseList().size(); i++){
            Exercise exercise = workout.getExerciseList().get(i);
            ExerciseStat exerciseStat = session.getCompletedStats().get(i);
            if (exercise.getName().equals(exerciseStat.getName())) {
                if (exercise.getUnit() == Exercise.Unit.SECS) {
                    float difference = exercise.getValue() - exerciseStat.getTimeTaken();
                    if (difference > 0) return false;
                } else {
                    if (exercise.getValue() > exerciseStat.getRepsDone()) return false;
                }
            }
            else {
                throw new Exception("Order of exercises is wrong!");
            }
        }

        return true;
    }

    //Gets the appropriate milestone workout for the user in order to determine what exercises to recommend
    static Workout getMilestoneWorkout(String category){
        Session lastSession = getLatestSession(category);

        String level;

        if (lastSession == null) {
            level = "Beginner";
            return returnMilestone(category, level);
        }
        else {
            //get last workout
            Workout lastWorkout = lastSession.getWorkout();
            level = lastWorkout.getLevel();

            //find matching milestone
            String lastWorkoutName = lastWorkout.getName();
            String lastWorkoutLevel = lastWorkout.getLevel();
            Workout milestone = returnMilestone(lastWorkoutName, lastWorkoutLevel);

            //check if user was asked to do the same exercises and reps as the milestone
            boolean sameRecommendationAsMilestone = true;

            for (int i=0; i<lastWorkout.getExerciseList().size(); i++)
                if (!lastWorkout.getExerciseList().get(i).equals(milestone.getExerciseList().get(i)) ) {
                    sameRecommendationAsMilestone = false;
                    break;
                }

            //if yes, then check if they did all the exercises perfectly
            boolean exercisesDonePerfectly;

            if (sameRecommendationAsMilestone){
                try {
                    exercisesDonePerfectly = checkWhetherExercisesWereDonePerfectly(lastSession);
                } catch (Exception e){
                    System.err.println("Handling this exception because Java won't let me go without doing this.");
                    exercisesDonePerfectly = false;
                }

                String nextLevel;

                if (level.equals("Beginner")) nextLevel = "Intermediate";
                else if (level.equals("Intermediate")) nextLevel = "Advanced";
                else nextLevel = "Advanced";

                if (exercisesDonePerfectly) return returnMilestone(category, nextLevel);
                else return returnMilestone(category, level);
            }
            else return returnMilestone(category, level);

        }

    }

    //Gets the required exercise details from the database and returns it as an Exercise object
    static Exercise getExercise(String exerciseName){
        //TODO: @Vishal this should find the exercise in the db and return the object
        return new Exercise();
    }

    //Recommends a workout that the user should do if they pick a category
    public static Workout recommendWorkout(String category) throws Exception{

        Workout milestone = new Workout(getMilestoneWorkout(category));

        Session lastSession = new Session(getLatestSession(category));
        Workout lastWorkout = lastSession.getWorkout();

        Workout recommendedWorkout = new Workout();
        recommendedWorkout.setName(category);
        ArrayList<Exercise> recommendedExercises = new ArrayList<>(); //stores new exercise list to recommend to the user
        String level = "";

        boolean exercisesDonePerfectly;

        if (lastWorkout == null){
            return milestone;
        }
        else {
            if (!lastWorkout.getLevel().equals(milestone.getLevel())){
                return milestone;
            }
            try {
                exercisesDonePerfectly = checkWhetherExercisesWereDonePerfectly(lastSession);
            } catch (Exception e){
                System.err.println("Handling this exception because Java won't let me go without doing this.");
                exercisesDonePerfectly = false;
            }

            if (!exercisesDonePerfectly){
                level = lastWorkout.getLevel();
                for (int i=0; i<lastWorkout.getExerciseList().size(); i++){
                    Exercise exercise = lastWorkout.getExerciseList().get(i);
                    Exercise milestoneExercise = milestone.getExerciseList().get(i);
                    ExerciseStat exerciseStat = lastSession.getCompletedStats().get(i);
                    if (exercise.getName().equals(exerciseStat.getName())) {

                        recommendedExercises.add(new Exercise(getExercise(exercise.getName())));

                        if (exercise.getUnit() == Exercise.Unit.SECS) { //time-based exercises
                            float differenceBetweenRecommendedAndDone = exercise.getValue() - exerciseStat.getTimeTaken();
                            if (differenceBetweenRecommendedAndDone > 0) { //not done properly because user took less time that recommended
                                int newValue = round(exerciseStat.getTimeTaken() + (differenceBetweenRecommendedAndDone/2));
                                recommendedExercises.get(i).setValue(newValue);
                            }
                            else { //exercise was done properly
                                int differenceBetweenMilestoneAndRecommended = milestoneExercise.getValue() - exercise.getValue();
                                if (exercise.getValue() < milestoneExercise.getValue()){
                                    int newValue = round(exercise.getValue() + (differenceBetweenMilestoneAndRecommended/2));
                                    recommendedExercises.get(i).setValue(newValue);
                                }
                                else {
                                    recommendedExercises.get(i).setValue(milestoneExercise.getValue());
                                }
                            }
                        }
                        else { //rep-based exercises
                            float differenceBetweenRecommendedAndDone = exercise.getValue() - exerciseStat.getRepsDone();
                            if (differenceBetweenRecommendedAndDone > 0) { //user did fewer reps than recommended
                                int newReps = round(exercise.getValue() - (differenceBetweenRecommendedAndDone/2));
                                recommendedExercises.get(i).setValue(newReps);
                            }
                            else { //user did recommended reps
                                double avgTimeTaken = lastSession.getCompletedStats().get(i).getTimeTaken();
                                double neededAvgTime = exercise.getTimePerRep()*exercise.getValue();
                                double difference = neededAvgTime-avgTimeTaken;
                                if (difference<-3.5){
                                    recommendedExercises.get(i).setValue(exercise.getValue());
                                }
                                else {
                                    int differenceBetweenMilestoneAndRecommended = milestoneExercise.getValue() - exercise.getValue();
                                    if (exercise.getValue() < milestoneExercise.getValue()){
                                        int newValue = round(exercise.getValue() + (differenceBetweenMilestoneAndRecommended/2));
                                        recommendedExercises.get(i).setValue(newValue);
                                    }
                                    else {
                                        recommendedExercises.get(i).setValue(milestoneExercise.getValue());
                                    }
                                }
                            }
                        }
                    }
                    else {
                        throw new Exception("Order of exercises is wrong!");
                    }
                }
            }
            else { //recommended exercises were done perfectly
                level = milestone.getLevel();
                for (int i=0; i<lastWorkout.getExerciseList().size(); i++) {
                    Exercise exercise = lastWorkout.getExerciseList().get(i);
                    Exercise milestoneExercise = milestone.getExerciseList().get(i);
                    ExerciseStat exerciseStat = lastSession.getCompletedStats().get(i);
                    if (exercise.getName().equals(exerciseStat.getName())) {

                        recommendedExercises.add(new Exercise(getExercise(exercise.getName())));

                        if (exercise.getUnit() == Exercise.Unit.SECS) { //time-based exercises
                            int differenceBetweenMilestoneAndRecommended = milestoneExercise.getValue() - exercise.getValue();
                            if (exercise.getValue() < milestoneExercise.getValue()){
                                int newValue = round(exercise.getValue() + (differenceBetweenMilestoneAndRecommended/2));
                                recommendedExercises.get(i).setValue(newValue);
                            }
                            else {
                                recommendedExercises.get(i).setValue(milestoneExercise.getValue());
                            }
                        }
                        else { //rep-based exercises
                            int differenceBetweenMilestoneAndRecommended = milestoneExercise.getValue() - exercise.getValue();
                            if (exercise.getValue() < milestoneExercise.getValue()){
                                int newValue = round(exercise.getValue() + (differenceBetweenMilestoneAndRecommended/2));
                                recommendedExercises.get(i).setValue(newValue);
                            }
                            else {
                                recommendedExercises.get(i).setValue(milestoneExercise.getValue());
                            }
                        }
                    }
                    else {
                        throw new Exception("Order of exercises is wrong!");
                    }
                }
            }
        }

        recommendedWorkout.calculateDuration();
        recommendedWorkout.calculateDifficulty();
        return recommendedWorkout;
    }

    //Identifies the ideal category for the user and recommends a workout as well

    private static OkHttpClient client = new OkHttpClient();


    static String dbResponse = "";

    public static String run(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();

        String outputResponse = "";
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    dbResponse = response.body().toString();
                }
            }
        });

        return dbResponse;
    }


    public static void recommendWorkout(Context ctx) throws Exception {
        //TODO: @Vishal assign the vars from the DB

//        String bodyType = "Mesomorph";
//        double upperBodyScore = 2;
//        double lowerBodyScore = 9;
//        double coreScore = 4;

//        return recommendWorkout(recommendWorkoutCategory(getWorkoutPriorities(ctx, bodyType, upperBodyScore, lowerBodyScore, coreScore)));
    }


}
