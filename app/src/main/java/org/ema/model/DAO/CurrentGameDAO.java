package org.ema.model.DAO;
import android.util.Log;

import org.ema.model.business.Champion;
import org.ema.model.business.League;
import org.ema.model.business.Spell;
import org.ema.model.business.Summoner;
import org.ema.utils.CallbackMatcher;
import org.ema.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ema.utils.Constant;

import java.util.ArrayList;

public class CurrentGameDAO {
    public static ArrayList<Summoner> getSummonerListInGameFromCurrentUser(Summoner user) {
        //Get request
        String jsonResult = Utils.getDocument(Constant.API_CURRENT_GAME_URI + user.getId());

        try {
            //Not on game
            if(jsonResult == null) {
                return null;
            }

            JSONObject json = new JSONObject(jsonResult);
            JSONArray jsonArray = (JSONArray)json.get("participants");
            ArrayList<Summoner> summonerList = new ArrayList<Summoner>();

            //For each participant
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonParticipant = ((JSONObject)jsonArray.get(i));

                Summoner summoner = new Summoner();

                //Set summoner infos
                summoner.setName(jsonParticipant.get("summonerName").toString());
                summoner.setId((int) (jsonParticipant.get("summonerId")));
                summoner.setTeamId((int) jsonParticipant.get("teamId"));
                //TODO: Launch async task to set more info about summoner

                //Set summoner championId
                Champion champion = new Champion();
                champion.setId((int) jsonParticipant.get("championId"));
                summoner.setChampion(champion);

                //Set summoner spells
                Spell[] spells = new Spell[2];
                spells[0] = new Spell((int) jsonParticipant.get("spell1Id"),"",null,null);
                spells[1] = new Spell((int) jsonParticipant.get("spell2Id"),"",null,null);
                summoner.setSpells(spells);

                //The summuner is the user of the application
                if(user.getId() == (int) (jsonParticipant.get("summonerId"))){
                    user.setSpells(summoner.getSpells());
                    user.setTeamId(summoner.getTeamId());
                    user.setChampion(summoner.getChampion());
                }

                summonerList.add(summoner);
            }

            JSONObject jsonSummonerSpells = new JSONObject(Utils.getDocument(Constant.API_SUMMONER_SPELLS));
            JSONObject jsonChampions = new JSONObject(Utils.getDocument(Constant.API_CHAMPION_URI));

            for(Summoner current : summonerList)
            {
                //Set spell1
                JSONObject jsonSpell1 = (JSONObject)((JSONObject)jsonSummonerSpells.get("data")).get(((Integer)current.getSpells()[0].getId()).toString());
                current.getSpells()[0].setIconName(((JSONObject) jsonSpell1.get("image")).get("full").toString());
                float spellCouldown[] = new float[1];
                spellCouldown[0] = Float.valueOf(jsonSpell1.get("cooldown").toString().replaceAll("\\[", "").replaceAll("\\]",""));
                current.getSpells()[0].setCooldown(spellCouldown);

                //Set spell2
                JSONObject jsonSpell2 = (JSONObject)((JSONObject)jsonSummonerSpells.get("data")).get(((Integer)current.getSpells()[1].getId()).toString());
                current.getSpells()[1].setIconName(((JSONObject) jsonSpell2.get("image")).get("full").toString());
                float spellCouldown2[] = new float[1];
                spellCouldown2[0] = Float.valueOf(jsonSpell2.get("cooldown").toString().replaceAll("\\[", "").replaceAll("\\]",""));
                current.getSpells()[1].setCooldown(spellCouldown2);

                //set champion
                JSONObject championJson = (JSONObject)((JSONObject)jsonChampions.get("data")).get(((Integer)current.getChampion().getId()).toString());
                current.getChampion().setName(championJson.get("name").toString());
                current.getChampion().setAllyTips(championJson.get("allytips").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                current.getChampion().setEnemyTips(championJson.get("enemytips").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                current.getChampion().setIconName(((JSONObject) championJson.get("image")).get("full").toString());
                new Utils.SetIconFromUrl().execute(current.getChampion());

                JSONObject jsonUltimateSpell = (JSONObject)((JSONArray) championJson.get("spells")).get(3);
                Spell ultimate = new Spell();
                ultimate.setIconName(((JSONObject) jsonUltimateSpell.get("image")).get("full").toString());
                String spellsStr[] = jsonUltimateSpell.get("cooldown").toString().replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                float cooldowns[] = new float[spellsStr.length];
                for(int i = 0; i < spellsStr.length; i++){
                    cooldowns[i] = Float.valueOf(spellsStr[i]);
                }
                ultimate.setCooldown(cooldowns);
                current.getChampion().setSpell(ultimate);
            }

            return summonerList;

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static League getSummonerRank(Summoner user){
        //Get request
        try {
            JSONObject jsonResult = new JSONObject(Utils.getDocument(Constant.API_LEAGUE_URI + user.getId() + "/entry"));

            JSONArray leagueSummonerJSON = jsonResult.getJSONArray(String.valueOf(user.getId()));
            String tier = leagueSummonerJSON.getJSONObject(0).getString("tier") ;
            JSONArray entitiesLeagueJSON = leagueSummonerJSON.getJSONObject(0).getJSONArray("entries");
            int leaguePoints = entitiesLeagueJSON.getJSONObject(0).getInt("leaguePoints");
            tier += " " + entitiesLeagueJSON.getJSONObject(0).getString("division");
            user.setWinPercentage(entitiesLeagueJSON.getJSONObject(0).getInt("wins"));
            user.setLoosePercentage(entitiesLeagueJSON.getJSONObject(0).getInt("losses"));

            League summonerLeague = new League(tier, null, leaguePoints);
            //user.setLeague(summonerLeague);
            Log.v("League Summoner", "test");
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
