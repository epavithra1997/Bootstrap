import Utility.JSONtoMapConversion;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class HTMLStructureFormation {
    final static String UNIQUESTRING = "{}";
    private static Map<String, String> obj;

    static {
        try {
            obj = JSONtoMapConversion.toStringMap((JSONObject) new JSONParser().parse(new FileReader("./bootstrap.json")));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static String getHTMLStringForGivenProperty(String property){
        return obj.get(property);
    }

    public static String buildHTMLStringForGivenPropertyAndValue(String property, String htmlValue){

        if(property.equals("col")) {
//            String divContainer = obj.get("div_con").replace("[]", htmlValue);
            return obj.get(property).replace(UNIQUESTRING, htmlValue);
        }else{
            if(property.equals("image")){
                String imageSrc;
                if(Files.exists(Paths.get("assets/"+htmlValue+".png"))){
                    imageSrc = "../assets/"+htmlValue+".png";
                }else {
                    imageSrc = "../assets/"+htmlValue+".jpg";
                }
                return obj.get(property).replace(UNIQUESTRING, "").replace("srcName",imageSrc);
            }
            return obj.get(property).replace(UNIQUESTRING, htmlValue);
        }
    }
}
