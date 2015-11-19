/* Copyright  2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette oeuvre est protegee par le droit d auteur et strictement reservee a l usage prive du
 * client. Toute reproduction ou diffusion au profit de tiers, a titre
 * gratuit ou onereux, de
 * tout ou partie de cette oeuvre est strictement interdite et constitue une contrefacon prevue
 * par les articles L 335-2 et suivants du Code de la propriete
 * intellectuelle. Les ayants-droits se
 * reservent le droit de poursuivre toute atteinte a leurs droits de
 * propriete intellectuelle devant les
 * juridictions civiles ou penales.
 */

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
