import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class HTMLGeneratorMain {
    public static void main(String[] args) throws IOException, ParseException {
//        String regGraphPath = args[0];
//        String regGraphPath = "regionDetails_Zeplin.json";
//        String regGraphPath = "regionDetails-demopage.json";
//        String regGraphPath = "regionDetailsForInputPage-new11.json";
    //  String regGraphPath = "regionDetails_XD_New.json";
        //String regGraphPath = "regionDetails_Zeplin.json";
   String regGraphPath = "regionDetails_XD.json";

        HashMap<String, ElementDetails> elementsDetails = ParseRegionGraphJSON.readRegGraphAndGetElementDetailsMap(regGraphPath);

        int widthOfScreen = elementsDetails.get("screen").getWidth();
        int heightOfScreen = elementsDetails.get("screen").getHeight();

        System.out.println(widthOfScreen);

        for (HashMap.Entry<String, ElementDetails> entry:
             elementsDetails.entrySet()) {
            String elementName = entry.getKey();
            ElementDetails entryElementDetails = entry.getValue();

            if (!elementName.equals("screen") && !elementName.equals("ROOT_REF_WINDOW")) {
                ElementDetails parentElementDetails = elementsDetails.get(entry.getValue().getParent().get(0));
                if(parentElementDetails!=null) {
                    entryElementDetails.setColNo(calcNoOfColumn(parentElementDetails.getWidth(), entryElementDetails.getWidth()));
                    entryElementDetails.setStartCol(calcStartCol(parentElementDetails, entryElementDetails));
                    entryElementDetails.setEndCol(calcEndCol(parentElementDetails, entryElementDetails));
//                    entryElementDetails.addCssStyles("margin-left", String.valueOf(calcSpacingBetweenLeftAndCurrentElement(elementsDetails, entryElementDetails))+"px");
                }
            }
        }

        HashMap<String, ElementDetails> sortedElementsDetails = SortMap.sortMapByChildrenLength(elementsDetails);

        CSSGenerator cssGenerator = new CSSGenerator(sortedElementsDetails);
        String cssContent = cssGenerator.builtCSSFormatContent();

        HTMLGenerator htmlGenerator = new HTMLGenerator(sortedElementsDetails);
        String htmlTemplate = htmlGenerator.formHTMLStructure();

        String fileName = new File(regGraphPath).getName().split(".json")[0];
        if(new File(fileName).exists()){
            try{
//                FileUtils.deleteDirectory(new File(fileName));
            }catch (Exception e){
                System.out.println("folder del error");
            }
        }
        if(!new File(fileName).exists()) {
            new File(fileName).mkdir();
        }
//        System.out.println(htmlTemplate);
        try (FileWriter writer = new FileWriter(fileName+"/template.css");
             BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(cssContent);
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        try (FileWriter writer = new FileWriter(fileName+"/template.html");
             BufferedWriter bw = new BufferedWriter(writer)) {
             bw.write(htmlTemplate);
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    }

    private static int calcSpacingBetweenLeftAndCurrentElement(HashMap<String, ElementDetails> elementDetails, ElementDetails elementDetail){
        if(elementDetail.hasLeft()){
            String leftElementName = elementDetail.getLeft().get(0);
            ElementDetails leftElementDetails = elementDetails.get(leftElementName);
            if(elementDetail.getX() - (leftElementDetails.getX() + leftElementDetails.getWidth())>= 1){
                return elementDetail.getX() - (leftElementDetails.getX() + leftElementDetails.getWidth());
            }
        }else {
            return elementDetail.getX();
        }
        return 0;
    }

    public static int calcNoOfColumn(int screenWidth, int elementWidth){
        return (int) Math.ceil((double) elementWidth/((double) screenWidth/12));
    }

    public static int calcStartCol(ElementDetails parentDetails, ElementDetails elementDetail){
        double singleColWidth = (double) parentDetails.getWidth()/12;
        int distanceFromStartToStart = elementDetail.getX() - parentDetails.getX();
        int startCol = 0;
        for(int i = 1; i<=12; i++){
            if((singleColWidth * i) >= distanceFromStartToStart){
                startCol = i - 1;
                break;
            }
        }
        return startCol;
    }

    public static int calcEndCol(ElementDetails parentDetails, ElementDetails elementDetail){
        double singleColWidth = (double) parentDetails.getWidth()/12;
        int distanceFromStartToEnd = elementDetail.getX() + elementDetail.getWidth() - parentDetails.getX();
        int endCol = 0;
        for(int i = 1; i<=12; i++){
            if((singleColWidth * i) >= distanceFromStartToEnd){
                endCol = i;
                break;
            }
        }
        return endCol;
    }
}
