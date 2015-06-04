package org.ema.lolcompanion;

import android.util.Log;

import org.ema.utils.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Constantin on 19/05/2015.
 */
public class WsEventHandling {

    private static ArrayList<String> playersInChannel_g;

    public static void handlingMessage(String message) {
        Log.v("Websocket:","message from websocket: " + message);

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
                        activateTimer(obj.getString("idSortGrille"), obj.getString("timestampDeclenchement"));
                        break;
                    case "playerList":
                        break;
                    case "timerDelay":
                        break;
                    case "razTimer":
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            Log.v("Websocket","Error during message parsing: " +e.getMessage());
        }
    }

    public static void activateTimer(String buttonIdGrid,String activationTimestamp){
        Log.v("Websocket","Timer activation received");
        TimerActivity.instance.activateTimer(buttonIdGrid);
    }

    public static void firstMessage(JSONArray playersInChannel) {
        updateChannelPlayers(playersInChannel);

        //Lancement du timer pour les 5 minutes
       // TimerTask tasknew = new TimerGetTimeStamp();
        Timer timer = new Timer();

        Log.v("Websocket","Timer Starting");
        // scheduling the task at interval
        //300000ms = 5 minutes
        //timer.schedule(tasknew, 300000);
        Log.v("Websocket","Timer Ending");
    }

    public static void updateChannelPlayers(JSONArray playersInChannel) {

        //on recréer la liste des joueurs du channel
        playersInChannel_g = new ArrayList<String>();

        try {
            //on ajoute chaque joueur
            for(int i = 0; i<playersInChannel.length();i++) {
                playersInChannel_g.add(i, playersInChannel.getString(i));
            }
        } catch (JSONException e) {
            Log.v("Websocket","Error during message parsing in updateChannelPlayers: " +e.getMessage());
        }

        //Récupération des images des joueurs


        //Ajout des images à la vue


    }

   /* class TimerGetTimeStamp extends TimerTask {
        public void run() {
            System.out.println("Timer task executed.");
        }
    }*/

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
        sendMessage("{\"action\":\"pickedChampion\",\"gameId\":\""+ gameId +"\",\"teamId\":\""+ teamId +"\",\"championIconId\":\""+ championIconName +"\",\"passphrase\":\""+ channel +"\"}");
    }

    public static void switchChannel(String newChannel) {
        sendMessage("{\"action\":\"switchChannel\",\"channel\":\""+ newChannel +"\"}");
    }

    public static void timerActivation(String gridSpellId, String timestampOfTrigger) {
        sendMessage("{\"action\":\"timerActivation\",\"idSortGrille\":\""+ gridSpellId +"\",\"timestampDeclenchement\":\""+ timestampOfTrigger +"\"}");
    }

    public static void timerDelay(String gridSpellId) {
        sendMessage("{\"action\":\"timerDelay\",\"idSortGrille\":\""+ gridSpellId +"\"}");
    }

    public static void resetTimer(String gridSpellId) {
        sendMessage("{\"action\":\"razTimer\",\"idSortGrille\":\""+ gridSpellId +"\"}");
    }


}
