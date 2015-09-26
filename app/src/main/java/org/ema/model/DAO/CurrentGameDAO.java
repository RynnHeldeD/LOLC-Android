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
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import org.ema.model.business.Champion;
import org.ema.model.business.LaneProbability;
import org.ema.model.business.League;
import org.ema.model.business.Mastery;
import org.ema.model.business.Rune;
import org.ema.model.business.Spell;
import org.ema.model.business.Statistic;
import org.ema.model.business.Summoner;
import org.ema.model.business.Item;
import org.ema.utils.Constant;
import org.ema.utils.SortChampionsArrayList;
import org.ema.utils.SortIntegerTabArrayList;
import org.ema.utils.SortSummonerByTeamAndPerf;
import org.ema.utils.SummonerList;
import org.ema.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

public class CurrentGameDAO {

    public static int numberOfGamesAnalyzed = 10;
    public static void loadStatisticsDetailed(Summoner summoner) {
        if(!summoner.getDataProcessed().isDetailedStats()) {
            //Load images of mostPlayedChampions
            loadMostPlayedChampionsImages(summoner);

            JSONArray matchHistory = getMatchHistory(summoner, numberOfGamesAnalyzed);
            if(matchHistory != null ) {
                getSummonerFavoriteBuild(summoner, matchHistory);
                getCreepChartInfo(summoner, matchHistory);
            }

            while(!summoner.areImagesMostPlayedChampionsLoaded() || !summoner.getChampion().areImagesBuildLoaded()){
                SystemClock.sleep(500);
            }

            summoner.getDataProcessed().setDetailedStats(true);
        }
    }

    public static ArrayList<Mastery> getSummunerMasteries(Summoner summuner, JSONArray jsonMasteries) {
        ArrayList<Mastery> masteries = new ArrayList<>();

        for(int i = 0; i < jsonMasteries.length(); i++) {
            try {
                JSONObject jsonMastery = jsonMasteries.getJSONObject(i);

                Mastery mastery = new Mastery();
                mastery.setId(jsonMastery.getInt("masteryId"));
                mastery.setRank(jsonMastery.getInt("rank"));
                mastery.setIconUrl(mastery.getId() + ".png");

                masteries.add(mastery);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }


        }
            return masteries;
    }

    public static ArrayList<Rune> getSummunerRunes(Summoner summuner, JSONArray jsonRunes) {
        ArrayList<Rune> runes = new ArrayList<>();

        for(int i = 0; i < jsonRunes.length(); i++) {
            try {
                JSONObject jsonRune = jsonRunes.getJSONObject(i);

                Rune rune = new Rune();
                rune.setId(jsonRune.getInt("runeId"));
                rune.setCount(jsonRune.getInt("count"));
                rune.setIconUrl(rune.getId() + ".png");
                runes.add(rune);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return runes;
    }

    public static ArrayList<Summoner> getSummunerListInGameFromCurrentUser(Summoner user) {
        //Get request
        String jsonResult = Utils.getDocumentAndCheck(Constant.API_CURRENT_GAME_URI + user.getId(),2);
        try {
            //Not on game
            if(jsonResult == null) {
                return null;
            }

            JSONObject json = new JSONObject(jsonResult);
            JSONArray jsonArray = (JSONArray)json.get("participants");
            int gameId = json.getInt("gameId");

            ArrayList<Summoner> summonersList = new ArrayList<Summoner>();

            //For each participant
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonParticipant = ((JSONObject)jsonArray.get(i));

                Summoner summoner = new Summoner();

                //Set summoner infos
                summoner.setName(jsonParticipant.get("summonerName").toString());
                summoner.setId((int) (jsonParticipant.get("summonerId")));
                summoner.setTeamId((int) jsonParticipant.get("teamId"));
                summoner.setGameId(gameId);
                //TODO: Launch async task to set more info about summoner

                //Set summoner championId
                Champion champion = new Champion();
                champion.setId((int) jsonParticipant.get("championId"));
                summoner.setChampion(champion);

                //Set summoner spells
                Spell[] spells = new Spell[2];
                spells[0] = new Spell((int) jsonParticipant.get("spell1Id"),"",null,null);
                spells[1] = new Spell((int) jsonParticipant.get("spell2Id"),"",null,null);

                //This is the flash
                if(spells[0].getId() == 4) {
                    Spell spell = spells[1];
                    spells[1] = spells[0];
                    spells[0] = spell;
                }
                summoner.setSpells(spells);

                //The summoner is the user of the application
                if(user.getId() == (int) (jsonParticipant.get("summonerId"))){
                    user.setSpells(summoner.getSpells());
                    user.setTeamId(summoner.getTeamId());
                    user.setChampion(summoner.getChampion());
                }

                //get runes
                summoner.setRunes(getSummunerRunes(summoner, jsonParticipant.getJSONArray("runes")));

                //get masteries
                summoner.setMasteries(getSummunerMasteries(summoner, jsonParticipant.getJSONArray("masteries")));

                summonersList.add(summoner);
            }

            //Async thread to get detailles runes and masteries (images, descriptions, effects)
            new Utils.getRunesDetailled().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, summonersList);
            new Utils.getMasteriesDetailled().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, summonersList);

            getLevels(summonersList);

            JSONObject jsonSummonerSpells = new JSONObject(Utils.getDocumentAndCheck(Constant.API_SUMMONER_SPELLS,2));
            JSONObject jsonChampions = new JSONObject(Utils.getDocumentAndCheck(Constant.API_CHAMPION_URI,2));

            for(Summoner current : summonersList)
            {
                //Set spell1
                JSONObject jsonSpell1 = (JSONObject)((JSONObject)jsonSummonerSpells.get("data")).get(((Integer)current.getSpells()[0].getId()).toString());
                current.getSpells()[0].setIconName(((JSONObject) jsonSpell1.get("image")).get("full").toString());
                float spellCouldown[] = new float[1];
                spellCouldown[0] = Float.valueOf(jsonSpell1.get("cooldown").toString().replaceAll("\\[", "").replaceAll("\\]",""));
                current.getSpells()[0].setCooldown(spellCouldown);
                new Utils.SetObjectIcon().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, current.getSpells()[0]);

                //Set spell2
                JSONObject jsonSpell2 = (JSONObject)((JSONObject)jsonSummonerSpells.get("data")).get(((Integer)current.getSpells()[1].getId()).toString());
                current.getSpells()[1].setIconName(((JSONObject) jsonSpell2.get("image")).get("full").toString());
                float spellCouldown2[] = new float[1];
                spellCouldown2[0] = Float.valueOf(jsonSpell2.get("cooldown").toString().replaceAll("\\[", "").replaceAll("\\]",""));
                current.getSpells()[1].setCooldown(spellCouldown2);
                new Utils.SetObjectIcon().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, current.getSpells()[1]);

