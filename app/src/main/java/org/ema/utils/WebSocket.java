package org.ema.utils;

import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import org.ema.lolcompanion.WsEventHandling;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;


import java.net.URI;
import java.net.URISyntaxException;

public class WebSocket {

    public static WebSocketClient mWebSocketClient;

    public static void connectWebSocket() {
        URI uri;
        try {
            //    uri = new URI("ws://10.0.2.2:12345/");
            uri = new URI("ws://5.135.153.45:8080/");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.v("Websocket", "Opened");
                //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                mWebSocketClient.send("{\"action\":\"pickedChampion\",\"gameId\":\"1\",\"teamId\":\"100\",\"championIconId\":\"323\",\"passphrase\":\"\"}");
                Log.v("Websocket","Message send to websocket : {\"action\":\"pickedChampion\",\"gameId\":\"1\",\"teamId\":\"100\",\"championIconId\":\"323\",\"passphrase\":\"\"} ");
            }

            @Override
            public void onMessage(String s) {
                WsEventHandling.handlingMessage(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.v("Websocket", "Closed :" + s);
                System.exit(0);
            }

            @Override
            public void onError(Exception e) {
                Log.v("Websocket", "Error :" + e.getMessage());
            }

        };

        mWebSocketClient.connect();
    }

    public static void send(String s) {
        mWebSocketClient.send(s);
    }
}
