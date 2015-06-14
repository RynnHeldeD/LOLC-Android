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

package org.ema.model.DAO;
import android.util.JsonReader;
import android.util.Log;

import org.ema.model.business.Summoner;
import org.ema.utils.CallbackMatcher;
import org.ema.utils.Utils;
import org.json.JSONObject;
import org.ema.utils.Constant;

import java.net.URLEncoder;

/**
 * Created by romain on 08/05/2015.
 */
public class SummonerDAO {
    public static Summoner getSummoner(String name){
        try {
            //Get request
            String jsonResult = Utils.getDocument(Constant.API_SUMMONER_INFO_URI + URLEncoder.encode(name,"UTF-8"));

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
