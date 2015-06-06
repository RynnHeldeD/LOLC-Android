package org.ema.utils;

import android.util.Log;

public class GameTimestamp {

    public static long serverTimestamp;
    public static long currentTimeAtStart;

    public static void setGameTimestamp(long serverTimestamp){
        Log.v("Websocket","On set le game timestamp");
        GameTimestamp.serverTimestamp = serverTimestamp;
        GameTimestamp.currentTimeAtStart = System.currentTimeMillis();
    }

    public static long transfertDelay(long activationTimer){
        long serverTime = System.currentTimeMillis() - currentTimeAtStart + serverTimestamp;
        return serverTime - activationTimer;
    }

    public static long getServerTimestamp() {
        return System.currentTimeMillis() - currentTimeAtStart + serverTimestamp;
    }

}
