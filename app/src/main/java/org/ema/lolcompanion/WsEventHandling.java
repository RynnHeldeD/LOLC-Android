package org.ema.lolcompanion;

import android.os.SystemClock;
import android.util.Log;

import org.ema.utils.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                        WsEventHandling.switchChannel("toto");
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
        long delayOfTransfert = 0;
        SystemClock.sleep(3000);
        java.util.Date date= new java.util.Date();
        Timestamp tstmp = new Timestamp(date.getTime());

        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(activationTimestamp);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            //on fait la difference entre les deux timestamp, et on a arrondis à la seconde
            delayOfTransfert = tstmp.getTime() - timestamp.getTime();
            Log.v("Websocket","Delay of transfert: " + delayOfTransfert);
        }catch(ParseException e){
            Log.v("Websocket","Erreur: impossible de parser la date reçue:" + e.getMessage());
        }
        TimerActivity.instance.activateTimer(buttonIdGrid,delayOfTransfert);
    }

    public static void delayTimer(String buttonIdGrid) {
        //Pour l'instant, si le timer est en cours, ça enlève les 5 secondes
        //Sinon ça démarre le timer. Ce sera corrigé lors de l'implémentation du ShareTimer
        TimerActivity.instance.activateTimer(buttonIdGrid,0);
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
