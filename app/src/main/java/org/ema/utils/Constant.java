package org.ema.utils;

/**
 * Created by romain on 01/05/2015.
 */
public class Constant {
    public static String DDRAGON_SUMMUNER_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/profileicon/";
    public static String DDRAGON_CHAMPION_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/champion/";
    public static String DDRAGON_CHAMPION_SPELL_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/spell/";
    public static String DDRAGON_SUMMONER_SPELL_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/spell/";
    public static String DDRAGON_ITEMS_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/item/";
    public static String DDRAGON_SCOREBOARD_ICON_URI = "http://ddragon.leagueoflegends.com/cdn/5.2.1/img/ui/";

    private static String CACHE_SERVER_URI = "http://devtoolbox.fr/?url=";
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

    //Give the url with the selected region.
    //DON'T WORK FOR API_CURRENT_GAME_URI;
    public static String getApiUrlByRegion(String request, String region)
    {
        return request.replace("euw", region);
    }

    //Get API_CURRENT_GAME_URI with the new region
    public static String getCurrentGameUrlByRegion(String request, String region)
    {
        return request.replace("EUW1", region);
    }
}
