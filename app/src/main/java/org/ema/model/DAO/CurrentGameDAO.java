package org.ema.model.DAO;
import android.util.Log;

import org.ema.model.business.Champion;
import org.ema.model.business.League;
import org.ema.model.business.Spell;
import org.ema.model.business.Statistic;
import org.ema.model.business.Summoner;
import org.ema.utils.CallbackMatcher;
import org.ema.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ema.utils.Constant;

import java.util.ArrayList;

public class CurrentGameDAO {
    public static ArrayList<Summoner> getSummunerListInGameFromCurrentUser(Summoner user) {
        //Get request
        String jsonResult = Utils.getDocument(Constant.API_CURRENT_GAME_URI + user.getId());

        try {
            //Not on game
            if(jsonResult == null) {
                return null;
            }

            JSONObject json = new JSONObject(jsonResult);
            JSONArray jsonArray = (JSONArray)json.get("participants");
            ArrayList<Summoner> summonersList = new ArrayList<Summoner>();

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

                //The summoner is the user of the application
                if(user.getId() == (int) (jsonParticipant.get("summonerId"))){
                    user.setSpells(summoner.getSpells());
                    user.setTeamId(summoner.getTeamId());
                    user.setChampion(summoner.getChampion());
                }

                summonersList.add(summoner);
            }

            JSONObject jsonSummonerSpells = new JSONObject(Utils.getDocument(Constant.API_SUMMONER_SPELLS));
            JSONObject jsonChampions = new JSONObject(Utils.getDocument(Constant.API_CHAMPION_URI));

            for(Summoner current : summonersList)
            {
                //Set spell1
                JSONObject jsonSpell1 = (JSONObject)((JSONObject)jsonSummonerSpells.get("data")).get(((Integer)current.getSpells()[0].getId()).toString());
                current.getSpells()[0].setIconName(((JSONObject) jsonSpell1.get("image")).get("full").toString());
                float spellCouldown[] = new float[1];
                spellCouldown[0] = Float.valueOf(jsonSpell1.get("cooldown").toString().replaceAll("\\[", "").replaceAll("\\]",""));
                current.getSpells()[0].setCooldown(spellCouldown);
                new Utils.SetObjectIcon().execute(current.getSpells()[0]);

                //Set spell2
                JSONObject jsonSpell2 = (JSONObject)((JSONObject)jsonSummonerSpells.get("data")).get(((Integer)current.getSpells()[1].getId()).toString());
                current.getSpells()[1].setIconName(((JSONObject) jsonSpell2.get("image")).get("full").toString());
                float spellCouldown2[] = new float[1];
                spellCouldown2[0] = Float.valueOf(jsonSpell2.get("cooldown").toString().replaceAll("\\[", "").replaceAll("\\]",""));
                current.getSpells()[1].setCooldown(spellCouldown2);
                new Utils.SetObjectIcon().execute(current.getSpells()[1]);

                //set champion
                JSONObject championJson = (JSONObject)((JSONObject)jsonChampions.get("data")).get(((Integer)current.getChampion().getId()).toString());
                current.getChampion().setName(championJson.get("name").toString());
                current.getChampion().setAllyTips(championJson.get("allytips").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                current.getChampion().setEnemyTips(championJson.get("enemytips").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                current.getChampion().setIconName(((JSONObject) championJson.get("image")).get("full").toString());
                new Utils.SetObjectIcon().execute(current.getChampion());

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
                new Utils.SetObjectIcon().execute(ultimate);
	        }

            getSummonersRank(summonersList);

            for(Summoner current : summonersList) {
                current.getChampion().setStatistic(getSummonerHistoryStatistic(current));
                calculUserPerformance(current);
            }

            return summonersList;

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void getSummonersRank(ArrayList<Summoner> summoners){
	
	String concatIds = "";
        for(Summoner current : summoners){
            concatIds += current.getId() + ",";
        }
        concatIds = concatIds.substring(0,concatIds.length()-1);
        
	//Get request

        try {
            String request = Constant.API_LEAGUE_URI + concatIds + "/entry";
            JSONObject jsonResult = new JSONObject(Utils.getDocument(Constant.API_LEAGUE_URI + concatIds + "/entry"));

            for(Summoner user : summoners) {
                JSONArray leagueSummonerJSON = jsonResult.getJSONArray(String.valueOf(user.getId()));
                String tier = leagueSummonerJSON.getJSONObject(0).getString("tier") ;
                JSONArray entitiesLeagueJSON = leagueSummonerJSON.getJSONObject(0).getJSONArray("entries");
                int leaguePoints = entitiesLeagueJSON.getJSONObject(0).getInt("leaguePoints");
                tier += " " + entitiesLeagueJSON.getJSONObject(0).getString("division");
                user.setWins(entitiesLeagueJSON.getJSONObject(0).getInt("wins"));
                user.setLooses(entitiesLeagueJSON.getJSONObject(0).getInt("losses"));

                League summonerLeague = new League(tier, null, leaguePoints);
                user.setLeague(summonerLeague);
            }
            //*/
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Statistic getSummonerHistoryStatistic(Summoner user) {
        float kill = 0;
        float death = 0;
        float assist = 0;
        int win = 0;
        int loose = 0;
        JSONObject jsonResult;
        try {
            jsonResult = new JSONObject(Utils.getDocument(Constant.API_MATCH_HISTORY_URI +
                    user.getId() +
                    "?championIds=" +
                    user.getChampion().getId() +
                    "&rankedQueus=RANKED_SOLO_5x5&beginIndex=" + 0 + "&endIndex=" + 10));
            JSONArray jsonMatches = jsonResult.getJSONArray("matches");
            JSONArray jsonParticipants;
            for (int i = 0; i < jsonMatches.length(); i++) {
                jsonParticipants = jsonMatches.getJSONObject(i).getJSONArray("participants");
                kill += jsonParticipants.getJSONObject(0).getJSONObject("stats").getInt("kills");
                death += jsonParticipants.getJSONObject(0).getJSONObject("stats").getInt("deaths");
                assist += jsonParticipants.getJSONObject(0).getJSONObject("stats").getInt("assists");

                if (jsonParticipants.getJSONObject(0).getJSONObject("stats").getBoolean("winner")) {
                    win++;
                } else {
                    loose++;
                }
            }
            int numberOfGames = win + loose;
            kill /= numberOfGames;
            death /= numberOfGames;
            assist /= numberOfGames;
            Statistic statsUser = new Statistic(kill, death, assist, win, loose, (float) 0, (float) 0, (float) 0, null);
            return statsUser;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Erreur stats", e.getMessage());
            Statistic statsUser = new Statistic((float)0, (float)0, (float)0, 0, 0, (float) 0, (float) 0, (float) 0, null);
            return statsUser;
        }
    }

    //Allow to calcul the user performance.
    //Based on 4 elements.
    public static void calculUserPerformance(Summoner summoner){
        float winRateWithChampion = (float)summoner.getChampion().getStatistic().getWin()/((float)summoner.getChampion().getStatistic().getWin()+(float)summoner.getChampion().getStatistic().getLoose());
        float nbGamesWithChampion = Math.min(summoner.getChampion().getStatistic().getWin()+summoner.getChampion().getStatistic().getLoose()/50,1);
        float rank;

        switch (summoner.getLeague().getDivision().split(" ")[0].toString()) {
            case "BRONZE": rank = new Float(0.1);
                break;
            case "SILVER": rank = new Float(0.2);
                break;
            case "GOLD": rank = new Float(0.4);
                break;
            case "PLATINUM": rank = new Float(0.7);
                break;
            case "DIAMOND": rank = new Float(0.8);
                break;
            case "MASTER": rank = new Float(0.9);
                break;
            case "CHALLENGER": rank = new Float(1);
                break;
            default: rank = new Float(0);
                break;
        }

        //Coefficient values / 10
        //Set a ratio between 0 and 1
        summoner.getChampion().getStatistic().setPerformance((winRateWithChampion*(float)2.5+nbGamesWithChampion*(float)2.5+5*rank)/10);
    }
}
