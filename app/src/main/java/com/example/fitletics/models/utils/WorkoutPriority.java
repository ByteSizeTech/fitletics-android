package com.example.fitletics.models.utils;

public class WorkoutPriority implements Comparable<WorkoutPriority>  {
    private String workoutCategory;
    private double workoutPriority;

    public WorkoutPriority(){
        workoutCategory = "";
        workoutPriority = 0.0;
    }

    public WorkoutPriority(String categoryName){
        workoutCategory = categoryName;
        workoutPriority = 0.0;
    }

    public WorkoutPriority(String categoryName, double priority){
        workoutCategory = categoryName;
        workoutPriority = priority;
    }

    public String getWorkoutCategory() {
        return workoutCategory;
    }

    public void setWorkoutCategory(String workoutCategory) {
        this.workoutCategory = workoutCategory;
    }

    public double getWorkoutPriority() {
        return workoutPriority;
    }

    public void setWorkoutPriority(double workoutPriority) {
        this.workoutPriority = workoutPriority;
    }

    @Override
    public String toString() {
        return workoutCategory + " - " + workoutPriority;
    }

    @Override
    public int compareTo(WorkoutPriority w) {
        return (int)((w.getWorkoutPriority()*1000) - (this.workoutPriority*1000));
    }
}
