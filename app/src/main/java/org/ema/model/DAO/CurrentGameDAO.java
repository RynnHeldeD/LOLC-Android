package org.ema.model.DAO;
import android.util.Log;

import org.ema.model.business.Champion;
import org.ema.model.business.League;
import org.ema.model.business.Spell;
import org.ema.model.business.Statistic;
import org.ema.model.business.Summoner;
import org.ema.utils.CallbackMatcher;
import org.ema.utils.SortChampionsArrayList;
import org.ema.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ema.utils.Constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by romain on 10/05/2015.
 */
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
            ArrayList<Summoner> summonerList = new ArrayList<Summoner>();

            //For each participant
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonParticipant = ((JSONObject)jsonArray.get(i));

                Summoner summoner = new Summoner();

                //Set summoner infos
                summoner.setName(jsonParticipant.get("summonerName").toString());
                summoner.setId((int) (jsonParticipant.get("summonerId")));
                summoner.setTeamId((int) jsonParticipant.get("teamId"));

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

            getSummonersRank(summonerList);

            for(Summoner current : summonerList) {
                //current.getChampion().setStatistic(getSummonerHistoryStatistic(current));
                getStatiscicsAndMostChampionsPlayed(current);
                calculUserPerformance(current);
            }

            //Load images of mostPlayedChampions
            loadMostPlayedChampionsImages(summonerList);

            return summonerList;

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
                if(!jsonResult.isNull(String.valueOf(user.getId()))) {
                    JSONArray leagueSummonerJSON = jsonResult.getJSONArray(String.valueOf(user.getId()));
                    String tier = leagueSummonerJSON.getJSONObject(0).getString("tier");
                    JSONArray entitiesLeagueJSON = leagueSummonerJSON.getJSONObject(0).getJSONArray("entries");
                    int leaguePoints = entitiesLeagueJSON.getJSONObject(0).getInt("leaguePoints");
                    tier += " " + entitiesLeagueJSON.getJSONObject(0).getString("division");
                    user.setWins(entitiesLeagueJSON.getJSONObject(0).getInt("wins"));
                    user.setLooses(entitiesLeagueJSON.getJSONObject(0).getInt("losses"));

                    League summonerLeague = new League(tier, null, leaguePoints);
                    user.setLeague(summonerLeague);
                }
                else{
                    League summonerLeague = new League("Unranked", null, 0);
                    user.setLeague(summonerLeague);
                }
            }
            //*/
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getSummonerHistoryStatistic(Summoner user, JSONArray jsonArray) {
        float kill = 0;
        float death = 0;
        float assist = 0;
        int win = 0;
        int loose = 0;
        JSONObject jsonResult;
        try {

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonChampion = ((JSONObject) jsonArray.get(i));

                if(jsonChampion.getInt("id") == user.getChampion().getId()) {
                    JSONObject jsonStats = (JSONObject)jsonChampion.get("stats");
                    if(!jsonStats.isNull("totalChampionKills")) {
                        kill = jsonStats.getInt("totalChampionKills");
                    }
                    if(!jsonStats.isNull("totalAssists")) {
                        assist = jsonStats.getInt("totalAssists");
                    }
                    if(!jsonStats.isNull("totalDeathsPerSession")) {
                        death = jsonStats.getInt("totalDeathsPerSession");
                    }
                    if(!jsonStats.isNull("totalSessionsWon")) {
                        win = jsonStats.getInt("totalSessionsWon");
                    }
                    if(!jsonStats.isNull("totalSessionsLost")) {
                        loose = jsonStats.getInt("totalSessionsLost");
                    }
                    kill/= (win + loose);
                    assist/= (win + loose);
                    death/= (win + loose);
                }
            }
            Statistic statsUser = new Statistic(kill, death, assist, win, loose, (float) 0, (float) 0, (float) 0, null);
            user.getChampion().setStatistic(statsUser);
            getCreepChartInfo(user);

        } catch (Exception e) {

            e.printStackTrace();
            Log.v("Erreur stats", e.getMessage());
        }
    }

    //Allow to calcul the user performance.
    //Based on 4 elements.
    public static void calculUserPerformance(Summoner summoner){
        //Player is unranked
        if(summoner.getChampion().getStatistic() == null) {
            return;
        }

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

    public static void getCreepChartInfo(Summoner user) {
        JSONObject jsonResult;
        try {
            jsonResult = new JSONObject(Utils.getDocument(Constant.API_MATCH_HISTORY_URI +
                    user.getId() +
                    "?championIds=" +
                    user.getChampion().getId() +
                    "&rankedQueus=RANKED_SOLO_5x5&beginIndex=" + 0 + "&endIndex=" + 10));
            JSONArray jsonMatches = null;
            if(!jsonResult.isNull("matches")) {
                jsonMatches = jsonResult.getJSONArray("matches");

                JSONArray jsonParticipants;
                double zeroToTen, tenToTwenty, twentyToThirty, thirtyToEnd;
                zeroToTen = tenToTwenty = twentyToThirty = thirtyToEnd = 0;
                int numberOfValueZeroToTen, numberOfValueTenToTwenty, numberOfValueTwentyToThirsty, numberOfValueThirtyToEnd;
                numberOfValueZeroToTen = numberOfValueTenToTwenty = numberOfValueTwentyToThirsty = numberOfValueThirtyToEnd = 0;

                for (int i = 0; i < jsonMatches.length(); i++) {
                    jsonParticipants = jsonMatches.getJSONObject(i).getJSONArray("participants");

                    if (!jsonParticipants.getJSONObject(0).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").isNull("zeroToTen")) {
                        zeroToTen += jsonParticipants.getJSONObject(0).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").getDouble("zeroToTen");
                        numberOfValueZeroToTen++;
                    }
                    if (!jsonParticipants.getJSONObject(0).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").isNull("tenToTwenty")) {
                        tenToTwenty += jsonParticipants.getJSONObject(0).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").getDouble("tenToTwenty");
                        numberOfValueTenToTwenty++;
                    }
                    if (!jsonParticipants.getJSONObject(0).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").isNull("twentyToThirty")) {
                        twentyToThirty += jsonParticipants.getJSONObject(0).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").getDouble("twentyToThirty");
                        numberOfValueTwentyToThirsty++;

                    }
                    if (!jsonParticipants.getJSONObject(0).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").isNull("thirtyToEnd")) {
                        thirtyToEnd += jsonParticipants.getJSONObject(0).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").getDouble("thirtyToEnd");
                        numberOfValueThirtyToEnd++;
                    }
                }

                if (numberOfValueZeroToTen != 0) {
                    zeroToTen /= numberOfValueZeroToTen;
                }
                if (numberOfValueTenToTwenty != 0) {
                    tenToTwenty /= numberOfValueTenToTwenty;
                }
                if (numberOfValueTwentyToThirsty != 0) {
                    twentyToThirty /= numberOfValueTwentyToThirsty;
                }
                if (numberOfValueThirtyToEnd != 0) {
                    thirtyToEnd /= numberOfValueThirtyToEnd;
                }
                double[][] creepChartInfo = new double[2][4];
                for (int i = 0; i < 2; i++) {
                    creepChartInfo[i] = new double[4];
                }
                creepChartInfo[0][0] = zeroToTen * 10;
                creepChartInfo[0][1] = tenToTwenty * 10;
                creepChartInfo[0][2] = twentyToThirty * 10;
                creepChartInfo[0][3] = thirtyToEnd * 10;
                for (int i = 1; i < 2; i++) {
                    for (int j = 0; j < 4; j++) {
                        creepChartInfo[i][j] = 100 - creepChartInfo[i - 1][j];
                    }
                }
                user.getChampion().getStatistic().setCreepChartInfo(creepChartInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Erreur creep", e.getMessage());
        }
    }

    //Get most champions played list and Stats
    public static void getStatiscicsAndMostChampionsPlayed(Summoner summoner) {
        String jsonResult = Utils.getDocument(Constant.API_STATS_URI + summoner.getId() + "/ranked?season=SEASON2015");

        try {
            //Not on game
            if (jsonResult == null) {
                return;
            }

            JSONObject json = new JSONObject(jsonResult);
            JSONArray jsonArray = (JSONArray) json.get("champions");
            ArrayList<Champion> allChampionsPlayed = new ArrayList<>();
            getSummonerHistoryStatistic(summoner, jsonArray);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonChampion = ((JSONObject) jsonArray.get(i));

                if(jsonChampion.getInt("id") != 0) {
                    Champion champion = new Champion();
                    champion.setId(jsonChampion.getInt("id"));

                    Statistic stats = new Statistic();
                    JSONObject jsonStats = (JSONObject)jsonChampion.get("stats");
                    stats.setWin(jsonStats.getInt("totalSessionsWon"));
                    stats.setLoose(jsonStats.getInt("totalSessionsLost"));

                    champion.setStatistic(stats);
                    allChampionsPlayed.add(champion);
                }
            }

            Collections.sort(allChampionsPlayed, new SortChampionsArrayList());

            Champion[] mostChampionsPlayed = new Champion[Math.min(3,allChampionsPlayed.size())];
            for(int j = 0; j < mostChampionsPlayed.length; j++) {
                mostChampionsPlayed[j] = allChampionsPlayed.get(j);
            }

            summoner.setMostChampionsPlayed(mostChampionsPlayed);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    //Load async images
    public static void loadMostPlayedChampionsImages(ArrayList<Summoner> summoners) {
        try {
            JSONObject jsonChampions = new JSONObject(Utils.getDocument(Constant.API_CHAMPION_URI));

            for(Summoner current : summoners)
            {
                if(current.getMostChampionsPlayed() != null) {
                    for(int i = 0; i < current.getMostChampionsPlayed().length; i++) {
                        //set champion
                        JSONObject championJson = (JSONObject) ((JSONObject)jsonChampions.get("data")).get(((Integer)current.getMostChampionsPlayed()[i].getId()).toString());
                        current.getMostChampionsPlayed()[i].setName(championJson.get("name").toString());
                        current.getMostChampionsPlayed()[i].setIconName(((JSONObject) championJson.get("image")).get("full").toString());
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
