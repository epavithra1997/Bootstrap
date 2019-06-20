package Utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class JSONtoMapConversion {
    public static Map<String, Object> jsonToMap(JSONObject json) {
        Map<String, Object> retMap;
        retMap = toMap(json);
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) {
        Map<String, Object> map = new HashMap<>();

        for (Object key : object.keySet()) {
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put((String) key, value);
        }
        return map;
    }

    public static Map<String, String> toStringMap(JSONObject object) {
        Map<String, String> map = new HashMap<>();

        for (Object key : object.keySet()) {
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put((String) key, value.toString());
        }
        return map;
    }

    public static Map<String, String> toMapWithStringKeyAndValue(JSONObject object) {
        Map<String, String> map = new HashMap<>();

        for (Object key : object.keySet()) {
            Object value = object.get(key);
            map.put((String) key, (String) value);
        }
        return map;
    }

    public static LinkedList<String> toLinkedListWithStringValue(JSONArray array) {
        LinkedList<String> list = new LinkedList<>();
        for (int i = 0; i < array.size(); i++) {
            String value = (String) array.get(i);
            list.add(value);
        }
        return list;
    }

    public static List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
