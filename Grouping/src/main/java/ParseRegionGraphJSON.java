import Utility.JSONtoMapConversion;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ParseRegionGraphJSON {

    public static HashMap<String, ElementDetails> readRegGraphAndGetElementDetailsMap(String regGraphFilePath) throws IOException, ParseException {
        HashMap<String, ElementDetails> elementsDetails = new HashMap<>();
        JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(regGraphFilePath));
        for (Object key : obj.keySet()) {
            if(!key.equals("ROOT_REF_WINDOW"))
                elementsDetails.put(key.toString(), formElementDetailsInstanceForGivenObject(obj, key));
        }
        return elementsDetails;
    }

    public static ElementDetails formElementDetailsInstanceForGivenObject(JSONObject obj, Object key) {

        JSONObject selectedObject = (JSONObject) obj.get(key);
        String elementName = (String) selectedObject.get("elementName");
        String type = (String) selectedObject.get("elementType");
        String text = (String) selectedObject.get("text");
        int styleGuideX = convertStringInPXToInt((String) selectedObject.get("styleGuideX"));
        int styleGuideY = convertStringInPXToInt((String) selectedObject.get("styleGuideY"));
        int width = convertStringInPXToInt((String) selectedObject.get("width"));
        int height = convertStringInPXToInt((String) selectedObject.get("height"));
        Map<String, String> cssStyles = selectedObject.get("cssStyles") != null ? JSONtoMapConversion.toMapWithStringKeyAndValue((JSONObject) selectedObject.get("cssStyles")) : new HashMap<>();
        LinkedList<String> top = selectedObject.get("top") != null ? JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("top")) : new LinkedList<>();
        LinkedList<String> bottom = selectedObject.get("bottom") != null ? JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("bottom")) : new LinkedList<>();
        LinkedList<String> left = selectedObject.get("left") != null ? JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("left")) : new LinkedList<>();
        LinkedList<String> right = selectedObject.get("right") != null ? JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("right")) : new LinkedList<>();
        LinkedList<String> parent = selectedObject.get("parent") != null ? JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("parent")) : new LinkedList<>();
        LinkedList<String> child = selectedObject.get("contains") != null ? JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("contains")) : new LinkedList<>();
        int xOffset = getOffsetAlongXdirection(obj, selectedObject);
        int yOffset = getOffsetAlongYdirection(obj, selectedObject);

        return new ElementDetails(elementName, type,text, width, height, styleGuideX, styleGuideY, xOffset, yOffset, cssStyles, child, top, bottom, left, right, parent);

    }

    private static int getOffsetAlongXdirection(JSONObject obj, JSONObject selectedObject){
        LinkedList<String> left = JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("left"));
        LinkedList<String> right = JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("right"));
        if(left.size()!=0){
            return checkValueExistsAndGetOffsetValue("left",obj,selectedObject, left);
        }else if(right.size()!=0){
            return checkValueExistsAndGetOffsetValue("right",obj,selectedObject, right);
        }else {
            return 0;
        }
    }

    private static int getOffsetAlongYdirection(JSONObject obj, JSONObject selectedObject){
        LinkedList<String> top = JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("top"));
        LinkedList<String> bottom = JSONtoMapConversion.toLinkedListWithStringValue((JSONArray) selectedObject.get("bottom"));
        if(top.size()!=0){
            return checkValueExistsAndGetOffsetValue("top",obj,selectedObject, top);
        }else if(bottom.size()!=0){
            return checkValueExistsAndGetOffsetValue("bottom",obj,selectedObject, bottom);
        }else {
            return 0;
        }
    }



    private static int checkValueExistsAndGetOffsetValue(String direction, JSONObject obj, JSONObject currentElement, LinkedList<String> refElementList) {
        Map<String, Integer> xyCoordinatesOfCurrentElement = getSizeAndCoordinatesOfGivenObject(currentElement);
        int minDistance = 0;
        if (refElementList.size() != 0) {
            for (String refElementFromList
                    : refElementList) {
                int distance;
                if (obj.containsKey(refElementFromList)) {
                    JSONObject refObject = (JSONObject) obj.get(refElementFromList);
                    Map<String, Integer> xyCoordinatesOfRefObject = getSizeAndCoordinatesOfGivenObject(refObject);
                    distance =  getDistaceBetweenGivenElements(direction, xyCoordinatesOfCurrentElement, xyCoordinatesOfRefObject);
                    if(minDistance == 0){
                        minDistance = distance;
                    }else {
                        if(distance < minDistance) {
                            minDistance = distance;
                        }
                    }
                }
            }
        }
        return minDistance;
    }

    private static int getDistaceBetweenGivenElements(String direction, Map<String, Integer> xyCoordinatesOfCurrentElement, Map<String, Integer> xyCoordinatesOfRefObject) {
        switch (direction) {
            case "left":
                return xyCoordinatesOfCurrentElement.get("x") - (xyCoordinatesOfRefObject.get("x") + xyCoordinatesOfRefObject.get("width"));
            case "right":
                return xyCoordinatesOfRefObject.get("x") - (xyCoordinatesOfCurrentElement.get("x") + xyCoordinatesOfCurrentElement.get("width"));
            case "top":
                return xyCoordinatesOfCurrentElement.get("y") - (xyCoordinatesOfRefObject.get("y") + xyCoordinatesOfRefObject.get("height"));
            case "bottom":
                return xyCoordinatesOfRefObject.get("y") - (xyCoordinatesOfCurrentElement.get("y") + xyCoordinatesOfCurrentElement.get("height"));
        }
        return 0;
    }

    private static Map<String, Integer> getSizeAndCoordinatesOfGivenObject(JSONObject obj) {
        int x = convertStringInPXToInt((String) obj.get("styleGuideX"));
        int y = convertStringInPXToInt((String) obj.get("styleGuideY"));
        int width = convertStringInPXToInt((String) obj.get("width"));
        int height = convertStringInPXToInt((String) obj.get("height"));
        Map<String, Integer> xyCoordinates = new HashMap<>();
        xyCoordinates.put("x", x);
        xyCoordinates.put("y", y);
        xyCoordinates.put("width", width);
        xyCoordinates.put("height", height);
        return xyCoordinates;
    }

    private static int convertStringInPXToInt(String value) {
        double doubleValue = Double.valueOf(value.replace("px", ""));
        return (int) Math.round(doubleValue);
//        return Integer.valueOf(value.replace("px", ""));
    }

}
