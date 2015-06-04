package org.ema.utils;

import org.ema.model.business.Summoner;

import java.util.ArrayList;

/**
 * Created by romain on 03/06/2015.
 */
public class SummonerList {

   //Get the min and max performance in a float array [min,max];
    public static int[] getMinAndMAxPerformance(ArrayList<Summoner> summoners) {
        int minValue = 100;
        int maxValue = 0;

        for(Summoner current : summoners) {
            if(current.getChampion().getStatistic() != null) {
                int performance = current.getChampion().getStatistic().getIntPerformance();
                minValue = Math.min(performance,minValue);
                maxValue = Math.max(performance,maxValue);
            }
        }

        int[] result = new int[2];
        result[0] = minValue;
        result[1] = maxValue;

        return result;
    }
}
