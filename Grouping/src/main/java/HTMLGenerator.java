import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HTMLGenerator {
    private HashMap<String, Boolean> elementFormationStatus = new HashMap<>();
    private HashMap<String, ElementDetails> elementDetails;

    HTMLGenerator(HashMap<String, ElementDetails> elementDetails) {
        this.elementDetails = elementDetails;
    }

    public String formHTMLStructure() {
        HashMap<String, String[]> elementsTemplateMap = new HashMap<>();
        String tempElementName = "";
        String finalTemplate = "";
        for (HashMap.Entry<String, ElementDetails> elementDetailEntry :
                elementDetails.entrySet()) {
            String elementName = elementDetailEntry.getKey();
            ElementDetails elementDetail = elementDetailEntry.getValue();
            if (elementFormationStatus.containsKey(elementName) && elementFormationStatus.get(elementName)) {
                continue;
            }
            if (elementDetail.hasContains() || !elementFormationStatus.containsKey(elementName)) {
                elementFormationStatus.put(elementName, true);
                Pair<String, LinkedList<ArrayList<String>>> childElementsPair = getFilteredChildElementListBasedOnSideElements(elementDetail);
                String templete = null;
                String divClass = "";
                if (childElementsPair != null && childElementsPair.getKey().equals("row")) {
                    templete = formRowData(childElementsPair.getValue());
                    divClass = "row";
                }
                if (childElementsPair != null && childElementsPair.getKey().equals("col")) {
                    templete = formColumnData(childElementsPair.getValue());
                    divClass = "col";
                }
                String tempStringArray[] = {templete, divClass};
                elementsTemplateMap.put(elementName, tempStringArray);
            }
        }
        finalTemplate = formTemplateFromElementTemplateMap(elementsTemplateMap);
//        System.out.println(finalTemplate);
        System.out.println(elementsTemplateMap);
        if (Files.exists(Paths.get("assets/body-background.png"))) {
            finalTemplate = HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue("body", finalTemplate).replace("mainbgimgPath", "../assets/body-background.png");
        } else {
            finalTemplate = HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue("body", finalTemplate);
        }
        return finalTemplate;
    }

    private String formTemplateFromElementTemplateMap(HashMap<String, String[]> elementsTemplateMap) {
        HashMap<String, Boolean> templeteFormationStatusMap = new HashMap<>();
        HashMap<String, String[]> newElementsTemplateMap = new HashMap<>();
        for (HashMap.Entry<String, String[]> pair :
                elementsTemplateMap.entrySet()) {
            if (pair.getValue()[0] != null) {
                templeteFormationStatusMap.put(pair.getKey(), false);
                newElementsTemplateMap.put(pair.getKey(), pair.getValue());
            }
        }
        elementsTemplateMap = newElementsTemplateMap;
        String tempElementName = "";
        String finalTemplate = "";
        int count = 0;
        while (checkWhetherTemplateIsFormedForAllElements(templeteFormationStatusMap)) {
            for (HashMap.Entry<String, String[]> pair :
                    elementsTemplateMap.entrySet()) {
                String elementName = pair.getKey();
                String templete = pair.getValue()[0];
                String divClass = pair.getValue()[1];
                if (!templeteFormationStatusMap.get(elementName)) {
                    if (finalTemplate.contains(elementName)) {
                        if (!elementDetails.get(tempElementName).getContains().contains(elementName))
                            tempElementName = elementName;
//                        String checkStructure = HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue(divClass,elementName).replace("className",elementName);
//                        String checkStructure = getHTMLStructureBasedOnElementType("col",elementDetails.get(elementName).getName(),elementDetails.get(elementName).getType());
//                        finalTemplate = finalTemplate.replace(checkStructure,templete);
                        finalTemplate = finalTemplate.replace(">\n" + (elementDetails.get(elementName).getText() != null ? elementDetails.get(elementName).getText() : elementName) + "\n<", ">" + templete + "<");
                        templeteFormationStatusMap.put(elementName, true);
                    } else if (!tempElementName.equals("") && templete.contains(tempElementName)) {
//                        String checkStructure = HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue(divClass,elementName).replace("className",elementName);
//                        String checkStructure = getHTMLStructureBasedOnElementType("col",elementDetails.get(tempElementName).getName(),elementDetails.get(tempElementName).getType());
                        //                        finalTemplate = templete.replace(checkStructure,finalTemplate);
                        finalTemplate = templete.replace(">\n" + (elementDetails.get(tempElementName).getText() != null ? elementDetails.get(tempElementName).getText() : tempElementName) + "\n<", ">" + finalTemplate + "<");
                        if (!elementDetails.get(tempElementName).getContains().contains(elementName))
                            tempElementName = elementName;
                        templeteFormationStatusMap.put(elementName, true);
                    } else if (finalTemplate.equals("")) {
                        tempElementName = elementName;
                        finalTemplate = templete;
                        templeteFormationStatusMap.put(elementName, true);
                    }
                }
            }
            count++;
            if (count > 30)
                break;
            System.out.println(templeteFormationStatusMap);
        }
        return finalTemplate;
    }

    private boolean checkWhetherTemplateIsFormedForAllElements(HashMap<String, Boolean> elementsTemplateMap) {
        boolean isAllElementTempleteAdded = false;
        for (HashMap.Entry<String, Boolean> pair :
                elementsTemplateMap.entrySet()) {
            if (!pair.getValue()) {
                isAllElementTempleteAdded = true;
            }
        }
        return isAllElementTempleteAdded;
    }

    private Pair<String, LinkedList<ArrayList<String>>> getFilteredChildElementListBasedOnSideElements(ElementDetails elementDetail) {
        HashMap<String, ElementDetails> sortedChildElementDetailsMap = SortMap.sortMapByXandY(constructMapForGivenLinkedList(elementDetail.getContains(), elementDetails));
        LinkedList<ArrayList<String>> childElementWithSideElements = new LinkedList<>();
        boolean isRow = false;
        boolean isColumn = false;
        ArrayList<String> negleteElementArray = new ArrayList<>();
        for (HashMap.Entry<String, ElementDetails> elementMap :
                sortedChildElementDetailsMap.entrySet()) {
            ElementDetails childElementDetails = elementMap.getValue();
            if (childElementDetails.hasTop() || childElementDetails.hasBottom()) {
                isRow = true;
                ArrayList<String> sideElementsArray = new ArrayList<String>() {{
                    addAll(childElementDetails.getLeft());
                    addAll(childElementDetails.getRight());
                }};
                if (checkWhetherSideElementsAvailable(childElementDetails) && isTopOrBottomElementPresentInSideElementsList(sideElementsArray) == null) {
                    sideElementsArray.removeAll(negleteElementArray);
                    if (!negleteElementArray.contains(childElementDetails.getName()))
                        childElementWithSideElements = checkWhetherElementAlreadyExistsAndUpdateAccordingly(childElementWithSideElements, sideElementsArray, childElementDetails.getName());
                } else {
                    childElementWithSideElements.add(new ArrayList<String>() {{
                        add(childElementDetails.getName());
                    }});
                }
                if (isTopOrBottomElementPresentInSideElementsList(sideElementsArray) != null) {
                    negleteElementArray.add(isTopOrBottomElementPresentInSideElementsList(sideElementsArray));
                    childElementWithSideElements.add(new ArrayList<String>() {{
                        add(isTopOrBottomElementPresentInSideElementsList(sideElementsArray));
                    }});
                }


            } else {
                isColumn = true;
                childElementWithSideElements.add(new ArrayList<String>() {{
                    add(childElementDetails.getName());
                }});
            }
        }
        if (childElementWithSideElements.size() > 0) {
            for (ArrayList<String> elementNameList :
                    childElementWithSideElements) {
                int indexOfElementNameInList = childElementWithSideElements.indexOf(elementNameList);
                childElementWithSideElements.set(indexOfElementNameInList, SortMap.sortListByXandYUsingElementName(elementDetails, elementNameList));
            }
            //return childElementWithSideElements;
        }
//        for (ArrayList<String> childElementsArray:
//                childElementWithSideElements) {
//            int indexOfElementNameInList = childElementWithSideElements.indexOf(childElementsArray);
//            childElementWithSideElements.set(indexOfElementNameInList,SortMap.sortListByXUsingElementName(elementDetails,childElementsArray));
//        }
        if (isRow) {
            return new Pair<>("row", childElementWithSideElements);
        }
        if (isColumn) {
            childElementWithSideElements = SortMap.sortListByXUsingElementName(elementDetails, childElementWithSideElements);
            return new Pair<>("col", childElementWithSideElements);
        }
        return null;
    }

    private String formColumnData(LinkedList<ArrayList<String>> childElementWithSideElements) {
        StringBuilder columnData = new StringBuilder();
        for (ArrayList<String> elementNameList :
                childElementWithSideElements) {
            for (String elementName :
                    elementNameList) {
                ElementDetails currentElement = elementDetails.get(elementName);
                int marginTop = 0;
                int marginBottom = 0;
                if(currentElement.hasParent()){
                    ElementDetails parentElement = elementDetails.get(currentElement.getParent().get(0));
                    marginTop = currentElement.getY() - parentElement.getY();
                    marginBottom = (parentElement.getY() + parentElement.getHeight()) - (currentElement.getY() + currentElement.getHeight());
                }

                String colTemplate;

                if(elementNameList.size() == 1 &&
                        currentElement.hasParent() &&
                        elementDetails.get(currentElement.getParent().get(0)).getType().equals("button")){
                    String elementText = elementDetails.get(elementName).getText() != null ? elementDetails.get(elementName).getText() : elementName;
                    colTemplate = HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue("empty-div",elementText);

                }else {
                    colTemplate = getHTMLStructureBasedOnElementType("col", currentElement.getName(), currentElement.getType());
                }
                Document doc = Jsoup.parse(colTemplate);
                doc.body().child(0).attr("style", doc.body().child(0).attr("style")+"padding-top: " + String.valueOf(marginTop) + "px;");
                doc.body().child(0).attr("style", doc.body().child(0).attr("style")+"padding-bottom: " + String.valueOf(marginBottom) + "px;");
                columnData.append(doc.body().child(0).toString());
            }
        }
        return columnData.toString();
    }

    private String formRowData(LinkedList<ArrayList<String>> childElementWithSideElements) {
        StringBuilder rowData = new StringBuilder();
        for (ArrayList<String> elementNameList :
                childElementWithSideElements) {
            int lowestYValue = ListParser.findLowestTopElementDistance(elementDetails,elementNameList);
            String marginTopString = "style=\"margin-top: " + String.valueOf(lowestYValue) + "px;\"";
            StringBuilder columnData = new StringBuilder();
            for (String elementName :
                    elementNameList) {
                columnData.append(getHTMLStructureBasedOnElementType("col", elementDetails.get(elementName).getName(), elementDetails.get(elementName).getType()));
            }
            rowData.append(HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue("row", columnData.toString()).replace("row\"", "row \"" + marginTopString));
        }
        return rowData.toString();
    }

    private String getHTMLStructureBasedOnElementType(String divClass, String elementName, String type) {
//        return HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue(divClass,
//                HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue(type,elementName)
//                        .replace("[]",elementName).replace("className",elementDetails.get(elementName).getName()));
        System.out.println(elementName);
        System.out.println(calcColOffset(elementDetails.get(elementName)));
        int offsetValue = calcColOffset(elementDetails.get(elementName));
        String offsetString = (offsetValue > 1 ? " offset-" + offsetValue : "");
        String marginLeftString = "style=\"margin-left: " + String.valueOf(calcSpacingBetweenLeftAndCurrentElement(elementDetails.get(elementName))) + "%;\"";
        //String marginLeftString = "style=\"margin-left: " + String.valueOf(calcSpacingBetweenLeftAndCurrentElement(elementDetails.get(elementName))) + "%;margin-top: "+elementDetails.get(elementName).getOffsetY()+"px;\"";
        return HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue(divClass,
                getTemplateBasedOnType(elementName, type))
                .replace("className", elementName)
                .replace("colNo\"", String.valueOf(elementDetails.get(elementName).getColNo()) +
                        "\""+marginLeftString);
//        "\""+marginLeftString
//        offsetString + "\""
    }

    private String getTemplateBasedOnType(String elementName, String type) {
        String elementText = elementDetails.get(elementName).getText() != null ? elementDetails.get(elementName).getText() : elementName;
        String templateText;
        if (type != null) {
            System.out.println(type);
            templateText = HTMLStructureFormation.buildHTMLStringForGivenPropertyAndValue(type, elementText);
        } else {
            templateText = elementText;
        }
        return templateText;
    }

    private int calcColOffset(ElementDetails elementDetail) {
        if (elementDetail.hasLeft()) {
            String leftElementName = elementDetail.getLeft().get(0);
            ElementDetails leftElementDetails = elementDetails.get(leftElementName);
            if (elementDetail.getStartCol() - leftElementDetails.getEndCol() >= 1) {
                return elementDetail.getStartCol() - leftElementDetails.getEndCol();
            }
        } else {
            return elementDetail.getStartCol();
        }
        return 0;
    }

    private int calcSpacingBetweenLeftAndCurrentElement(ElementDetails elementDetail) {
        int screenWidth = elementDetails.get("screen").getWidth();
        if (elementDetail.hasLeft()) {
            String leftElementName = elementDetail.getLeft().get(0);
            ElementDetails leftElementDetails = elementDetails.get(leftElementName);
//            if (elementDetail.getX() - (leftElementDetails.getX() + leftElementDetails.getWidth()) > 1) {
                int distanceBetweenLeftAndCurrentElement = elementDetail.getX() - (leftElementDetails.getX() + leftElementDetails.getWidth());
                return (int) Math.floor(((distanceBetweenLeftAndCurrentElement) / (float) screenWidth) * 100);
//            }
        } else {
            int xvalue;
            if (elementDetail.hasParent()) {
                xvalue = elementDetail.getX() - elementDetails.get(elementDetail.getParent().get(0)).getX();
            } else {
                xvalue = elementDetail.getX();
            }
            System.out.println(((float) xvalue / (float) screenWidth));
            return (int) Math.floor((xvalue / (float) screenWidth) * 100);
        }
//        return 0;
    }



    private boolean checkWhetherSideElementsAvailable(ElementDetails elementDetails) {
        return elementDetails.hasLeft() || elementDetails.hasRight();
    }

    private String isTopOrBottomElementPresentInSideElementsList(ArrayList<String> sideElements) {
        boolean isAvaliable = false;
        sideElements = SortMap.sortListByYUsingElementName(elementDetails, sideElements);
        String refElement = null;
        for (String sideElementName :
                sideElements) {
            ArrayList<String> remainingSideElements = new ArrayList<>(sideElements);
            remainingSideElements.remove(sideElements.get(sideElements.indexOf(sideElementName)));
            for (String remainingSideElementName :
                    remainingSideElements) {
                if (elementDetails.get(remainingSideElementName).getTop().contains(sideElementName)) {
                    refElement = sideElementName;
                }
                if (elementDetails.get(remainingSideElementName).getBottom().contains(sideElementName)) {
                    refElement = sideElementName;
                }
            }
        }
        return refElement;
    }

    private LinkedList<ArrayList<String>> checkWhetherElementAlreadyExistsAndUpdateAccordingly(LinkedList<ArrayList<String>> childElementWithSideElements, ArrayList<String> sideElements, String currentElement) {
        sideElements.add(currentElement);
        if (childElementWithSideElements.size() == 0) {
            return new LinkedList<ArrayList<String>>() {{
                add(new ArrayList<String>() {{
                    addAll(sideElements);
                }});
            }};
        }
        boolean isPresent = false;
        for (String elementName :
                sideElements) {
            for (ArrayList<String> elementList :
                    childElementWithSideElements) {
                if (elementList.contains(elementName)) {
                    int index = childElementWithSideElements.indexOf(elementList);
//                    if(isTopOrBottomElementPresentInSideElementsList(childElementWithSideElements.get(index)) == null){
                    childElementWithSideElements.get(index).addAll(sideElements);
                    Set<String> set = new HashSet<>(childElementWithSideElements.get(index));
                    childElementWithSideElements.get(index).clear();
                    childElementWithSideElements.get(index).addAll(set);
//                    }else {
//                        String removeElementName = isTopOrBottomElementPresentInSideElementsList(childElementWithSideElements.get(index));
//                        childElementWithSideElements.get(index).remove(removeElementName);
//                        childElementWithSideElements.add(new ArrayList<String>(){{add(removeElementName);}});
//                    }
                    isPresent = true;
//                    break;
                }
            }
        }

        if (!isPresent) {
            childElementWithSideElements.add(new ArrayList<String>() {{
                addAll(sideElements);
            }});
        }
        return childElementWithSideElements;
    }

    private boolean checkWhetherGivenElementSideOfCurrentElementsRecursively(ArrayList<String> elementList, String elementName) {
        ElementDetails currentElementDetail = elementDetails.get(elementName);
        boolean isExists = false;
        if (checkWhetherSideElementsAvailable(currentElementDetail)) {
            ArrayList<String> sideElementsArray = new ArrayList<String>() {{
                addAll(currentElementDetail.getLeft());
                addAll(currentElementDetail.getRight());
            }};
            System.out.println(sideElementsArray);
            if (sideElementsArray.size() > 0) {
                for (String sideElement :
                        sideElementsArray) {
                    ElementDetails sideElementDetails = elementDetails.get(sideElement);
                    if (elementList.contains(sideElement)) {
                        isExists = true;
                        break;
                    } else {
                        if (checkWhetherSideElementsAvailable(sideElementDetails)) {
                            if (checkWhetherGivenElementSideOfCurrentElementsRecursively(elementList, sideElement)) {
                                isExists = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return isExists;
    }

    private HashMap<String, ElementDetails> constructMapForGivenLinkedList(LinkedList<String> containElements, HashMap<String, ElementDetails> elementsDetails) {
        HashMap<String, ElementDetails> containElementsMap = new HashMap<>();
        for (String detail :
                containElements) {
            for (HashMap.Entry<String, ElementDetails> detailMap :
                    elementsDetails.entrySet()) {
                if (detailMap.getKey().equals(detail)) {
                    containElementsMap.put(detail, detailMap.getValue());
                }
            }
        }
        return containElementsMap;
    }
}