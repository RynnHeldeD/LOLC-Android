/* Copyright © 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette œuvre est protégée par le droit d’auteur et strictement réservée à l’usage privé du
 * client. Toute reproduction ou diffusion au profit de tiers, à titre
 * gratuit ou onéreux, de
 * tout ou partie de cette œuvre est strictement interdite et constitue une contrefaçon prévue
 * par les articles L 335-2 et suivants du Code de la propriété
 * intellectuelle. Les ayants-droits se
 * réservent le droit de poursuivre toute atteinte à leurs droits de
 * propriété intellectuelle devant les
 * juridictions civiles ou pénales.
 */

package org.ema.lolcompanion;

import android.graphics.Bitmap;

import org.ema.model.business.Summoner;
import org.ema.utils.GameTimestamp;
import org.ema.utils.GlobalDataManager;
import org.ema.utils.LogUtils;
import org.ema.utils.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Constantin on 19/05/2015.
 */
public class WsEventHandling {

    public static String waitingReconnexionMessage = "";

    public static void handlingMessage(String message) {
        LogUtils.LOGV("Websocket:", "message from websocket: " + message);

        try{

            JSONObject obj = new JSONObject(message);
            boolean error = obj.getBoolean("error");

            if(error) {
                getErrorFromJson(obj);
            } else {

                String action = obj.getString("action");
                LogUtils.LOGV("Websocket", "Action requise: " + action);

                switch (action){
                    case "timer":
                        activateTimer(obj.getString("idSortGrille"), obj.getLong("timestampDeclenchement"));
                        break;
                    case "playerList":
                        startGameTimestamp(obj.getLong("timestamp"));
                        updateChannelPlayersThread(obj.getJSONArray("allies"));
                        break;
                    case "playerList_toNewAllies":
                        updateChannelPlayersThread(obj.getJSONArray("allies"));
                        if(obj.has("share")){
                            shareTimers();
                        }
                        break;
                    case "playerList_toOldAllies":
                        updateChannelPlayersThread(obj.getJSONArray("allies"));
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
                            updateTimers(obj.getJSONArray("timers"),obj.getJSONArray("cdr"),obj.getJSONArray("ultiLevel"),obj.getString("timestamp"));
                        } catch (JSONException e){
                            LogUtils.LOGV("Websocket", "Erreur parsage:" + e.getMessage());
                        }
                        break;
                    case "sharedCooldown":
                        setCdr(obj.getString("champUlti"), obj.getInt("cdr"));
                        break;
                    case "sharedUltimateLevel":
                        setLevelUlti(obj.getString("buttonId"), obj.getInt("ultiLevel"));
                        break;
                    case "requestChampion":
                        pickedChampion();
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            LogUtils.LOGV("Websocket", "Error during message parsing: " + e.getMessage());
        }
    }

    public static void activateTimer(final String buttonIdGrid, Long activationTimestamp){
        long delayOfTransfert = 0;
        delayOfTransfert = GameTimestamp.transfertDelay(activationTimestamp);

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
                    LogUtils.LOGV("Websocket", "Impossible de parser la current date");
                }

                Timestamp currentTimestamp = Timestamp.valueOf(activationTimestamp);

                try {
                    LogUtils.LOGV("Websocket", "CurrentTimeStamp get time :" + currentTimestamp.getTime());
                    final long delayOfTransfert = tstmp.getTime() - currentTimestamp.getTime();
                    CompanionActivity.instance.restartTimer(buttonIdGrid, delayOfTransfert, true);
                } catch (Exception e) {
                    LogUtils.LOGV("Websocket", "Erreur lors du calcul du delay of transfert");
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

    public static void updateChannelPlayersThread(final JSONArray playersInChannelJson) {
        CompanionActivity.instance.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    CompanionActivity.instance.cleanChannelSummary();

                    Summoner user = (Summoner)GlobalDataManager.get("user");
                    ArrayList<Summoner> summonersList = (ArrayList<Summoner>)GlobalDataManager.get("summonersList");

                    //on ajoute chaque joueur
                    for(int i = 0; i<playersInChannelJson.length();i++) {
                        String iconeSummonerName = playersInChannelJson.getString(i);
                        //On ajoute pas l'image du user qui utilie l'appli
                        for(Summoner s : summonersList) {
                            //On passe dans la boucle si ce n'est pas le joueur courant et que c'est un allié
                            if (!s.getName().equals(user.getName()) && s.getTeamId() ==  user.getTeamId() && s.getChampion().getIconName().equals(iconeSummonerName)) {
                                LogUtils.LOGV("Websocket", "On ajoute l'icone du joueur:" + s.getName());
                                final Bitmap summonerIconName = s.getChampion().getIcon();
                                 CompanionActivity.instance.appendPlayerIconToChannelSummary(summonerIconName);
                            }
                        }
                    }
                } catch (JSONException e) {
                    LogUtils.LOGV("Websocket", "Error during message parsing in updateChannelPlayers: " + e.getMessage());
                }
            }
        });
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
                        LogUtils.LOGV("Websocket", "Erreur : parse : " + e.getMessage());
                    } catch (Exception ex){
                        LogUtils.LOGV("Websocket", "Erreur :" + ex.getMessage());
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

        //Sharing current activated timers
        String requestToSend = "{\"action\":\"sentTimers\",\"timers\":[";
        for (int i = 0;i< tableSize;i++){
            requestToSend += "[\"" + timersTableToShare[i][0] + "\",\"" + timersTableToShare[i][1] + "\"],";
        }
        if(tableSize > 0){
            requestToSend = requestToSend.substring(0, requestToSend.length() - 1);
        }

        requestToSend += "],";

        //Sharing CDR
        requestToSend += "\"cdr\":[";

        for(Map.Entry<String,Integer> CDR : CompanionActivity.instance.timerCdrMap.entrySet()){
            requestToSend += "[\"" + CDR.getKey() + "\",\"" + CDR.getValue()  + "\"],";
        }
        //remove the last comma
        requestToSend = requestToSend.substring(0, requestToSend.length() - 1);
        requestToSend += "],";



        //Sharing ultimates level
        requestToSend += "\"ultiLevel\":[";

        for(Map.Entry<String,Integer> ultiLevel : CompanionActivity.instance.timerUltiLvlMap.entrySet()){
            requestToSend += "[\"" + ultiLevel.getKey() + "\",\"" + ultiLevel.getValue()  + "\"],";
        }
        //remove the last comma
        requestToSend = requestToSend.substring(0, requestToSend.length() - 1);
        requestToSend += "],";



        long serverTime = GameTimestamp.getServerTimestamp();
        requestToSend += "\"timestamp\":" + serverTime + "}";
        sendMessage(requestToSend);
    }

    public static void updateTimers(JSONArray timerTable,JSONArray cdrTable,JSONArray ultiLevelTable,String timestampEnvoi){
        CompanionActivity.instance.cancelAllTimers();

        long delayOfTransfert = GameTimestamp.transfertDelay(Long.parseLong(timestampEnvoi));
        try {
            //Updating timers
            for(int i = 0; i < timerTable.length();i++){
                JSONArray buttonAndCooldown = (JSONArray) timerTable.get(i);
                if((Long.parseLong(buttonAndCooldown.getString(1)) - delayOfTransfert) > 0) {
                    long cooldown = Long.parseLong(buttonAndCooldown.getString(1)) - delayOfTransfert;
                    CompanionActivity.instance.activateTimer(buttonAndCooldown.getString(0),cooldown);
                }
            }

            //Update timer cdr hashmap
            for(int i = 0; i < cdrTable.length();i++){
                JSONArray buttonAndCdr = (JSONArray) cdrTable.get(i);
                CompanionActivity.instance.timerCdrMap.put(buttonAndCdr.getString(0),Integer.parseInt(buttonAndCdr.getString(1)));
            }

            //Update timer ulti Level hashmap
            for(int i = 0; i < ultiLevelTable.length();i++){
                JSONArray buttonAndUltiLevel = (JSONArray) ultiLevelTable.get(i);
                //Update in hashmap of ulti level (used to get and set the dialog
                CompanionActivity.instance.timerUltiLvlMap.put(buttonAndUltiLevel.getString(0),Integer.parseInt(buttonAndUltiLevel.getString(1)));
                //Update the hashmap of cooldown (used when a timer is activated)
                CompanionActivity.instance.updateCooldownWithNewUltimateLevel(buttonAndUltiLevel.getString(0),Integer.parseInt(buttonAndUltiLevel.getString(1)));
            }

        } catch (JSONException e){
            LogUtils.LOGV("Websocket", "Erreur lors de la reception des timers partagés");
        }

    }

    public  static void setCdr(String buttonId, Integer cooldown){
        CompanionActivity.instance.setCdr(buttonId,cooldown);
    }

    public  static void setLevelUlti(String buttonId, Integer ultiLevel){
        CompanionActivity.instance.setUltimateLevel(buttonId,ultiLevel);
    }

    public static void getErrorFromJson(JSONObject obj) {
        try {
            LogUtils.LOGV("Websocket", "Error : server return error during action -" + obj.getString("action") + "- message : " + obj.getString("message"));
        } catch (JSONException e) {
            LogUtils.LOGV("Websocket:", "Error : During message parsing error message: " + e.getMessage());
        }
    }


    private static void sendMessage(String msg) {
        WebSocket.send(msg);
    }

    public static void pickedChampion() {
        Summoner user = (Summoner) GlobalDataManager.get("user");
        Integer gameId = user.getGameId();
        Integer teamId = user.getTeamId();
        String championIconName = user.getChampion().getIconName();
        String channel = CompanionActivity.instance.settingsManager.get(CompanionActivity.instance.getActivity(),"passphrase");

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

    public static void sendCdr(String timerButton,Integer cdr){
        sendMessage("{\"action\":\"sentCooldown\",\"champUlti\":\"" + timerButton + "\",\"cdr\":\"" + cdr + "\"}");
    }

    public static void sendUltiLevel(String timerButton,Integer ultiLevel){
        sendMessage("{\"action\":\"shareUltimateLevel\",\"buttonId\":\"" + timerButton + "\",\"ultiLevel\":\"" + ultiLevel + "\"}");
    }

    public static void disconnect() {
        WebSocket.mWebSocketClient.close();
    }


}
