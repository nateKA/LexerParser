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

    protected XMLElement(String tag){
        attributes.put(Utilities.TAG,tag);
    }

    protected void addAttribute(String key, String value){
        attributes.put(key,value);
    }
    protected void removeAttribute(String key){
        attributes.remove(key);
    }
    protected String getAttribute(String key){
        return attributes.get(key);
    }
    protected HashMap<String,String> getAttributeMap(){return attributes;}
    protected void addSubElement(XMLElement e){
        subElements.add(e);
    }
    protected void addSubElements(List<XMLElement> e){
        subElements.addAll(e);
    }
    protected String getTag(){
        return attributes.get(Utilities.TAG);
    }

    protected String getInnerXML() {
        return innerXML;
    }

    protected HashMap<String, String> getAttributes(){
        return attributes;
    }

    protected void setInnerXML(String innerXML) {
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
