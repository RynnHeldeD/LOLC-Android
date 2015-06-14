/* Copyright © 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette œuvre est protégée par le droit d’auteur et strictement réservée à l’usage privé du
 * client. Toute reproduction ou diffusion au profit de tiers, à titre
 * gratuit ou onéreux, de
 * tout ou partie de cette œuvre est strictement interdite et constitue une contrefaçon prévue
 * par les articles L 335-2 et suivants du Code de la propriété
 * intellectuelle. Les ayants-droits se
 * réservent le droit de poursuivre toute atteinte à leurs droits de
 * propriété intellectuelle devant les
 * juridictions civiles ou pénales.
 */

package org.ema.model.business;

/**
 * Created by romain on 08/06/2015.
 */
public class LaneProbabilitySummary {
    private LanesEnum lane;
    private float proba;

    public LanesEnum getLane() {
        return lane;
    }

    public void setLane(LanesEnum lane) {
        this.lane = lane;
    }

    public float getProba() {
        return proba;
    }

    public void setProba(float proba) {
        this.proba = proba;
    }

    public LaneProbabilitySummary(LanesEnum lane, float proba) {
        this.lane = lane;
        this.proba = proba;
    }
}
