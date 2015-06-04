package org.ema.utils;

import java.util.HashMap;

public class GlobalDataManager {
    private static HashMap<String, Object> map = new HashMap<String, Object>();

    public static void add(String key, Object value){
        GlobalDataManager.map.put(key, value);
    }

    public static Object get(String key){
        Object obj = null;

        if(GlobalDataManager.map.containsKey(key)){
            obj = GlobalDataManager.map.get(key);
        }

        return obj;
    }

    public static void remove(String key){
        GlobalDataManager.map.remove(key);
    }

    public static void clearAll(){
        GlobalDataManager.map.clear();
    }
}
