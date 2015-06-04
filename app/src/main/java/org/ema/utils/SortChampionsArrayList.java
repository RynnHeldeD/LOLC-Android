package org.ema.utils;

import org.ema.model.business.Champion;

import java.util.Comparator;

/**
 * Created by romain on 03/06/2015.
 */
public class SortChampionsArrayList implements Comparator<Champion> {
    @Override
    public int compare(Champion champion1, Champion champion2) {
        return ((champion1.getStatistic().getWin() + champion1.getStatistic().getLoose()) < (champion2.getStatistic().getWin() + champion2.getStatistic().getLoose())) ? 1 : -1;
    }
}
