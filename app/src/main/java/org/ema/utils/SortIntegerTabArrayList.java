package org.ema.utils;

import java.util.Comparator;

/**
  * Created by eddy on 6/4/2015.
  */
 public class SortIntegerTabArrayList implements Comparator<int[]> {
    @Override
    public int compare(int[] tab1, int[] tab2) {
        return (tab1[1] < tab2[1]) ? 1 : -1;
    }
 }
