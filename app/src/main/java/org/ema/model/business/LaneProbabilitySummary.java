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
