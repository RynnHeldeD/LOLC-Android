package org.ema.model.business;

/**
 * Created by romain on 08/06/2015.
 */
public class SummonerSorted {
    private Summoner summoner;
    private int rank;
    private boolean filtred = false;

    public boolean isFiltred() {
        return filtred;
    }

    public void setFiltred(boolean filtred) {
        this.filtred = filtred;
    }

    public Summoner getSummoner() {
        return summoner;
    }

    public void setSummoner(Summoner summoner) {
        this.summoner = summoner;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public SummonerSorted(Summoner summoner, int rank) {
        this.summoner = summoner;
        this.rank = rank;
    }
}