                //set champion
                JSONObject championJson = (JSONObject)((JSONObject)jsonChampions.get("data")).get(((Integer)current.getChampion().getId()).toString());
                current.getChampion().setName(championJson.get("name").toString());
                current.getChampion().setAllyTips(championJson.get("allytips").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                current.getChampion().setEnemyTips(championJson.get("enemytips").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                current.getChampion().setIconName(((JSONObject) championJson.get("image")).get("full").toString());
                new Utils.SetObjectIcon().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, current.getChampion());

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
                new Utils.SetObjectIcon().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ultimate);
	        }

            getSummonersRank(summonersList);

            for(Summoner current : summonersList) {
                new Utils.getPremades().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, current, summonersList);
                new Utils.getStats().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, current);
            }

            while(!SummonerList.areSummenersPremadesLoaded(summonersList) ||
                  !SummonerList.areSummenersStatsLoaded(summonersList) ||
                  !SummonerList.areSummenersRunesLoaded(summonersList) ||
                  !SummonerList.areSummenersMasteriesLoaded(summonersList)) {
                SystemClock.sleep(500);
            }

            for(Summoner current : summonersList) {
                current.getCooldownPerLevelAndCalculCooldowns();
                calculUserPerformance(current);
            }

            calculLanesProbabilities(summonersList);

            Collections.sort(summonersList, new SortSummonerByTeamAndPerf());

            summonersList = SummonerList.sortSummonersByTeamAndLanes(summonersList);

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
            JSONObject jsonResult = new JSONObject(Utils.getDocumentAndCheck(Constant.API_LEAGUE_URI + concatIds + "/entry",2));

            for(Summoner user : summoners) {
                if(!jsonResult.isNull(String.valueOf(user.getId())) && (jsonResult.getJSONArray(String.valueOf(user.getId())).getJSONObject(0).getString("queue").equals("RANKED_SOLO_5x5"))) {
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
            JSONArray matchHistory = getMatchHistory(user, numberOfGamesAnalyzed);
            if(matchHistory != null ) {
                getDamageDealtAndDamageTaken(user, matchHistory);
                getLanesProbabilities(user, matchHistory);
            }

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

        float winRateWithChampion = 0;
        float nbGamesWithChampion = 0;
        if((float)summoner.getChampion().getStatistic().getWin() + (float)summoner.getChampion().getStatistic().getLoose() != 0){
             winRateWithChampion = (float)summoner.getChampion().getStatistic().getWin()/((float)summoner.getChampion().getStatistic().getWin()+(float)summoner.getChampion().getStatistic().getLoose());
             nbGamesWithChampion = Math.min((summoner.getChampion().getStatistic().getWin()+summoner.getChampion().getStatistic().getLoose())/50,1);
        }

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
        summoner.getChampion().getStatistic().setPerformance((winRateWithChampion * (float) 2.5 + nbGamesWithChampion * (float) 2.5 + 5 * rank) / 10);
     }

    public static void getCreepChartInfo(Summoner user, JSONArray matchHistory) {
        double zeroToTen, tenToTwenty, twentyToThirty, thirtyToEnd;
        zeroToTen = tenToTwenty = twentyToThirty = thirtyToEnd = 0;
        int numberOfValueZeroToTen, numberOfValueTenToTwenty, numberOfValueTwentyToThirsty, numberOfValueThirtyToEnd;
        numberOfValueZeroToTen = numberOfValueTenToTwenty = numberOfValueTwentyToThirsty = numberOfValueThirtyToEnd = 0;
        JSONArray jsonParticipants = null;
        long idGame = 0;



        try {
            for (int i = 0; i < matchHistory.length(); i++) {

                idGame = matchHistory.getJSONObject(i).getLong("matchId");
                String jsonResult = Utils.getDocument(Constant.API_MATCHS + String.valueOf(idGame));
                JSONObject gameDetails = new JSONObject(jsonResult);

                boolean idChampionFound = false;
                int indexParticipants = 0;
                while(!idChampionFound)
                {
                    Log.v("Debug creep ", "index participants : " + indexParticipants);
                    if(gameDetails.getJSONArray("participants").getJSONObject(indexParticipants).getInt("championId") == user.getChampion().getId())
                    {
                        idChampionFound = true;
                    }
                    else{
                        indexParticipants ++;
                    }
                }
                if (!gameDetails.isNull("participants")) {
                    jsonParticipants = gameDetails.getJSONArray("participants");
                    if (!jsonParticipants.getJSONObject(indexParticipants).isNull("timeline")) {
                        if (!jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").isNull("creepsPerMinDeltas")) {
                            if (!jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").isNull("zeroToTen")) {
                                zeroToTen += jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").getDouble("zeroToTen");
                                numberOfValueZeroToTen++;
                            }
                            if (!jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").isNull("tenToTwenty")) {
                                tenToTwenty += jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").getDouble("tenToTwenty");
                                numberOfValueTenToTwenty++;
                            }
                            if (!jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").isNull("twentyToThirty")) {
                                twentyToThirty += jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").getDouble("twentyToThirty");
                                numberOfValueTwentyToThirsty++;

                            }
                            if (!jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").isNull("thirtyToEnd")) {
                                thirtyToEnd += jsonParticipants.getJSONObject(indexParticipants).getJSONObject("timeline").getJSONObject("creepsPerMinDeltas").getDouble("thirtyToEnd");
                                numberOfValueThirtyToEnd++;
                            }
                        }
                    }
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
    } catch (Exception e) {
            e.printStackTrace();
            Log.v("Erreur creep", e.getMessage());
        }
    }

    //Get most champions played list and Stats
    public static void getStatiscicsAndMostChampionsPlayed(Summoner summoner) {
        String jsonResult = Utils.getDocument(Constant.API_STATS_URI + summoner.getId() + "/ranked");

        try {
            //Not on game
            if (jsonResult == null) {
                summoner.getDataProcessed().setStats(true);
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

            if(mostChampionsPlayed.length != 0 && mostChampionsPlayed[0].getId() == summoner.getChampion().getId()) {
                summoner.getChampion().setMain(true);
            }
            summoner.getDataProcessed().setStats(true);
        } catch (Exception e) {
            summoner.getDataProcessed().setStats(true);
            e.printStackTrace();
            return;
        }


    }

    //Load async images
    public static void loadMostPlayedChampionsImages(Summoner summoner) {
        try {
            JSONObject jsonChampions = new JSONObject(Utils.getDocumentAndCheck(Constant.API_CHAMPION_URI,5));

            if(summoner.getMostChampionsPlayed() != null) {
                for(int i = 0; i < summoner.getMostChampionsPlayed().length; i++) {
                    //set champion
                    JSONObject championJson = (JSONObject) ((JSONObject)jsonChampions.get("data")).get(((Integer)summoner.getMostChampionsPlayed()[i].getId()).toString());
                    summoner.getMostChampionsPlayed()[i].setName(championJson.get("name").toString());
                    summoner.getMostChampionsPlayed()[i].setIconName(((JSONObject) championJson.get("image")).get("full").toString());
                    new Utils.SetObjectIcon().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, summoner.getMostChampionsPlayed()[i]);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Get premades
    public static void getPremades(Summoner summoner, ArrayList<Summoner> summoners) {
        try {
            JSONObject json = new JSONObject(Utils.getDocumentAndCheck(Constant.API_SUMMONER_GAMES + summoner.getId() + "/recent",2));

            int[][] nbGamesWithUser = new int[(summoners.size() / 2) - 1][2];

            int index = 0;
            for (int i = 0; i < summoners.size(); i++) {
                if (summoners.get(i).getId() != summoner.getId() && summoners.get(i).getTeamId() == summoner.getTeamId()) {
                    int[] line = new int[2];
                    //User id
                    line[0] = summoners.get(i).getId();
                    //Nb times saw on games
                    line[1] = 0;
                    nbGamesWithUser[index] = line;
                    index++;
                }
            }

            JSONArray jsonArray = (JSONArray)json.get("games");

            //For each games
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonGame = ((JSONObject) jsonArray.get(i));

                if(!jsonGame.isNull("fellowPlayers")) {
                    JSONArray jsonPlayers = (JSONArray)jsonGame.get("fellowPlayers");

                    for(int j = 0; j < jsonPlayers.length(); j++) {
                        JSONObject jsonPlayer = ((JSONObject) jsonPlayers.get(j));

                        for(int x = 0; x < nbGamesWithUser.length; x++) {
                            if(summoner.getTeamId() == jsonPlayer.getInt("teamId") && nbGamesWithUser[x][0] == jsonPlayer.getInt("summonerId") ) {
                                nbGamesWithUser[x][1]++;
                                break;
                            }
                        }
                    }
                }
            }

            /*Log.v("TOTO","Team = " + summoner.getTeamId());

            for (int i = 0; i < nbGamesWithUser.length; i++) {
                Log.v("PREMADES", nbGamesWithUser[i][0] + " " + nbGamesWithUser[i][1]);
            }*/

            int limit;
            switch (summoner.getLeague().getDivision().split(" ")[0].toString()) {
                case "BRONZE":
                    limit = 1;
                    break;
                case "SILVER":
                    limit = 1;
                    break;
                case "GOLD":
                    limit = 1;
                    break;
                case "PLATINUM":
                    limit = 1;
                    break;
                case "DIAMOND":
                    limit = 2;
                    break;
                case "MASTER":
                    limit = 3;
                    break;
                case "CHALLENGER":
                    limit = 3;
                    break;
                default:
                    limit = 1;
                    break;
            }

            for(int i = 0; i < nbGamesWithUser.length; i++) {
                if(nbGamesWithUser[i][1] >= limit) {
                    for(Summoner premadeCurrent : summoners) {
                        if(premadeCurrent.getId() == nbGamesWithUser[i][0]) {
                            Log.v("PREMADES",summoner.getName() + " and " + premadeCurrent.getName() + " are premade");

                            if(premadeCurrent.getPremade() != 0 && summoner.getPremade() != 0) {
                                //1 and 2 are friends, need merge
                                if(premadeCurrent.getPremade() != summoner.getPremade()) {
                                    for(Summoner summonerPremade : summoners) {
                                        if(summonerPremade.getPremade() != 0) {
                                            summonerPremade.setPremade(1);
                                        }
                                    }
                                }
                            }
                            //Get the premade to summoner
                            else if(premadeCurrent.getPremade() != 0){
                                summoner.setPremade(premadeCurrent.getPremade());
                            }
                            //Get the premade to premadeCurrent
                            else if(summoner.getPremade() != 0) {
                                premadeCurrent.setPremade(summoner.getPremade());
                            }
                            //New premades detected
                            else {
                                premadeCurrent.setPremade(getMaxPremades(summoner.getTeamId(), summoners)+1);
                                summoner.setPremade(premadeCurrent.getPremade());
                            }
                        }
                    }
                }
            }
            summoner.getDataProcessed().setPremades(true);
            //Log.v("TOTO", "Limit = " + String.valueOf(limit));
        } catch (Exception e) {
            summoner.getDataProcessed().setPremades(true);
            e.printStackTrace();
        }
    }

    public static int[] getUserBuild(JSONArray jsonParticipants, int numberOfItemToAnalyze, int summonerIdChampion)
    {
        JSONArray json = jsonParticipants;

        int[] build = new int[numberOfItemToAnalyze];
        try{
            boolean idChampionFound = false;
            int indexParticipants = 0;
            while(!idChampionFound)
            {
                Log.v("Debug favorite build ", "index participants : " + indexParticipants);
                if(jsonParticipants.getJSONObject(indexParticipants).getInt("championId") == summonerIdChampion)
                {
                    idChampionFound = true;
                }
                else{
                    indexParticipants ++;
                }
            }
            for(int i=0;i<numberOfItemToAnalyze;i++)
            {
                if(!jsonParticipants.getJSONObject(indexParticipants).isNull("stats")) {
                    if(!jsonParticipants.getJSONObject(indexParticipants).getJSONObject("stats").isNull("item" + i)){
                        int itemID = jsonParticipants.getJSONObject(indexParticipants).getJSONObject("stats").getInt("item" + i);
                        build[i] = itemID;
                    }
                    else{
                        build[i] = 0;
                    }
                }
            }
            //DEBUG EDDY
            /*for(int i=0;i<numberOfItemToAnalyze;i++)
            {
                Log.v("Debug favorite build ", "item + " + build[i]);
            }
            Log.v("Debug favorite build ", "------------------");*/
            //DEBUG EDDY
            return build;
        }catch (Exception e) {
            e.printStackTrace();
            Log.v("Erreur creep", e.getMessage());
            return null;
        }
    }

    //Get the max value of premades
    private static int getMaxPremades(int team, ArrayList<Summoner> summoners) {
        int value = 0;

        for(Summoner summoner  : summoners) {
            if(summoner.getPremade() > value && summoner.getTeamId() == team) {
                value = summoner.getPremade();
            }
        }

        return value;
    }

    private static void getLevels(ArrayList<Summoner> summoners) {
        try {
            //Get request
            String jsonResult = Utils.getDocument(Constant.API_SUMMONER_INFO_URI + concatAndEncodeNames(summoners));

            //Player not exist
            if(jsonResult == null) {
                return;
            }

            JSONObject json = new JSONObject(jsonResult);

            for(Summoner current : summoners) {
                JSONObject jsonChampion = (JSONObject)json.get(current.getName().replaceAll(" ", "").toLowerCase());

                //Set summuner level
                current.setLevel((int) jsonChampion.get("summonerLevel"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Concat and encod names
    private static String concatAndEncodeNames(ArrayList<Summoner> summoners) {
        String result = "";

        try {
            for (Summoner summoner : summoners) {
                result += URLEncoder.encode(summoner.getName(), "UTF-8") + ",";
            }

            result = result.substring(0, result.length() - 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void getDamageDealtAndDamageTaken(Summoner summoner, JSONArray jsonMatches){
        long idGame = 0;
        int teamID = 0;
        int totalDamageDealtByUserTeamInCurrentGame = 0;
        int toalDamageDealtByUserInCurrentGame = 0;

        int totalDamageTakenByUserTeamInCurrentGame = 0;
        int totalDamageTakenByUserInCurrentGame = 0;

        float meanPercentageDamageDealtByUser = 0;
        float meanPercentageDamageTakenByUser = 0;

        int numberOfGames = 0;
        try {
            for (int i = jsonMatches.length()-1; i > Math.max(jsonMatches.length() - 3, jsonMatches.length() - 2) ; i--) {
                if (!jsonMatches.getJSONObject(i).isNull("season") && (jsonMatches.getJSONObject(i).getString("season").equals("SEASON2015") )) {

                    numberOfGames++;
                    idGame = jsonMatches.getJSONObject(i).getLong("matchId");
                    String jsonResult = Utils.getDocument(Constant.API_MATCHS + String.valueOf(idGame));

                    if(jsonResult != null)
                    {
                        JSONObject gameDetails = new JSONObject(jsonResult);

                        boolean idChampionFound = false;
                        int indexParticipants = 0;
                        while(!idChampionFound)
                        {
                            Log.v("Debug dealt ", "index participants : " + indexParticipants);
                            if(gameDetails.getJSONArray("participants").getJSONObject(indexParticipants).getInt("championId") == summoner.getChampion().getId())
                            {
                                idChampionFound = true;
                            }
                            else{
                                indexParticipants ++;
                            }
                        }

                        teamID = gameDetails.getJSONArray("participants").getJSONObject(indexParticipants).getInt("teamId");
                        toalDamageDealtByUserInCurrentGame = gameDetails.getJSONArray("participants").getJSONObject(indexParticipants).getJSONObject("stats").getInt("totalDamageDealtToChampions");
                        totalDamageTakenByUserInCurrentGame = gameDetails.getJSONArray("participants").getJSONObject(indexParticipants).getJSONObject("stats").getInt("totalDamageTaken");

                        if (jsonResult != null) {
                            //JSONObject gameDetails = new JSONObject(jsonResult);
                            for (int j = 0; j < gameDetails.getJSONArray("participants").length(); j++) {
                                if (gameDetails.getJSONArray("participants").getJSONObject(j).getInt("teamId") == teamID) {
                                    totalDamageDealtByUserTeamInCurrentGame += gameDetails.getJSONArray("participants").getJSONObject(j).getJSONObject("stats").getInt("totalDamageDealtToChampions");
                                    totalDamageTakenByUserTeamInCurrentGame += gameDetails.getJSONArray("participants").getJSONObject(j).getJSONObject("stats").getInt("totalDamageTaken");
                                }
                            }
                            meanPercentageDamageDealtByUser += (float) toalDamageDealtByUserInCurrentGame / (float) totalDamageDealtByUserTeamInCurrentGame;
                            meanPercentageDamageTakenByUser += (float) totalDamageTakenByUserInCurrentGame / (float) totalDamageTakenByUserTeamInCurrentGame;
                            totalDamageDealtByUserTeamInCurrentGame = 0;
                            totalDamageTakenByUserTeamInCurrentGame = 0;
                        }
                    }
                }
            }
            if(numberOfGames != 0)
            {
                meanPercentageDamageDealtByUser /= numberOfGames;
                meanPercentageDamageTakenByUser /= numberOfGames;
                meanPercentageDamageDealtByUser *= 100;
                meanPercentageDamageTakenByUser *= 100;
            } else {
                meanPercentageDamageDealtByUser = 0;
                meanPercentageDamageTakenByUser = 0;
            }
            summoner.getChampion().getStatistic().setDamageDealtPercentage(meanPercentageDamageDealtByUser);
            summoner.getChampion().getStatistic().setDamageTakenPercentage(meanPercentageDamageTakenByUser);
        } catch(Exception e){
            e.printStackTrace();
            Log.v("Erreur creep", e.getMessage());
        }
    }

    public static void getSummonerFavoriteBuild(Summoner summoner, JSONArray matchHistory) {
        long idGame = 0;
        Log.v("DAO", "Favorite build");
        ArrayList<int[]> itemHistoy = new ArrayList<>();
        int[] matchItemHistory = new int[6];
        int numberOfItemToAnalyze = 5;
        JSONArray jsonParticipants = null;
        try {
            String globalItems = Utils.getDocumentAndCheck(Constant.API_ITEMS + "?itemListData=gold,into",4);

            if(globalItems == null) {
                return;
            }

            JSONObject jsonGlobalItems = new JSONObject(globalItems);
            jsonGlobalItems = jsonGlobalItems.getJSONObject("data");

            for (int i = 0; i < matchHistory.length(); i++) {
                idGame = matchHistory.getJSONObject(i).getLong("matchId");
                String jsonResult = Utils.getDocument(Constant.API_MATCHS + String.valueOf(idGame));
                JSONObject gameDetails = new JSONObject(jsonResult);

                if (!gameDetails.isNull("participants") && gameDetails.getString("season").equals("SEASON2015")) {
                    jsonParticipants = gameDetails.getJSONArray("participants");
                    matchItemHistory = getUserBuild(jsonParticipants, numberOfItemToAnalyze, summoner.getChampion().getId());
                    if (i == 0) {
                        for (int j = 0; j < numberOfItemToAnalyze; j++) {
                            int itemID = matchItemHistory[j];
                            if (itemID != 0){
                                if(!jsonGlobalItems.isNull(String.valueOf(itemID)))
                                {
                                    JSONObject result = jsonGlobalItems.getJSONObject(String.valueOf(itemID));

                                    if(!result.isNull("gold") && !result.getJSONObject("gold").isNull("total"))
                                    {
                                        if (result.isNull("into") && result.getJSONObject("gold").getInt("total") > 1000) {
                                            int[] item = new int[2];
                                            item[0] = itemID;
                                            item[1] = 0;
                                            itemHistoy.add(item);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        for (int j = 0; j < numberOfItemToAnalyze; j++) {

                            boolean found = false;
                            for (int k = 0; k < itemHistoy.size(); k++) {
                                if (itemHistoy.get(k)[0] == matchItemHistory[j]) {
                                    itemHistoy.get(k)[1]++;
                                    found = true;
                                }
                            }
                            if (!found) {
                                //Log.v("DAO", "Item into, user : " + user.getName());
                                int itemID = matchItemHistory[j];
                                if (itemID != 0) {
                                    if(!jsonGlobalItems.isNull(String.valueOf(itemID)))
                                    {
                                        JSONObject result = jsonGlobalItems.getJSONObject(String.valueOf(itemID));
                                        if(!result.isNull("gold") && !result.getJSONObject("gold").isNull("total"))
                                        {
                                            if (result.isNull("into") && result.getJSONObject("gold").getInt("total") > 1000) {
                                                int[] newItem = new int[2];
                                                newItem[0] = itemID;
                                                newItem[1] = 0;
                                                itemHistoy.add(newItem);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        Collections.sort(itemHistoy, new SortIntegerTabArrayList());
        int numberOfitems = Math.min(numberOfItemToAnalyze, itemHistoy.size());
        Item[] Build = new Item[numberOfitems];
        for(int i=0; i <numberOfitems;i++)
        {
             Build[i] = new Item(String.valueOf(itemHistoy.get(i)[0]) + ".png", null);
            new Utils.SetObjectIcon().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Build[i]);
        }
        summoner.getChampion().setBuild(Build);
        } catch (Exception e){
            e.printStackTrace();
            //Log.v("Erreur creep", e.getMessage());
        }
    }

    public static JSONArray getMatchHistory(Summoner summoner, int numberOfGamesAnalyzed){
        JSONObject jsonResult;
        String debugRequest = Constant.API_MATCH_LIST_URI +
                summoner.getId() +
                "?championIds=" +
                summoner.getChampion().getId() +
                "&rankedQueus=RANKED_SOLO_5x5&beginIndex=" + 0 + "&endIndex=" + numberOfGamesAnalyzed;
        try {
            jsonResult = new JSONObject(Utils.getDocument(Constant.API_MATCH_LIST_URI +
                    summoner.getId() +
                    "?championIds=" +
                    summoner.getChampion().getId() +
                    "&rankedQueus=RANKED_SOLO_5x5&beginIndex=" + 0 + "&endIndex=" + numberOfGamesAnalyzed));
            JSONArray jsonMatches = null;
            if(!jsonResult.isNull("matches")) {
                jsonMatches = jsonResult.getJSONArray("matches");
            }
            return jsonMatches;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("Erreur creep", e.getMessage());
            return null;
        }
    }

    private static void getLanesProbabilities(Summoner user, JSONArray matchHistory) {
        long idGame = 0;
        try {
            for (int i = 0; i < matchHistory.length(); i++) {

                idGame = matchHistory.getJSONObject(i).getLong("matchId");
                String jsonResult = Utils.getDocument(Constant.API_MATCHS + String.valueOf(idGame));
                if(jsonResult != null)
                {
                    JSONObject gameDetails = new JSONObject(jsonResult);

                    LaneProbability laneProbability = new LaneProbability();

                    JSONObject jsonMatch = (JSONObject)matchHistory.get(i);

                    boolean idChampionFound = false;
                    int indexParticipants = 0;
                    while(!idChampionFound)
                    {
                        Log.v("Debug dealt ", "index participants : " + indexParticipants);
                        if(gameDetails.getJSONArray("participants").getJSONObject(indexParticipants).getInt("championId") == user.getChampion().getId())
                        {
                            idChampionFound = true;
                        }
                        else{
                            indexParticipants ++;
                        }
                    }

                    jsonMatch = (JSONObject)((gameDetails.getJSONArray("participants")).get(indexParticipants));

                    laneProbability.setSpellId1(jsonMatch.getInt("spell1Id"));
                    laneProbability.setSpellId2(jsonMatch.getInt("spell2Id"));
                    jsonMatch = (JSONObject)jsonMatch.get("timeline");
                    laneProbability.setLane(jsonMatch.getString("lane"));
                    laneProbability.setRole(jsonMatch.getString("role"));

                    user.getChampion().addLaneProbability(laneProbability);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            Log.v("Erreur lanes", e.getMessage());
        }
    }

    public static void calculLanesProbabilities(ArrayList<Summoner> summonersList) {
        for(Summoner summoner : summonersList) {
            ArrayList<LaneProbability> summary = new ArrayList<>();
            for(LaneProbability laneHistory : summoner.getChampion().getLanesProbabilities()) {
                boolean isFound = false;
                for(LaneProbability lane : summary) {
                    if(lane.getLane().equals(laneHistory.getLane()) && lane.getRole().equals(laneHistory.getRole())) {
                        if((lane.getSpellId1() == laneHistory.getSpellId1() || lane.getSpellId1() == laneHistory.getSpellId2()) &&
                                (lane.getSpellId2() == laneHistory.getSpellId1() || lane.getSpellId2() == laneHistory.getSpellId2())) {
                            lane.setNbOccurences(lane.getNbOccurences() + 1);
                            isFound = true;
                        }
                    }
                }
                if(!isFound) {
                    laneHistory.setNbOccurences(1);
                    summary.add(laneHistory);
                }
            }

            for(LaneProbability lane : summary) {
                lane.setProba((float) lane.getNbOccurences() / (float) summoner.getChampion().getLanesProbabilities().size());
                if((summoner.getSpells()[0].getId() == lane.getSpellId1() || summoner.getSpells()[0].getId() == lane.getSpellId2()) &&
                        (summoner.getSpells()[1].getId() == lane.getSpellId1() || summoner.getSpells()[1].getId() == lane.getSpellId2())) {
                    lane.setProba(lane.getProba() + new Float(0.5));
                }

             }

            summoner.getChampion().setLanesProbabilities(summary);

            for(LaneProbability lane : summary) {
                if(lane.getRole().contains("SUPPORT")) {
                    summoner.getChampion().getSummary()[4].setProba(summoner.getChampion().getSummary()[4].getProba() + lane.getProba());
                }
                else if(lane.getLane().contains("JUNGLE")) {
                    summoner.getChampion().getSummary()[1].setProba(summoner.getChampion().getSummary()[1].getProba() + lane.getProba());
                }
                else if(lane.getLane().contains("TOP")) {
                    summoner.getChampion().getSummary()[0].setProba(summoner.getChampion().getSummary()[0].getProba() + lane.getProba());
                }
                else if(lane.getLane().contains("MIDDLE")) {
                    summoner.getChampion().getSummary()[2].setProba(summoner.getChampion().getSummary()[2].getProba() + lane.getProba());
                }
                else if(lane.getRole().contains("CARRY")) {
                    summoner.getChampion().getSummary()[3].setProba(summoner.getChampion().getSummary()[3].getProba() + lane.getProba());
                }
            }
        }

        for(int i = 0; i < 5; i++) {
            Summoner user = null;
            Summoner user2 = null;

            for(Summoner summoner : summonersList) {
                if(summoner.getTeamId() == 100) {
                    if(user == null) {
                        user = summoner;
                    }
                    else if(user.getChampion().getSummary()[i].getProba() < summoner.getChampion().getSummary()[i].getProba()) {
                        user = summoner;
                    }
                }

            }

            for(Summoner summoner : summonersList) {
                if(summoner.getTeamId() == 200) {
                    if(user2 == null) {
                        user2 = summoner;
                    }
                    else if(user2.getChampion().getSummary()[i].getProba() < summoner.getChampion().getSummary()[i].getProba()) {
                        user2 = summoner;
                    }
                }

            }

            if(user != null && user.getChampion().getSummary()[i].getProba() != new Float(0)) {
                user.getChampion().setLane(user.getChampion().getSummary()[i].getLane());
            }

            if(user2 != null && user2.getChampion().getSummary()[i].getProba() != new Float(0)) {
                user2.getChampion().setLane(user.getChampion().getSummary()[i].getLane());
            }
        }
    }

    public static void getRunesDetailled(ArrayList<Summoner> summoners) {
        try {
            JSONObject jsonRunes = (new JSONObject(Utils.getDocumentAndCheck(Constant.API_RUNES,4))).getJSONObject("data");

            for(Summoner summoner : summoners) {
                for(int i = 0; i < summoner.getRunes().size(); i++) {
                    Rune rune = summoner.getRunes().get(i);
                    JSONObject jsonRune = jsonRunes.getJSONObject(String.valueOf(rune.getId()));
                    rune.setName(jsonRune.getString("name"));
                    rune.setDescription(jsonRune.getString("description"));

                    String stats = jsonRune.getString("stats");

                    //replace '{' and '}'
                    stats = stats.substring(1,stats.length()-1);

                    stats = stats.replaceAll("\"","").replace(" ","");
                    String statsSplitter[] = stats.split(",");

                    for(int j = 0; j < statsSplitter.length; j++) {
                        String stat[] = statsSplitter[j].split(":");

                        rune.getStats().put(stat[0],stat[1]);
                    }
                }

                summoner.getDataProcessed().setRunes(true);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();

            for(Summoner summoner : summoners) {
                summoner.getDataProcessed().setRunes(true);
            }
        }
    }

    public static void getMasteriesDetailled(ArrayList<Summoner> summoners) {
        try {
            JSONObject jsonMasteries = (new JSONObject(Utils.getDocumentAndCheck(Constant.API_MASTERIES,4))).getJSONObject("data");

            for(Summoner summoner : summoners) {
                for(int i = 0; i < summoner.getMasteries().size(); i++) {
                    Mastery mastery = summoner.getMasteries().get(i);

                    JSONObject jsonMastery = jsonMasteries.getJSONObject(String.valueOf(mastery.getId()));
                    mastery.setName(jsonMastery.getString("name"));
                    mastery.setDescription(jsonMastery.getJSONArray("description").getString(mastery.getRank()-1));
                }

                summoner.getDataProcessed().setMasteries(true);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();

            for(Summoner summoner : summoners) {
                summoner.getDataProcessed().setMasteries(true);
            }
        }
    }
}
