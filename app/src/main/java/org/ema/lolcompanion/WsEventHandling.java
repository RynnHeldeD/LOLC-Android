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

import java.util.ArrayList;

/**
 * Created by Constantin on 19/05/2015.
 */
public class WsEventHandling {

    public static String waitingReconnexionMessage = "";

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
                        updateChannelPlayers(obj.getJSONArray("allies"));
                        if(obj.has("share")){
                            shareTimers();
                        }
                        break;
                    case "playerList_toOldAllies":
                        updateChannelPlayers(obj.getJSONArray("allies"));
                        break;
                    case "timerDelay":
                        delayTimer(obj.getString("idSortGrille"));
                        break;
                    case "razTimer":
                        //doRestartTimer(obj.getString("idSortGrille"), obj.getString("timestampDeclenchement"));
                        break;
                    case "stopTimer":
                        doStopTimer(obj.getString("idSortGrille"));
                        break;
                    case "sharedTimers":
                        try {
                            updateTimers(obj.getJSONArray("timers"),obj.getString("timestamp"));
                        } catch (JSONException e){
                            Log.v("Websocket","Erreur parsage:" + e.getMessage());
                        }
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
                CompanionActivity.instance.simpleClickTimer(this.buttonIdGrid, this.delayOfTransfert, true, true);
            }
        }

        new Thread(){
            public void run(){
                CompanionActivity.instanceCompanion.runOnUiThread(new WebSocketAction(buttonIdGrid, DoT));
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
                CompanionActivity.instance.simpleClickTimer(buttonIdGrid, 0, true, false);
            }
        }

        new Thread(){
            public void run(){
                CompanionActivity.instanceCompanion.runOnUiThread(new WebSocketAction(buttonIdGrid));
            }
        }.start();
    }

    /**
     * FUNCTION DISABLED
     */
    /*
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
                    CompanionActivity.instance.restartTimer(buttonIdGrid, delayOfTransfert, true);
                } catch (Exception e) {
                    Log.v("Websocket", "Erreur lors du calcul du delay of transfert");
                }
            }
        }

        new Thread(){
            public void run(){
                CompanionActivity.instanceCompanion.runOnUiThread(new WebSocketAction(buttonIdGrid, activationTimestamp));
            }
        }.start();
    }
    */

    public static void doStopTimer(final String buttonIdGrid){
        class WebSocketAction implements Runnable {
            public String buttonIdGrid;

            public WebSocketAction(String buttonIdGrid){
                this.buttonIdGrid = buttonIdGrid;
            }

            public void run() {
                CompanionActivity.instance.stopTimer(this.buttonIdGrid, true);
            }
        }

        new Thread(){
            public void run(){
                CompanionActivity.instanceCompanion.runOnUiThread(new WebSocketAction(buttonIdGrid));
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
                CompanionActivity.instance.stopTimer(buttonIdGrid, true);
            }
        }

        new Thread(){
            public void run(){
                CompanionActivity.instanceCompanion.runOnUiThread(new WebSocketAction(buttonIdGrid));
            }
        }.start();
    }

    public static void updateChannelPlayers(JSONArray playersInChannelJson) {
        Summoner user = (Summoner)GlobalDataManager.get("user");
        ArrayList<Summoner> summonersList = (ArrayList<Summoner>)GlobalDataManager.get("summonersList");

        //on clean les joueurs deja affichés
        class TimerCleanPlayerList implements Runnable {
            public void run(){
                CompanionActivity.instance.cleanChannelSummary();
            }
        }
        new Thread(){
            public void run(){
                CompanionActivity.instanceCompanion.runOnUiThread(new TimerCleanPlayerList());
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
                                CompanionActivity.instance.appendPlayerIconToChannelSummary(iconeName);
                            }
                        }

                        new Thread(){
                            public void run(){
                                CompanionActivity.instanceCompanion.runOnUiThread(new TimerUpdatePlayerList(summonerIconName));
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

        //Ici on envoi le message qui a été envoyé pour se reconnecter
        //On ne peut pas l'envoyer à la reconnexion car il faut attendre d'avoir le nouveau temps serveur
        if(!waitingReconnexionMessage.equals("")) {
            //Si on est en reconnexion, il faut attendre que le serveur se reconnecte pour envoyer le message.
            //Sachant que la connexion ne retourne rien, on est obligé de le mettre ici

                String messageAfterReconnexionModified ="";
                //Comme le message a été envoyé avant la reconnexion, il n'a pas le bon timestamp, on va donc le mettre a jour.
                if(waitingReconnexionMessage.contains("timestampDeclenchement")){
                    try{
                        JSONObject obj = new JSONObject(waitingReconnexionMessage);
                        String oldTimeStamp = obj.getString("timestampDeclenchement");
                         messageAfterReconnexionModified = waitingReconnexionMessage.replace(oldTimeStamp,Long.toString(GameTimestamp.getServerTimestamp()));
                    } catch (JSONException e) {
                        Log.v("Websocket","Erreur : parse : " + e.getMessage());
                    } catch (Exception ex){
                        Log.v("Websocket","Erreur :" + ex.getMessage());
                    }
                }
                //si il n'y a pas eu de bug, on envoi la string modifiée, sinon on ne fait rien pour ne pas envoyer de fausses infos
                if(!messageAfterReconnexionModified.equals("")){
                    WebSocket.send(messageAfterReconnexionModified);
                }
            waitingReconnexionMessage ="";

        }
    }

    public static void shareTimers(){
        String[][] timersTableToShare = CompanionActivity.instance.shareTimers();
        int tableSize = timersTableToShare.length;

        if(tableSize > 0){
            String requestToSend = "{\"action\":\"sentTimers\",\"timers\":[";
            for (int i = 0;i< tableSize;i++){
                requestToSend += "[\"" + timersTableToShare[i][0] + "\",\"" + timersTableToShare[i][1] + "\"],";
            }
            requestToSend = requestToSend.substring(0, requestToSend.length() - 1);

            long serverTime = GameTimestamp.getServerTimestamp();
            requestToSend += "],\"timestamp\":" + serverTime + "}";

            sendMessage(requestToSend);
        }
    }

    public static void updateTimers(JSONArray timerTable,String timestampEnvoi){
        CompanionActivity.instance.cancelAllTimers();

        long delayOfTransfert = GameTimestamp.transfertDelay(Long.parseLong(timestampEnvoi));
        try {
            //HashMap<String,String> timerHaspmap = (HashMap<String,String>) timerTable.get(0);

            for(int i = 0; i < timerTable.length();i++){
                Log.v("Websocket","Update timer " + i);
                JSONArray buttonAndCooldown = (JSONArray) timerTable.get(i);
                if((Long.parseLong(buttonAndCooldown.getString(1)) - delayOfTransfert) > 0) {
                    long cooldown = Long.parseLong(buttonAndCooldown.getString(1)) - delayOfTransfert;
                    CompanionActivity.instance.activateTimer(buttonAndCooldown.getString(0),cooldown);
                }
            }
        } catch (JSONException e){
            Log.v("Websocket","Erreur lors de la reception des timers partagés");
        }

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
