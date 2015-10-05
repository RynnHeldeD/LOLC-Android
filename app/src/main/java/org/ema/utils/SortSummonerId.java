package org.ema.utils;

import org.ema.model.business.Summoner;

import java.util.Comparator;

public class SortSummonerId implements Comparator<Summoner> {
    @Override
    public int compare(Summoner s1, Summoner s2) {
        return (s1.getId() < s2.getId()) == true ? 1 : -1;
    }
}
