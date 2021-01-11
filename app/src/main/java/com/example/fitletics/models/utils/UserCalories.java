package com.example.fitletics.models.utils;

public class UserCalories {

    // calculates an average amount of calories that the user will burn per step that they take
    public static double calcCaloriesPerStep(double height, double weight){

        double caloriesPerMin = ((0.035*weight)+((Math.pow(1.4,2))/height))*0.029*weight;
        int stepsPerMin = 100;

        double caloriesPerStep = caloriesPerMin/stepsPerMin;

        return caloriesPerStep;
    }

}
