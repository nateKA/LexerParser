package XMLparsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class XMLElement {
    private String tag;
    private String innerXML;
    private List<XMLElement> subElements = new ArrayList<>();
    private HashMap<String,String> attributes = new HashMap<>();

    public XMLElement(String tag){
        this.tag = tag;
    }
    public XMLElement(String tag,String innerXML){
        this.tag = tag;
        this.innerXML = innerXML;
    }

    public List<XMLElement> getElementsByPath(String path){
        List<XMLElement> list = new ArrayList<>();

        String[] pathArray = path.split("\\.");
        if(pathArray.length<2){
            if(this.tag.equals(pathArray[0])){
                list.add(this);
            }
            return list;
        }
        list.addAll(getElHelper(pathArray,this,1));


        return list;
    }
    private List<XMLElement> getElHelper(String[] path, XMLElement root, int level){
        List<XMLElement> list = new ArrayList<>();
        for(XMLElement e: root.getSubElements()){
            int elPathSize = checkPath(e.getTag(),path,level);
            if(elPathSize > 0){
                if(level == path.length-elPathSize){
                    list.add(e);
                }else{
                    list.addAll(getElHelper(path,e,level+elPathSize));
                }
            }
        }
        return list;
    }
    private int checkPath(String tag, String[] path, int level){
        String[] tagPath = tag.split("\\.");
        int count = 0;
        for(String str: tagPath){
            if(count+level == path.length)return count;
            if(str.equals(path[level+count])){
                count++;
            }
        }
        return count;
    }

    public String getInnerXML() {
        return innerXML;
    }

    public void setInnerXML(String innerXML) {
        this.innerXML = innerXML;
    }

    public List<XMLElement> getSubElements() {
        return subElements;
    }

    public void addSubElement(XMLElement e){
        subElements.add(e);
    }

    public void setSubElements(List<XMLElement> subElements) {
        this.subElements = subElements;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void addAttribute(String key, String value){
        attributes.put(key,value);
    }
    public String getAttribute(String key){
        return attributes.get(key);
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<XMLElement> getSubElementsByTag(String tag){
        List<XMLElement> list = new ArrayList<>();

        for(XMLElement e: subElements){
            if(e.getTag().equals(tag)){
                list.add(e);
            }
        }
        return list;
    }

    public String toString(){
        String str = "";
        if(subElements.size() == 0){
            if(innerXML==null)
                return String.format("<%s%s/>",tag,attsToString());
            else
                return String.format("<%s%s>%s</%s>",tag,attsToString(),innerXML,tag);
        }else{
            str = String.format("<%s%s>",tag,attsToString());
            String strSub = "";
            for(XMLElement x: subElements){
                strSub+="\n"+x.toString();
            }
            str += strSub.replaceAll("\n","\n\t")+String.format("\n</%s>",tag);
        }
        return str;
    }
    private String attsToString(){
        String str = "";
        Iterator<String> iter = attributes.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            String val = attributes.get(key);
            str += String.format("  %s=\"%s\"",key,val);
        }
        return str;
    }
}
