package com.example.fitletics.models.utils;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.sqrt;

public class UserXP {

    // calculates the upper and lower bound of XP that is available per level
    static int[] calcMinAndMaxXP(int level){

        int max = (int)((100*(Math.pow(level,2)))/2)-1;
        int min = (int)((100*(Math.pow(level-1,2)))/2);

        int[] output = {min, max};
        return output;
    }

    // calculates the user's XP based on their level
    static int calcLevel(int xp){
        int level = (((int)(sqrt((xp*2.0)/100.0)))+1);
        return level;
    }

    // outputs XP-related information for display on the app dashboard
    public static Map<String, Object> returnExperienceProgress(int xp){
        Map<String, Object> output = new HashMap<>();

        int level = calcLevel(xp);
        output.put("Level", level);

        int min = calcMinAndMaxXP(level)[0];
        int max = calcMinAndMaxXP(level)[1];
        int difference = max-min;
        int percentageComplete = (int)(((double)(xp-min)/(double)difference)*100);
        output.put("Percentage", percentageComplete);

        String xpDisplay = xp + "/" + (max+1) + " XP";
        output.put("XP", xpDisplay);

        return output;
    }
}
