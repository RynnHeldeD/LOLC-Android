package org.ema.lolcompanion;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.ema.model.business.Summoner;
import org.ema.utils.GlobalDataManager;
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
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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
                        activateTimer(obj.getString("idSortGrille"), obj.getString("timestampDeclenchement"));
                        break;
                    case "playerList":
                         updateChannelPlayers(obj.getJSONArray("allies"));
                        break;
                    case "timerDelay":
                        delayTimer(obj.getString("idSortGrille"));
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

    public static void activateTimer(final String buttonIdGrid,String activationTimestamp){
        Log.v("Websocket", "Timer activation received");
        long delayOfTransfert = 0;

        SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS Z");
        //formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        Timestamp tstmp;
        try {
            tstmp = new Timestamp(formatUTC.parse(formatUTC.format(new Date())).getTime());
        } catch (ParseException e) {
            tstmp = new Timestamp(new Date().getTime());
            Log.v("Websocket","Impossible de parser la current date");
        }

        Timestamp currentTimestamp = Timestamp.valueOf(activationTimestamp);
      /*  try{
            Date parsedDate = formatUTCC.parse(activationTimestamp);
            currentTimestamp = new Timestamp(parsedDate.getTime());
        }catch(ParseException e){
            Log.v("Websocket","Date to parse:" + activationTimestamp);
            Log.v("Websocket","Erreur: impossible de parser la date reçue par le ws:" + e.getMessage());
          //  currentTimestamp = new Timestamp(new Date().getTime());
        }*/

        try {
            Log.v("Websocket","CurrentTimeStamp get time :" + currentTimestamp.getTime());
            //on fait la difference entre les deux timestamp, et on a arrondis à la seconde
            delayOfTransfert = tstmp.getTime() - currentTimestamp.getTime();
            Log.v("Websocket","Delay of transfert: " + delayOfTransfert);
        } catch (Exception e){
            Log.v("Websocket","Erreur lors du calcul du delay of transfert");
        }

        final long DoT = delayOfTransfert;
        class WebSocketAction implements Runnable {
            public String buttonIdGrid;
            public long delayOfTransfert;

            public WebSocketAction(String buttonIdGrid, long delayOfTransfert){
                this.buttonIdGrid = buttonIdGrid;
                this.delayOfTransfert = delayOfTransfert;
            }

            public void run(){
                TimerActivity.instance.simpleClickTimer(this.buttonIdGrid, this.delayOfTransfert, true);
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
                TimerActivity.instance.simpleClickTimer(buttonIdGrid, 0, true);
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
        sendMessage("{\"action\":\"pickedChampion\",\"gameId\":\"" + gameId + "\",\"teamId\":\"" + teamId + "\",\"championIconId\":\"" + championIconName + "\",\"passphrase\":\"" + channel + "\"}");
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

    public static void stopTimer(String gridSpellId) {
      //TODO:
        Log.v("Websocket", "STOP TIMER");
      //  sendMessage("{\"action\":\"razTimer\",\"idSortGrille\":\""+ gridSpellId +"\"}");
    }


}
