/* Copyright � 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette �uvre est prot�g�e par le droit d�auteur et strictement r�serv�e � l�usage priv� du
 * client. Toute reproduction ou diffusion au profit de tiers, � titre
 * gratuit ou on�reux, de
 * tout ou partie de cette �uvre est strictement interdite et constitue une contrefa�on pr�vue
 * par les articles L 335-2 et suivants du Code de la propri�t�
 * intellectuelle. Les ayants-droits se
 * r�servent le droit de poursuivre toute atteinte � leurs droits de
 * propri�t� intellectuelle devant les
 * juridictions civiles ou p�nales.
 */

package org.ema.model.DAO;

import org.ema.model.business.Summoner;
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
