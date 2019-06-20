import java.util.ArrayList;
import java.util.HashMap;

public class ListParser {
    public static int findLowestTopElementDistance(HashMap<String, ElementDetails> elementDetails, ArrayList<String> elementList){
        int lowestTopValue = 0;
        for (String elementName:
             elementList) {
            ElementDetails currentElementDetails = elementDetails.get(elementName);
            if(currentElementDetails.hasTop()){
                if(lowestTopValue == 0){
                    lowestTopValue = currentElementDetails.getOffsetY();
                }else {
                    if(currentElementDetails.getOffsetY() < lowestTopValue) {
                        lowestTopValue = currentElementDetails.getOffsetY();
                    }
                }
            }
        }
        return lowestTopValue;
    }
}
