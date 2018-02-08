package lexerParser;

import resources.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class XMLElement {
    private List<XMLElement> subElements = new ArrayList<>();
    private HashMap<String,String> attributes = new HashMap<>();
    private String innerXML = null;

    public XMLElement(String tag){
        attributes.put(Utilities.TAG,tag);
    }

    public void addAttribute(String key, String value){
        attributes.put(key,value);
    }
    public void removeAttribute(String key){
        attributes.remove(key);
    }
    public String getAttribute(String key){
        return attributes.get(key);
    }
    public HashMap<String,String> getAttributeMap(){return attributes;}
    public void addSubElement(XMLElement e){
        subElements.add(e);
    }
    public void addSubElements(List<XMLElement> e){
        subElements.addAll(e);
    }
    public String getTag(){
        return attributes.get(Utilities.TAG);
    }

    public String getInnerXML() {
        return innerXML;
    }

    public HashMap<String, String> getAttributes(){
        return attributes;
    }

    public void setInnerXML(String innerXML) {
        this.innerXML = innerXML;
    }

    public String toString(){
        String str = "";
        for(XMLElement e: subElements){
            str+= " "+e.toString()+" ";
        }
        if(subElements.size() == 0){
            str = innerXML;
        }

        String elTag = "";
        Iterator<String> iter = attributes.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            String value = attributes.get(key);
            if(key.equals(Utilities.TAG))continue;
            elTag += key+"=\""+value+"\" ";
        }

        return  "<"+getTag()+" "+elTag+">"+str+"</"+getTag()+">";
    }
}
