package org.ema.utils;

import android.util.Log;

import org.ema.model.business.Summoner;

import java.util.ArrayList;
import java.util.Hashtable;

public class Constant {
    //Region
    private static Region localRegion = Region.EUW;

    public static String DDRAGON_SUMMONER_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/profileicon/";
    public static String DDRAGON_CHAMPION_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/champion/";
    public static String DDRAGON_CHAMPION_SPELL_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/spell/";
    public static String DDRAGON_SUMMONER_SPELL_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/spell/";
    public static String DDRAGON_ITEMS_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/item/";
    public static String DDRAGON_SCOREBOARD_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/ui/";

    private static String CACHE_SERVER_URI = "http://devtoolbox.fr:1337/?url=";
    private static String API_DNS = "https://euw.api.pvp.net/";
    private static String API_STATIC_DNS = "https://global.api.pvp.net/";
    //Default EUW
    public static String API_CURRENT_GAME_URI = CACHE_SERVER_URI + API_DNS + "observer-mode/rest/consumer/getSpectatorGameInfo/EUW1/";

    //Default EUW (for all)
    public static String API_CHAMPION_URI = CACHE_SERVER_URI + API_STATIC_DNS + "api/lol/static-data/euw/v1.2/champion?dataById=true&champData=allytips,enemytips,altimages,image,spells";
    public static String API_LEAGUE_URI = CACHE_SERVER_URI + API_DNS + "api/lol/euw/v2.5/league/by-summoner/";
    public static String API_MATCH_HISTORY_URI = CACHE_SERVER_URI + API_DNS + "api/lol/euw/v2.2/matchhistory/";
    public static String API_STATS_URI = CACHE_SERVER_URI + API_DNS + "api/lol/euw/v1.3/stats/by-summoner/";
    public static String API_SUMMONER_INFO_URI = CACHE_SERVER_URI + API_DNS + "api/lol/euw/v1.4/summoner/by-name/";
    public static String API_SUMMONER_SPELLS = CACHE_SERVER_URI + API_STATIC_DNS + "api/lol/static-data/euw/v1.2/summoner-spell?dataById=true&spellData=cooldown,image";
    public static String API_ITEMS = CACHE_SERVER_URI + API_STATIC_DNS + "api/lol/static-data/euw/v1.2/item";
    public static String API_MATCHS = CACHE_SERVER_URI + API_DNS + "api/lol/euw/v2.2/match/";
    public static String API_RUNES = CACHE_SERVER_URI + API_STATIC_DNS + "api/lol/static-data/euw/v1.2/rune?runeListData=stats";
    public static String API_MASTERIES = CACHE_SERVER_URI + API_STATIC_DNS + "api/lol/static-data/euw/v1.2/mastery?masteryListData=masteryTree,ranks";

    //Need to add "/recent" after the summoner id
    public static String API_SUMMONER_GAMES = CACHE_SERVER_URI + API_DNS + "api/lol/euw/v1.3/game/by-summoner/";

    private static Hashtable<Region,String> regionsHashtable = new Hashtable<Region,String>();
    static {
        regionsHashtable.put(Region.BR,"/br");
        regionsHashtable.put(Region.EUNE,"/eune");
        regionsHashtable.put(Region.EUW,"/euw");
        regionsHashtable.put(Region.LAN,"/lan");
        regionsHashtable.put(Region.LAS,"/las");
        regionsHashtable.put(Region.NA,"/na");
        regionsHashtable.put(Region.OCE,"/oce");
        regionsHashtable.put(Region.RU,"/ru");
        regionsHashtable.put(Region.TR,"/tr");
    }

    private static Hashtable<Region,String> regionsCurrentHashtable = new Hashtable<Region,String>();
    static {
        regionsCurrentHashtable.put(Region.BR,"BR1");
        regionsCurrentHashtable.put(Region.EUNE,"EUN1");
        regionsCurrentHashtable.put(Region.EUW,"EUW1");
        regionsCurrentHashtable.put(Region.LAN,"LA1");
        regionsCurrentHashtable.put(Region.LAS,"LA2");
        regionsCurrentHashtable.put(Region.NA,"NA1");
        regionsCurrentHashtable.put(Region.OCE,"OC1");
        regionsCurrentHashtable.put(Region.RU,"RU");
        regionsCurrentHashtable.put(Region.TR,"TR1");
    }

    private static Hashtable<Region,String> regionsNameHashtable = new Hashtable<Region,String>();
    static {
        regionsNameHashtable.put(Region.BR, "Brazil");
        regionsNameHashtable.put(Region.EUNE, "Europe Nordic & East");
        regionsNameHashtable.put(Region.EUW, "Europe West");
        regionsNameHashtable.put(Region.LAN, "Latin America North");
        regionsNameHashtable.put(Region.LAS, "Latin America South");
        regionsNameHashtable.put(Region.NA, "North America");
        regionsNameHashtable.put(Region.OCE, "Oceania");
        regionsNameHashtable.put(Region.RU, "Russia");
        regionsNameHashtable.put(Region.TR, "Turkey");
    }

        public static Hashtable<String,Region> regionsFromViewHashtable = new Hashtable<String,Region>();
        static {
            regionsFromViewHashtable.put("NA - North America",Region.NA);
            regionsFromViewHashtable.put("EUW - Europe West",Region.EUW);
            regionsFromViewHashtable.put("EUNE - Europe Nordic and East",Region.EUNE);
            regionsFromViewHashtable.put("BR - Brasil",Region.BR);
            regionsFromViewHashtable.put("TR - Turkey",Region.TR);
            regionsFromViewHashtable.put("RU - Russia",Region.RU);
            regionsFromViewHashtable.put("LAN - Latin America North",Region.LAN);
            regionsFromViewHashtable.put("OCE - Oceania",Region.OCE);
            regionsFromViewHashtable.put("LAS - Latin America South",Region.LAS);
    }

    private static String setRequestRegion(String request, Region region) {
        request = request.replaceAll(regionsHashtable.get(localRegion),regionsHashtable.get(region));
        request = request.replaceAll(regionsCurrentHashtable.get(localRegion),regionsCurrentHashtable.get(region));
        return request;
    }

    public static void setRegion(Region region) {
        API_DNS = setRequestRegion(API_DNS,region);
        API_CURRENT_GAME_URI = setRequestRegion(API_CURRENT_GAME_URI,region);
        API_CHAMPION_URI = setRequestRegion(API_CHAMPION_URI,region);
        API_LEAGUE_URI = setRequestRegion(API_LEAGUE_URI,region);
        API_MATCH_HISTORY_URI = setRequestRegion(API_MATCH_HISTORY_URI,region);
        API_STATS_URI = setRequestRegion(API_STATS_URI,region);
        API_SUMMONER_INFO_URI = setRequestRegion(API_SUMMONER_INFO_URI,region);
        API_SUMMONER_SPELLS = setRequestRegion(API_SUMMONER_SPELLS,region);
        API_SUMMONER_GAMES = setRequestRegion(API_SUMMONER_GAMES,region);
        API_ITEMS = setRequestRegion(API_ITEMS,region);
        API_MATCHS = setRequestRegion(API_MATCHS,region);
        API_RUNES = setRequestRegion(API_RUNES,region);
        API_MASTERIES = setRequestRegion(API_MASTERIES,region);

        localRegion = region;
    }
}
