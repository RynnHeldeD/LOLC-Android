package org.ema.utils;

import android.util.Log;

import org.ema.lolcompanion.CompanionActivity;
import org.ema.lolcompanion.WsEventHandling;
import org.ema.model.business.Summoner;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

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

                String userNickname = ((Summoner)GlobalDataManager.get("user")).getName();
                ArrayList<Summoner> summonersList = (ArrayList<Summoner>)GlobalDataManager.get("summonersList");
                String passphrase = CompanionActivity.instance.settingsManager.get(CompanionActivity.instanceCompanion,"passphrase");
                Summoner user = (Summoner)GlobalDataManager.get("user");
                Log.v("Websocket","User: " + user.getName());

                for(Summoner s : summonersList){
                    if(s.getName().equals(userNickname)){
                        user.setTeamId(s.getTeamId());
                        Log.v("Websocket", "User team: " + user.getTeamId());
                        user.getChampion().setIconName(s.getChampion().getIconName());
                        Log.v("Websocket", "User icon: " + user.getChampion().getIconName());
                        user.setGameId(s.getGameId());
                    }
                }
                GlobalDataManager.add("user",user);

                //mWebSocketClient.send("{\"action\":\"pickedChampion\",\"gameId\":\"0\",\"teamId\":\"0\",\"championIconId\":\"0\",\"passphrase\":\""+ passphrase +"\"}");

                mWebSocketClient.send("{\"action\":\"pickedChampion\",\"gameId\":\""+ user.getGameId() +"\",\"teamId\":\"" + user.getTeamId() + "\",\"championIconId\":\""+ user.getChampion().getIconName() +"\",\"passphrase\":\""+ passphrase +"\"}");
                Log.v("Websocket","{\"action\":\"pickedChampion\",\"gameId\":\""+ user.getGameId() +"\",\"teamId\":\"" + user.getTeamId() + "\",\"championIconId\":\""+ user.getChampion().getIconName() +"\",\"passphrase\":\""+ passphrase +"\"}");
            }

            @Override
            public void onMessage(String s) {
                WsEventHandling.handlingMessage(s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.v("Websocket", "Closed :" + s);
                try {
                    CompanionActivity.instanceCompanion.handleDisconnection();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e)  {
                Log.v("Websocket", "Error :" + e.getMessage());
                this.onClose(0, "", true);
            }

        };

        mWebSocketClient.connect();
    }

    public static void send(String s) {
        mWebSocketClient.send(s);
    }
}
