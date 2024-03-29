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

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/**
 * Created by romain on 01/05/2015.
 */
public class Statistic implements Parcelable {
    private float kill;
    private float death;
    private float assist;
    private int win;
    private int loose;
    private float damageDealtPercentage;
    private float damageTakenPercentage;
    private float performance;
    private double[][] creepChartInfo;

    public float getKill() {
        return kill;
    }

    public void setKill(float kill) {
        this.kill = kill;
    }

    public float getDeath() {
        return death;
    }

    public void setDeath(float death) {
        this.death = death;
    }

    public float getAssist() {
        return assist;
    }

    public void setAssist(float assist) {
        this.assist = assist;
    }

    public int getWin() {
        return win;
    }

    public int getIntPerformance() {
        return Math.round(performance * 100);
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLoose() {
        return loose;
    }

    public void setLoose(int loose) {
        this.loose = loose;
    }

    public float getDamageDealtPercentage() {
        return damageDealtPercentage;
    }

    public void setDamageDealtPercentage(float damageDealtPercentage) {
        this.damageDealtPercentage = damageDealtPercentage;
    }

    public float getDamageTakenPercentage() {
        return damageTakenPercentage;
    }

    public void setDamageTakenPercentage(float damageTakenPercentage) {
        this.damageTakenPercentage = damageTakenPercentage;
    }

    public float getPerformance() {
        return performance;
    }

    public void setPerformance(float performance) {
        this.performance = performance;
    }

    public double[][] getCreepChartInfo() {
        return creepChartInfo;
    }

    public void setCreepChartInfo(double[][] creepChartInfo) {
        this.creepChartInfo = creepChartInfo;
    }

    public Statistic() {
    }

    public Statistic(float kill, float death, float assist, int win, int loose, float damageDealtPercentage, float damageTakenPercentage, float performance, double[][] creepChartInfo) {
        this.kill = kill;
        this.death = death;
        this.assist = assist;
        this.win = win;
        this.loose = loose;
        this.damageDealtPercentage = damageDealtPercentage;
        this.damageTakenPercentage = damageTakenPercentage;
        this.performance = performance;
        this.creepChartInfo = creepChartInfo;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "kill=" + kill +
                ", death=" + death +
                ", assist=" + assist +
                ", win=" + win +
                ", loose=" + loose +
                ", damageDealtPercentage=" + damageDealtPercentage +
                ", damageTakenPercentage=" + damageTakenPercentage +
                ", performance=" + performance +
                ", creepChartInfo=" + Arrays.toString(creepChartInfo) +
                '}';
    }

    // Parcelling part
    public Statistic(Parcel in){
        this.kill = in.readFloat();
        this.death = in.readFloat();
        this.assist = in.readFloat();
        this.win = in.readInt();
        this.loose = in.readInt();
        this.damageDealtPercentage = in.readFloat();
        this.damageTakenPercentage = in.readFloat();
        this.performance = in.readFloat();
        double[][] array;
        int N = 4;
        array = new double[2][N];
        for (int i=0; i<N; i++) {
            array[i] = in.createDoubleArray();
        }
        this.creepChartInfo = array;
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.kill);
        dest.writeFloat(this.death);
        dest.writeFloat(this.assist);
        dest.writeInt(this.win);
        dest.writeInt(this.loose);
        dest.writeFloat(this.damageDealtPercentage);
        dest.writeFloat(this.damageTakenPercentage);
        dest.writeFloat(this.performance);
        final int N = this.creepChartInfo.length;
        for (int i=0; i<N; i++) {
            dest.writeDoubleArray(this.creepChartInfo[i]);
        }
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Statistic createFromParcel(Parcel in) {
            return new Statistic(in);
        }

        public Statistic[] newArray(int size) {
            return new Statistic[size];
        }
    };
}
