package org.ema.utils;

import org.ema.model.business.Summoner;

import java.util.Comparator;

public class SortSummonerByTeamAndPerf implements Comparator<Summoner> {
    @Override
    public int compare(Summoner s1, Summoner s2) {
        if(s1.getTeamId() != s2.getTeamId()) {
            return (s1.getTeamId() > s2.getTeamId()) ? 1 : -1;
        }
        else if(s1.getChampion().getStatistic() == null && s2.getChampion().getStatistic() == null) {
            return 0;
        }
        else if(s1.getChampion().getStatistic() == null) {
            return 1;
        }
        else if(s2.getChampion().getStatistic() == null) {
            return -1;
        }
        else if (s1.getChampion().getStatistic().getPerformance() < s2.getChampion().getStatistic().getPerformance()){
            return 1;
        }
        else if (s1.getChampion().getStatistic().getPerformance() > s2.getChampion().getStatistic().getPerformance()){
            return -1;
        }
        else {
            return 0;
        }
    }
}
