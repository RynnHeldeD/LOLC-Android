package org.ema.utils;

import android.util.Log;

import org.ema.lolcompanion.CompanionActivity;
import org.ema.lolcompanion.WsEventHandling;
import org.ema.model.business.Summoner;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class WebSocket {

    public static WebSocketClient mWebSocketClient;
    public static Boolean alreadyDisconnected = false;



    public static void connectWebSocket() {
        URI uri;
        try {
            //    uri = new URI("ws://10.0.2.2:12345/");
            uri = new URI("ws://5.135.153.45:8080/");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.v("Websocket", "Fail to connect to websocket");
            return;
        }
    try {
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.v("Websocket", "Opened");

                if (alreadyDisconnected) {
                    try{
                        CompanionActivity.instanceCompanion.reconnectionNotification();
                    } catch (Exception e){
                        Log.v("Websocket","Erreur on Websocket open :" + e.getMessage());
                    }
                }

                String userNickname = ((Summoner) GlobalDataManager.get("user")).getName();
                ArrayList<Summoner> summonersList = (ArrayList<Summoner>) GlobalDataManager.get("summonersList");
                String passphrase = CompanionActivity.instance.settingsManager.get(CompanionActivity.instanceCompanion, "passphrase");

                Summoner user = (Summoner) GlobalDataManager.get("user");

                for (Summoner s : summonersList) {
                    if (s.getName().equals(userNickname)) {
                        user.setTeamId(s.getTeamId());
                        user.getChampion().setIconName(s.getChampion().getIconName());
                        user.setGameId(s.getGameId());
                    }
                }
                GlobalDataManager.add("user", user);

                //mWebSocketClient.send("{\"action\":\"pickedChampion\",\"gameId\":\"0\",\"teamId\":\"0\",\"championIconId\":\"0\",\"passphrase\":\""+ passphrase +"\"}");

                mWebSocketClient.send("{\"action\":\"pickedChampion\",\"gameId\":\"" + user.getGameId() + "\",\"teamId\":\"" + user.getTeamId() + "\",\"championIconId\":\"" + user.getChampion().getIconName() + "\",\"passphrase\":\"" + passphrase + "\"}");
                Log.v("Websocket", "{\"action\":\"pickedChampion\",\"gameId\":\"" + user.getGameId() + "\",\"teamId\":\"" + user.getTeamId() + "\",\"championIconId\":\"" + user.getChampion().getIconName() + "\",\"passphrase\":\"" + passphrase + "\"}");

            }

            @Override
            public void onMessage(String s) {
                WsEventHandling.handlingMessage(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.v("Websocket", "Closed :" + s);
                try{
                    CompanionActivity.instanceCompanion.handleDisconnection();
                } catch (NullPointerException ex){
                    Log.v("Websocket","Erreur dans le try catch du websocketOnError" + ex.getMessage());
                }

            }

            @Override
            public void onError(Exception e) {
                Log.v("Websocket", "Error :" + e.getMessage());
              /*  try{
                    CompanionActivity.instanceCompanion.handleDisconnection();
                } catch (NullPointerException ex){
                    Log.v("Websocket","Erreur dans le try catch du websocketOnError " + ex.getMessage());
                }*/
            }

        };
    } catch (Exception ex){
        Log.v("Websocket","Timeout ? :" + ex.getMessage());
    }
        mWebSocketClient.connect();
    }

    public static void send(String s) {
        try{
            mWebSocketClient.send(s);
            Log.v("Websocket","Message send to websocket: " + s);
        } catch (WebsocketNotConnectedException e){
            try {
                connectWebSocket();
                WsEventHandling.waitingReconnexionMessage = s;
            }catch (WebsocketNotConnectedException er){
                Log.v("Websocket","Erreur lors de l'envoi du message: " + e.getMessage());
                Log.v("Websocket","La tentative de reconnexion au serveur WS a echoué");
            }
        }
    }
}
