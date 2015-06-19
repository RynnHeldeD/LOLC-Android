package org.ema.utils;

public class GameTimestamp {

    public static long serverTimestamp;
    public static long currentTimeAtStart;

    public static void setGameTimestamp(long serverTimestamp){
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
