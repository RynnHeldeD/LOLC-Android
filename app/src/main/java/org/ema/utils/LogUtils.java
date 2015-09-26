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

package org.ema.utils;

import android.util.Log;

/**
 * Created by Parreno on 26/09/2015.
 */
public class LogUtils {
    static final boolean LOG = false;
    public static void LOGD(final String tag, String message) {
        if (LOG) {
            Log.d(tag, message);
        }
    }

    public static void LOGV(final String tag, String message) {
        if (LOG) {
            Log.v(tag, message);
        }
    }

    public static void LOGI(final String tag, String message) {
        if (LOG) {
            Log.i(tag, message);
        }
    }

    public static void LOGW(final String tag, String message) {
        if (LOG) {
            Log.w(tag, message);
        }
    }

    public static void LOGE(final String tag, String message) {
        if (LOG) {
            Log.e(tag, message);
        }
    }

}