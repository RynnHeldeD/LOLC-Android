package org.ema.lolcompanion;

import android.graphics.Bitmap;
import android.util.Log;

import org.ema.model.business.Summoner;
import org.ema.utils.GameTimestamp;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Constantin on 19/05/2015.
 */
public class WsEventHandling {


    public static void handlingMessage(String message) {
        Log.v("Websocket:", "message from websocket: " + message);

        try{

            JSONObject obj = new JSONObject(message);
            boolean error = obj.getBoolean("error");

            if(error) {
                getErrorFromJson(obj);
            } else {

                String action = obj.getString("action");
                Log.v("Websocket","Action requise: " +  action);

                switch (action){
                    case "timer":
                        activateTimer(obj.getString("idSortGrille"), obj.getLong("timestampDeclenchement"));
                        break;
                    case "playerList":
                        startGameTimestamp(obj.getLong("timestamp"));
                        updateChannelPlayers(obj.getJSONArray("allies"));
                        break;
                    case "playerList_toNewAllies":
                    case "playerList_toOldAllies":
                         updateChannelPlayers(obj.getJSONArray("allies"));
                         break;
                    case "timerDelay":
                        delayTimer(obj.getString("idSortGrille"));
                        break;
                    case "razTimer":
                        doRestartTimer(obj.getString("idSortGrille"), obj.getString("timestampDeclenchement"));
                        break;
                    case "stopTimer":
                        doStopTimer(obj.getString("idSortGrille"));
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            Log.v("Websocket","Error during message parsing: " +e.getMessage());
        }
    }

    public static void activateTimer(final String buttonIdGrid, Long activationTimestamp){
        Log.v("Websocket", "Timer activation received");
        long delayOfTransfert = 0;

        delayOfTransfert = GameTimestamp.transfertDelay(activationTimestamp);
        Log.v("Websocket","Delay of transfert: " + delayOfTransfert);

        final long DoT = delayOfTransfert;
        class WebSocketAction implements Runnable {
            public String buttonIdGrid;
            public long delayOfTransfert;

            public WebSocketAction(String buttonIdGrid, long delayOfTransfert){
                this.buttonIdGrid = buttonIdGrid;
                this.delayOfTransfert = delayOfTransfert;
            }

            public void run(){
                TimerActivity.instance.simpleClickTimer(this.buttonIdGrid, this.delayOfTransfert, true, true);
            }
        }

        new Thread(){
            public void run(){
                TimerActivity.instance.runOnUiThread(new WebSocketAction(buttonIdGrid, DoT));
            }
        }.start();
    }

    public static void delayTimer(final String buttonIdGrid) {
        // TODO
        //Pour l'instant, si le timer est en cours, ça enlève les 5 secondes
        //Sinon ça démarre le timer. Ce sera corrigé lors de l'implémentation du ShareTimer
        class WebSocketAction implements Runnable {
            public String buttonIdGrid;

            public WebSocketAction(String buttonIdGrid){
                this.buttonIdGrid = buttonIdGrid;
            }

            public void run(){
                TimerActivity.instance.simpleClickTimer(buttonIdGrid, 0, true, false);
            }
        }

        new Thread(){
            public void run(){
                TimerActivity.instance.runOnUiThread(new WebSocketAction(buttonIdGrid));
            }
        }.start();
    }

    public static void doRestartTimer(final String buttonIdGrid, final String activationTimestamp){
        class WebSocketAction implements Runnable {
            public String buttonIdGrid;
            public String activationTimestamp;

            public WebSocketAction(String buttonIdGrid, String activationTimestamp){
                this.buttonIdGrid = buttonIdGrid;
                this.activationTimestamp = activationTimestamp;
            }

            public void run() {
                SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS Z");
                Timestamp tstmp;
                try {
                    tstmp = new Timestamp(formatUTC.parse(formatUTC.format(new Date())).getTime());
                } catch (ParseException e) {
                    tstmp = new Timestamp(new Date().getTime());
                    Log.v("Websocket", "Impossible de parser la current date");
                }

                Timestamp currentTimestamp = Timestamp.valueOf(activationTimestamp);

                try {
                    Log.v("Websocket", "CurrentTimeStamp get time :" + currentTimestamp.getTime());
                    final long delayOfTransfert = tstmp.getTime() - currentTimestamp.getTime();
                    TimerActivity.instance.restartTimer(buttonIdGrid, delayOfTransfert, true);
                } catch (Exception e) {
                    Log.v("Websocket", "Erreur lors du calcul du delay of transfert");
                }
            }
        }

        new Thread(){
            public void run(){
                TimerActivity.instance.runOnUiThread(new WebSocketAction(buttonIdGrid, activationTimestamp));
            }
        }.start();
    }

    public static void doStopTimer(final String buttonIdGrid){
        class WebSocketAction implements Runnable {
            public String buttonIdGrid;

            public WebSocketAction(String buttonIdGrid){
                this.buttonIdGrid = buttonIdGrid;
            }

            public void run() {
                TimerActivity.instance.stopTimer(this.buttonIdGrid, true);
            }
        }

        new Thread(){
            public void run(){
                TimerActivity.instance.runOnUiThread(new WebSocketAction(buttonIdGrid));
            }
        }.start();
    }

    public static void cancelTimer(final String buttonIdGrid){
        class WebSocketAction implements Runnable {
            public String buttonIdGrid;

            public WebSocketAction(String buttonIdGrid){
                this.buttonIdGrid = buttonIdGrid;
            }

            public void run(){
                TimerActivity.instance.stopTimer(buttonIdGrid, true);
            }
        }

        new Thread(){
            public void run(){
                TimerActivity.instance.runOnUiThread(new WebSocketAction(buttonIdGrid));
            }
        }.start();
    }

    public static void updateChannelPlayers(JSONArray playersInChannelJson) {
        Summoner user = (Summoner)GlobalDataManager.get("user");
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>)GlobalDataManager.get("summonersList");

        //on clean les joueurs deja affichés
        class TimerCleanPlayerList implements Runnable {
            public void run(){
                TimerActivity.instance.cleanChannelSummary();
            }
        }
        new Thread(){
            public void run(){
                TimerActivity.instance.runOnUiThread(new TimerCleanPlayerList());
            }
        }.start();

        try {
            //on ajoute chaque joueur
            for(int i = 0; i<playersInChannelJson.length();i++) {
                String iconeSummonerName = playersInChannelJson.getString(i);
                //On ajoute pas l'image du user qui utilie l'appli
                for(Summoner s : summonersList) {
                    //On passe dans la boucle si ce n'est pas le joueur courant et que c'est un allié
                    if (!s.getName().equals(user.getName()) && s.getTeamId() ==  user.getTeamId() && s.getChampion().getIconName().equals(iconeSummonerName)) {
                        Log.v("Websocket", "On ajoute l'icone du joueur:" + s.getName());
                        final Bitmap summonerIconName = s.getChampion().getIcon();

                        class TimerUpdatePlayerList implements Runnable {
                            public Bitmap iconeName;

                            public TimerUpdatePlayerList(Bitmap iconeName){
                                this.iconeName = iconeName;
                            }

                            public void run(){
                                TimerActivity.instance.appendPlayerIconToChannelSummary(iconeName);
                            }
                        }

                        new Thread(){
                            public void run(){
                                TimerActivity.instance.runOnUiThread(new TimerUpdatePlayerList(summonerIconName));
                            }
                        }.start();
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            Log.v("Websocket","Error during message parsing in updateChannelPlayers: " +e.getMessage());
        }
    }

    public static void startGameTimestamp(long serverTimestamp) {
        GameTimestamp.setGameTimestamp(serverTimestamp);
    }


    public static void getErrorFromJson(JSONObject obj) {
        try {
            Log.v("Websocket","Error : server return error during action -" + obj.getString("action") + "- message : " + obj.getString("message"));
        } catch (JSONException e) {
            Log.v("Websocket:","Error : During message parsing error message: " +e.getMessage());
        }
    }

    private static void sendMessage(String msg) {
        WebSocket.send(msg);
        Log.v("Websocket","Message send to websocket: " + msg);
    }

    public static void pickedChampion(Integer gameId, Integer teamId, String championIconName, String channel) {
        sendMessage("{\"action\":\"pickedChampion\",\"gameId\":\"" + gameId + "\",\"teamId\":\"" + teamId + "\",\"championIconId\":\"" + championIconName + "\",\"passphrase\":\"" + channel + "\"}");
    }

    public static void switchChannel(String newChannel) {
        sendMessage("{\"action\":\"switchChannel\",\"channel\":\"" + newChannel + "\"}");
    }

    public static void timerActivation(String gridSpellId, String timestampOfTrigger) {
        sendMessage("{\"action\":\"timerActivation\",\"idSortGrille\":\""+ gridSpellId +"\",\"timestampDeclenchement\":\""+ timestampOfTrigger +"\"}");
    }

    public static void timerDelay(String gridSpellId) {
        sendMessage("{\"action\":\"timerDelay\",\"idSortGrille\":\""+ gridSpellId +"\"}");
    }

    public static void restartTimer(String gridSpellId, String timestampOfTrigger) {
        sendMessage("{\"action\":\"razTimer\",\"idSortGrille\":\""+ gridSpellId +"\",\"timestampDeclenchement\":\""+ timestampOfTrigger +"\"}");
    }

    public static void stopTimer(String gridSpellId) {
        sendMessage("{\"action\":\"stopTimer\",\"idSortGrille\":\"" + gridSpellId + "\"}");
    }

    public static void disconnect() {
        WebSocket.mWebSocketClient.close();
    }


}
