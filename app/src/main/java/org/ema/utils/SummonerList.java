package org.ema.utils;

import org.ema.model.business.Summoner;

import java.util.ArrayList;

/**
 * Created by romain on 03/06/2015.
 */
public class SummonerList {

   //Get the min and max performance in a float array [min,max];
    public static float[] getMinAndMAxPerformance(ArrayList<Summoner> summoners) {
        float minValue = 1;
        float maxValue = 0;

        for(Summoner current : summoners) {
            float performance = current.getChampion().getStatistic().getPerformance();
            minValue = Math.min(performance,minValue);
            maxValue = Math.max(performance,maxValue);
        }

        float[] result = new float[2];
        result[0] = minValue;
        result[1] = maxValue;

        return result;
    }
}
