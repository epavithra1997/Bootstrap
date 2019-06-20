import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ElementDetails {
    private String name;
    private String type;
    private String text;
    private int width;
    private int height;
    private Map<String, String> cssStyles;
    private LinkedList<String> contains;
    private int x;
    private int y;
    private int colNo;
    private int startCol;
    private int endCol;
    private LinkedList<String> top;
    private LinkedList<String> bottom;
    private LinkedList<String> left;
    private LinkedList<String> right;
    private LinkedList<String> parent;
    private int offsetX;
    private int offsetY;


    ElementDetails(String name,String type,String text, int width, int height,int x, int y, int offsetX, int offsetY, Map<String, String> cssStyles,
             LinkedList<String> contains,  LinkedList<String> top,  LinkedList<String> bottom,  LinkedList<String> left, LinkedList<String> right, LinkedList<String> parent) {
        this.name = name;
        this.type = type;
        this.width = width;
        this.height = height;
        this.cssStyles = parseCSSStyles(cssStyles,text, type);
        this.contains = contains;
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.parent = parent;
        this.text = text;
    }

    public Map<String, String> parseCSSStyles(Map<String, String> cssStyles, String elementText, String type){
        Map<String, String> newCSSStyles = new HashMap<>();
        for (HashMap.Entry<String, String> entry:
                cssStyles.entrySet()) {
            if(entry.getKey().equals("font-color") && elementText == null && !type.equals("image")){
                newCSSStyles.put("background-color", entry.getValue().split(",")[0]);
                continue;
            }
            if(entry.getKey().equals("font-color") && !type.equals("image")){
                newCSSStyles.put("color", entry.getValue().split(",")[0]);
                continue;
            }
            if (!entry.getKey().equals("width") && !entry.getKey().equals("line-height") && !entry.getKey().equals("font-size")){
                newCSSStyles.put(entry.getKey(), entry.getValue());
            }
            if (entry.getKey().equals("font-size")){
                System.out.println(convertPXToEM(entry.getValue()) + "px");
                newCSSStyles.put(entry.getKey(), convertPXToEM(entry.getValue()) + "em");
//                newCSSStyles.put(entry.getKey(), entry.getValue());
            }
        }
        if(type != null && (type.equals("image") || type.equals("video-container"))){
            newCSSStyles.put("width", String.valueOf(this.width - 20)+"px");
            newCSSStyles.put("height", String.valueOf(this.height)+"px");
            System.out.println(type);
        }
        return newCSSStyles;
    }

    public double convertPXToEM(String fontSize){
        int defaultFontSize = 16;
        double floatValue = Double.valueOf(fontSize.replace("px",""));
        int fontSizeValue = (int) Math.ceil(floatValue);
        return (float)fontSizeValue / (float)defaultFontSize;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public String getText() {
        return text;
    }

    public int getHeight() {
        return height;
    }

    public Map<String, String> getCssStyles() {
        return cssStyles;
    }

    public void addCssStyles(String property, String value){
        this.cssStyles.put(property, value);
    }

    public LinkedList<String> getContains() {
        return contains;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColNo() {
        return colNo;
    }

    public void setColNo(int colNo) {
        this.colNo = colNo;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public LinkedList<String> getTop() {
        return top;
    }

    public LinkedList<String> getBottom() {
        return bottom;
    }

    public LinkedList<String> getLeft() {
        return left;
    }

    public LinkedList<String> getRight() {
        return right;
    }

    public LinkedList<String> getParent() {
        return parent;
    }

    public boolean hasContains() {
        return contains.size() > 0;
    }

    public boolean hasTop() {
        return top.size() > 0;
    }

    public boolean hasBottom() {
        return bottom.size() > 0;
    }

    public boolean hasLeft() {
        return left.size() > 0;
    }

    public boolean hasRight() {
        return right.size() > 0;
    }

    public boolean hasParent() {
        return parent.size() > 0;
    }

}

