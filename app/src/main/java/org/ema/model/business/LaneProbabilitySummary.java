/* Copyright � 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette �uvre est prot�g�e par le droit d�auteur et strictement r�serv�e � l�usage priv� du
 * client. Toute reproduction ou diffusion au profit de tiers, � titre
 * gratuit ou on�reux, de
 * tout ou partie de cette �uvre est strictement interdite et constitue une contrefa�on pr�vue
 * par les articles L 335-2 et suivants du Code de la propri�t�
 * intellectuelle. Les ayants-droits se
 * r�servent le droit de poursuivre toute atteinte � leurs droits de
 * propri�t� intellectuelle devant les
 * juridictions civiles ou p�nales.
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
