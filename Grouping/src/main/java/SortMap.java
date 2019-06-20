import java.util.*;
import java.util.stream.Collectors;

public class SortMap {

    public static HashMap<String, ElementDetails> sortMapByChildrenLength(HashMap<String, ElementDetails> elementDetails){
        List<HashMap.Entry<String, ElementDetails>> elementDetailsList = new LinkedList<>(elementDetails.entrySet());

        elementDetailsList.sort(Comparator.comparingInt((o) -> o.getValue().getContains().size()));
        Collections.reverse(elementDetailsList);

        HashMap<String, ElementDetails> sortedMap = new LinkedHashMap<>();
        for (HashMap.Entry<String, ElementDetails> entry : elementDetailsList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static HashMap<String, ElementDetails> sortMapByXandY(HashMap<String, ElementDetails> elementDetails){
        List<HashMap.Entry<String, ElementDetails>> elementDetailsList = new LinkedList<>(elementDetails.entrySet());

        elementDetailsList.sort(Comparator.comparingInt((HashMap.Entry<String, ElementDetails> o) -> o.getValue().getY()).thenComparingInt(o -> o.getValue().getX()));

        HashMap<String, ElementDetails> sortedMap = new LinkedHashMap<>();
        for (HashMap.Entry<String, ElementDetails> entry : elementDetailsList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static ArrayList<String> sortListByXandYUsingElementName(HashMap<String, ElementDetails> elementDetails, ArrayList<String> list){
        ArrayList<ElementDetails> elementDetailsList = new ArrayList<>();
        for (String elementName:
             list) {
            if(elementDetails.containsKey(elementName)){
                elementDetailsList.add(elementDetails.get(elementName));
            }
        }
        elementDetailsList.sort(Comparator.comparingInt(ElementDetails::getX).thenComparingInt(ElementDetails::getY));
        return (ArrayList<String>) elementDetailsList.stream().map(ElementDetails::getName).collect(Collectors.toList());
    }

    public static LinkedList<ArrayList<String>> sortListByXUsingElementName(HashMap<String, ElementDetails> elementDetails, LinkedList<ArrayList<String>> listCollection){
        LinkedList<ArrayList<ElementDetails>> elementDetailsList = new LinkedList<>();
        for (ArrayList<String> elementArrayList:
                listCollection) {
            ArrayList<ElementDetails> arrayList = new ArrayList<>();
            for (String elementName:
                 elementArrayList) {
                if(elementDetails.containsKey(elementName)){
                    arrayList.add(elementDetails.get(elementName));
                }
            }
            elementDetailsList.add(arrayList);
        }
        elementDetailsList.sort(Comparator.comparingInt((ArrayList<ElementDetails> list) -> list.get(0).getX()));
        LinkedList<ArrayList<String>> elementDetailsListString = new LinkedList<>();
        for (ArrayList<ElementDetails> elementArrayList:
                elementDetailsList) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (ElementDetails elementName:
                    elementArrayList) {
                arrayList.add(elementName.getName());
            }
            elementDetailsListString.add(arrayList);
        }
        return elementDetailsListString;
    }

    public static ArrayList<String> sortListByXUsingElementName(HashMap<String, ElementDetails> elementDetails, ArrayList<String> list){
        ArrayList<ElementDetails> elementDetailsList = new ArrayList<>();
        for (String elementName:
                list) {
            if(elementDetails.containsKey(elementName)){
                elementDetailsList.add(elementDetails.get(elementName));
            }
        }
        elementDetailsList.sort(Comparator.comparingInt(ElementDetails::getX));
        return (ArrayList<String>) elementDetailsList.stream().map(ElementDetails::getName).collect(Collectors.toList());
    }

    public static ArrayList<String> sortListByYUsingElementName(HashMap<String, ElementDetails> elementDetails, ArrayList<String> list){
        ArrayList<ElementDetails> elementDetailsList = new ArrayList<>();
        for (String elementName:
                list) {
            if(elementDetails.containsKey(elementName)){
                elementDetailsList.add(elementDetails.get(elementName));
            }
        }
        elementDetailsList.sort(Comparator.comparingInt(ElementDetails::getY));
        return (ArrayList<String>) elementDetailsList.stream().map(ElementDetails::getName).collect(Collectors.toList());
    }
}
