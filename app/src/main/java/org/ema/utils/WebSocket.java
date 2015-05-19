package org.ema.utils;

import android.os.Build;
import android.util.Log;

import org.ema.lolcompanion.WsEventHandling;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;


import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Constantin on 19/05/2015.
 */
public class WebSocket {
    public WebSocketClient mWebSocketClient;

    public void connectWebSocket() {
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
                Log.i("Websocket", "Opened");
                //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                mWebSocketClient.send("{\"action\":\"pickedChampion\",\"gameId\":\"1\",\"teamId\":\"100\",\"championIconId\":\"323\",\"passphrase\":\"\"}");
                Log.i("Debug","Message send to websocket : {\"action\":\"pickedChampion\",\"gameId\":\"1\",\"teamId\":\"100\",\"championIconId\":\"323\",\"passphrase\":\"\"} ");
            }

            @Override
            public void onMessage(String s) {
                WsEventHandling wsEventHandling = new WsEventHandling(this);
                wsEventHandling.handlingMessage(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed :" + s);
                System.exit(0);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error :" + e.getMessage());
            }

        };
        mWebSocketClient.connect();
    }






}
