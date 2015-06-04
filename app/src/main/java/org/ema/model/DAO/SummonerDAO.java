package org.ema.model.DAO;
import android.util.JsonReader;
import android.util.Log;

import org.ema.model.business.Summoner;
import org.ema.utils.CallbackMatcher;
import org.ema.utils.Utils;
import org.json.JSONObject;
import org.ema.utils.Constant;

/**
 * Created by romain on 08/05/2015.
 */
public class SummonerDAO {
    public static Summoner getSummoner(String name){
        //Get request
        String jsonResult = Utils.getDocument(Constant.API_SUMMONER_INFO_URI + name.replaceAll(" ","%20"));

        try {
            //Player not exist
            if(jsonResult == null) {
                return null;
            }
            JSONObject json = new JSONObject(jsonResult);
            json = (JSONObject)json.get(name.replaceAll(" ","").toLowerCase());

            //Set summuner fields
            Summoner summoner = new Summoner();
            summoner.setId((int) json.get("id"));
            summoner.setName((String) json.get("name"));
            summoner.setLevel((int) json.get("summonerLevel"));

            return summoner;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isInGame(int id){
        //Get request
        String jsonResult = Utils.getDocument(Constant.API_CURRENT_GAME_URI + id);
        if(jsonResult != null) {
            String gameMode = null;
            try {
                JSONObject inGameResult = new JSONObject(jsonResult);
                gameMode = inGameResult.getString("gameMode");
                return gameMode.equals("CLASSIC");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }
}
