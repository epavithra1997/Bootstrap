import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class CSSGenerator {
    private HashMap<String, ElementDetails> elementDetails;
    private  Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
           private Stack< String> finalStack = new Stack<String>();
            private List<String> groupList = new ArrayList<String>();
            private Stack<String> groupStack = new Stack<String>();
            Map<String,String> css_map = new HashMap<String,String>();
            String prevString = " ";
            String st=" ";
            static int j;
            String Split=" ";
    CSSGenerator(HashMap<String, ElementDetails> elementDetails){
        this.elementDetails = elementDetails;
    }

    public String builtCSSFormatContent(){
        StringBuilder stringBuilder = new StringBuilder();
       Map<String,Integer> countMap = new HashMap<String, Integer>();
       ArrayList<String> cssVar =new ArrayList<>();


       elementDetails.forEach((key, value) -> {
            if(value.getCssStyles().size() == 0){
                return;
            }

            for (Map.Entry<String, String> entry : value.getCssStyles().entrySet()) {
                String str=entry.getValue();
                if(cssVar.contains(str))
                {
                              css_map.put(str,("var_"+Integer.toString(j)));
                              j++;
                }
                else
                {
                              cssVar.add(str);
                }

               // stringBuilder.append("\t").append(entry.getKey()).append(" : ").append(entry.getValue()).append(";").append("\n");
            }

                 ArrayList<String> list = new ArrayList<String>();

               Map<String,String> css = value.getCssStyles();
               int len = css.size();
               String temp;
               //Pushing Key and Value as String
                for (Map.Entry<String,String> entry : css.entrySet())
                {
                                String str = entry.getKey()+":"+entry.getValue();
                                list.add(str);
                }
                map.put(value.getName(), list);



        });
       Iterator<Map.Entry<String, String>> itr = css_map.entrySet().iterator();

       while(itr.hasNext())
       {
            Map.Entry<String, String> entry = itr.next();
           System.out.println("Key = " + entry.getKey() +
                                ", Value = " + entry.getValue());
           String strAppend= "$"+entry.getValue()+": "+entry.getKey()+";";

           stringBuilder.append(strAppend).append("\n");

       }
       		//System.out.println(map);
       		//Ordering Function call
             ordering();
        	//System.out.println("after:"+map);
             Stack<String> tmpStack = new Stack<>();
             String grpName="";
             int dummyNo = 1,classFlag = 0;
             Collections.reverse(groupList);

             System.out.println("grppppppppppppppp"+groupList);
             //To create common class

             for(int i = 0; i < groupList.size()-1; i++)
             {
                    if(!groupList.get(i).equals("-1"))
                    {
                           if(classFlag == 0)
                           {
                                  grpName = "dummyClass_"+dummyNo++;
                                  stringBuilder.append(".").append(grpName).append("{").append("\n");
                               classFlag=1;
                           }

                           String split[] = groupList.get(i).split(":");
                           stringBuilder.append("\t");
                           int flag=0;
               for (Map.Entry<String,String> entry1 : css_map.entrySet())
            	{
            		if(entry1.getKey().equals(split[1]))
            		{
            			flag=1;
            			//stringBuilder.append(entry.getKey()).append(":").append(entry1.getValue());
            			st=entry1.getValue();
            		 	break;
            		}
            		else
            		{

            		}
            	}
            	if(flag==1)
            	{
            		 stringBuilder.append(split[0]).append(": $").append(st).append(";").append("\n");
            	}
            	else
            	{
            		stringBuilder.append(split[0]).append(":").append(split[1]).append(";").append("\n");
            	}



                    }
                    else
                    {

                           stringBuilder.append("}\n");
                           classFlag=0;
                           groupStack.pop();
                           if(groupStack.size()!=0)
                           {
                                  while(!groupStack.peek().equals("-1"))
                                  {
                                         String tmp = groupStack.peek();
                                         ArrayList<String> list = map.get(tmp);
                                         list.add("@extend ."+grpName);

                                         map.put(tmp, list);
                                         groupStack.pop();
                                  }
                           }

                    }

             }
             stringBuilder.append("}");
             stringBuilder.append(generateSASS());
       return stringBuilder.toString();
    }
//SASS Generation
    private String generateSASS() {

         StringBuilder stringBuilder = new StringBuilder();
       for(Map.Entry<String,ArrayList<String>> entry :map.entrySet())
        {
    	   int flag=0;

              stringBuilder.append(".").append(entry.getKey()).append(" {\n");
              for(String str : entry.getValue())
              {
            	  if(!str.startsWith("@extend"))
            	  {
            	  String split1[] = str.split(":");
System.out.println("splitttttttt"+split1[0]);
System.out.println("splitttttttthhhhhhhhhhhhhhhhhhh"+split1[1]);
          	    for (Map.Entry<String,String> entry1 : css_map.entrySet())
                  	{
                  		if(entry1.getKey().equals(split1[1]))
                  		{
                  			flag=1;
                  			//stringBuilder.append(entry.getKey()).append(":").append(entry1.getValue());
                  			st=entry1.getValue();
                  			Split=split1[0];
                  			break;
                  		}
                  		else
                  		{

                  		}
                  	}
                  	if(flag==1)
                  	{
                  		 stringBuilder.append("\t").append(Split).append(" : $").append(st).append(";\n");
                  	}
                  	else
                  	{
                  		stringBuilder.append("\t").append(split1[0]).append(" : ").append(split1[1]).append(";\n");
                  	}
            	  }
            	  else
            	  {
            		  stringBuilder.append("\t").append(str).append(";\n");
            	  }


              }
              stringBuilder.append("}\n");


        }
              return stringBuilder.toString();

       }

    //Compare and Get Common Elements
       private void compareList(String prevString2) {

                    Stack<String> keyStack = new Stack<String>();
                    List<String> common = new ArrayList<String>();
                    String checkKey = prevString2;
                    List<String> checkValue = map.get(checkKey);
                    List<String> prevTemp = new ArrayList<String>(checkValue);

                    Iterator itr = finalStack.iterator();
                    while(itr.hasNext())
                    {
                           String current = (String) itr.next();


                           checkValue.retainAll(map.get(current));

                           if(checkValue.size() == 0)
                           {
                                  checkValue.addAll(prevTemp);
                                                             }
                           else
                           {

                                  if(checkValue.size() >= 2)
                                  {
                                                keyStack.push(current);
                                                common.clear();
                                                common.addAll(checkValue);
                                                prevTemp.clear();
                                                prevTemp.addAll(common);
                                  }

                           }

                    }
                    System.out.println("\ncommon......."+common);
                    System.out.println("\nkey stack----------"+keyStack);
                    if(keyStack.size()>1)
                    {

                           appendValues(common,keyStack);

                           removeCommon(common,keyStack);
                           //keyStack.clear();
                    }

       }


       private void appendValues(List<String> commonGroup, Stack<String> commonLayerStack) {

                                  groupList.add("-1");
                                  groupList.addAll(commonGroup);

              groupStack.addAll(commonLayerStack);
              groupStack.push("-1");


       }
//To remove Common Elements From Mp
       private void removeCommon(List<String> commonList, Stack<String> keyStacks) {


        while(!keyStacks.isEmpty())
        {
        	System.out.println("common list:"+commonList);
                        String top = keyStacks.pop();
                        ArrayList<String> listvalues = map.get(top);
                        listvalues.removeAll(commonList);
                        map.put(top,listvalues);



        }


        ordering();
       }

       //Arranging Attributes in Ascending Order
       private void ordering() {

              int greater = 0;
         Map<String,Integer> countMap = new HashMap<String, Integer>();
              for(Map.Entry<String,ArrayList<String>> entry :map.entrySet())
            {
            	  	System.out.println("key:"+entry.getKey());
            	  	System.out.println("value:"+entry.getValue());
                           countMap.put(entry.getKey(),entry.getValue().size());
                           if(entry.getValue().size() >= 2 )
                                  greater = 1;
            }
             // System.out.println("cont:"+countMap);

       if(greater == 1)
       {
              Map<String,Integer> ascMap = sortByValues(countMap);

              finalStack.clear();
              for(Map.Entry<String,Integer> entry : ascMap.entrySet())
              {
                           finalStack.push(entry.getKey());
              }

              if(prevString.equals(finalStack.peek()))
              {
                    finalStack.pop();
                    prevString = finalStack.peek();
                    compareList(prevString);
              }
              else
              {
                    prevString=finalStack.peek();
               compareList(prevString);

              }
      }
  }

    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator =  new Comparator<K>() {
          public int compare(K k1, K k2) {
            int compare =
                  map.get(k1).compareTo(map.get(k2));
            if (compare == 0)
              return 1;
            else
              return compare;
          }
        };

        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
      }
}




