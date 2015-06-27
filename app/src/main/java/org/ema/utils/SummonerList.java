package org.ema.utils;

import android.util.Log;

import org.ema.model.business.Summoner;
import org.ema.model.business.SummonerSorted;

import java.util.ArrayList;
import java.util.Collections;

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

    public static boolean areSummenersStatsLoaded(ArrayList<Summoner> summoners) {
        for(Summoner current : summoners) {
            if(!current.getDataProcessed().isStats()) {
                return false;
            }
        }

        return true;
    }

    public static boolean areSummenersRunesLoaded(ArrayList<Summoner> summoners) {
        for(Summoner current : summoners) {
            if(!current.getDataProcessed().isRunes()) {
                return false;
            }
        }

        return true;
    }

    public static boolean areSummenersMasteriesLoaded(ArrayList<Summoner> summoners) {
        for(Summoner current : summoners) {
            if(!current.getDataProcessed().isMasteries()) {
                return false;
            }
        }

        return true;
    }

    public static boolean areSummenersPremadesLoaded(ArrayList<Summoner> summoners) {
        for(Summoner current : summoners) {
            if(!current.getDataProcessed().isPremades()) {
                return false;
            }
        }

        return true;
    }

    public static ArrayList<Summoner> sortSummonersByTeamAndLanes(ArrayList<Summoner> summonersList) {
        ArrayList<Summoner> team100 = new ArrayList<>();
        ArrayList<Summoner> team200 = new ArrayList<>();

        for(Summoner summoner : summonersList) {
            if(summoner.getTeamId() == 100) {
                team100.add(summoner);
            }
            else {
                team200.add(summoner);
            }
        }

        team100 = sortSummonersByLanes(team100);
        team200 = sortSummonersByLanes(team200);

        ArrayList<Summoner> summonersSorted = new ArrayList<>();

        for(Summoner summoner : team100) {
            summonersSorted.add(summoner);
        }
        for(Summoner summoner : team200) {
            summonersSorted.add(summoner);
        }

        return summonersSorted;
    }

    public static ArrayList<Summoner> sortSummonersByLanes(ArrayList<Summoner> summonersList) {
        ArrayList<SummonerSorted> summonersSorted = new ArrayList<>();
        for(Summoner summoner : summonersList) {
            summonersSorted.add(new SummonerSorted(summoner,getPosition(summoner)));
        }

        Summoner[] list = new Summoner[summonersList.size()];
        for(SummonerSorted summoner : summonersSorted) {
            if(summoner.getRank() < list.length) {
                list[summoner.getRank()] = summoner.getSummoner();
                summoner.setFiltred(true);
            }
        }

        for(SummonerSorted summoner : summonersSorted) {
            if(!summoner.isFiltred()) {
                summoner.setFiltred(true);
                list[getNextEmptyIndex(list)] = summoner.getSummoner();
            }
        }

        ArrayList<Summoner> result = new ArrayList<>();
        for(int i = 0; i < list.length;i++) {
            result.add(list[i]);
        }

        return result;
    }

    public static int getPosition(Summoner summoner) {
        for(int i = 0; i < summoner.getChampion().getSummary().length;i++) {
            if(summoner.getChampion().getLane().equals(summoner.getChampion().getSummary()[i].getLane())) {
                return i;
            }
        }

        return 5;
    }

    public static int getNextEmptyIndex(Summoner[] summoners) {
        for(int i =0; i<summoners.length;i++) {
            if(summoners[i] == null) {
                return i;
            }
        }

        return summoners.length;
    }
}
