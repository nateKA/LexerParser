package XMLparsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLDocument {
    private XMLElement prolog = null;
    private XMLElement root = null;
    private String name = null;
    private String fullPath = null;

    public XMLDocument(XMLElement root){
        this.root = root;
    }

    public List<XMLElement> getElementsByPath(String path){
        return root.getElementsByPath(path);
    }

    /**
     * Returns a list of Strings where each element is the innerXML contained in the
     * XML element at the end of the path, and each element is unique.
     * @param path
     * @return
     */
    public List<String> getUniqueValuesByPath(String path){
        List<XMLElement> temp =  root.getElementsByPath(path);
        List<String> list = new ArrayList<>();

        for(XMLElement e: temp){
            if(!list.contains(e.getInnerXML())){
                list.add(e.getInnerXML());
            }
        }

        return list;
    }

    /**
     * Returns a list of XMLElements where each element is an element found at the end of the path
     * and contains a key with a value equal to the one provided as an argument
     * @param path
     * @param key
     * @param value
     * @return
     */
    public List<XMLElement> getElementsByAttribute(String path,String key, String value){
        List<XMLElement> temp =  root.getElementsByPath(path);
        List<XMLElement> list = new ArrayList<>();

        for(XMLElement e: temp){
            if(value.equals(e.getAttribute(key))){
                list.add(e);
            }
        }

        return list;
    }

    /**
     * Returns a list of elements where each element is an element found at the end of the path,
     * and only the subElements with tags found in the tags[] array are populated.
     * @param path
     * @param tags
     * @return
     */
    public List<XMLElement> collect(String path, String...tags){
        List<XMLElement> list = new ArrayList<>();

        for(XMLElement e: getElementsByPath(path)){
            XMLElement retEl = new XMLElement(e.getTag());
            retEl.setAttributes(e.getAttributes());
            retEl.setInnerXML(e.getInnerXML());
            for(String tag: tags){
                for(XMLElement subEl: e.getSubElementsByTag(tag)){
                    retEl.addSubElement(subEl);
                }
            }
            list.add(retEl);
        }

        return list;
    }

    public XMLElement getProlog() {
        return prolog;
    }

    public void setProlog(XMLElement prolog) {
        this.prolog = prolog;
    }

    public XMLElement getRoot() {
        return root;
    }

    public void setRoot(XMLElement root) {
        this.root = root;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
    public String toString(){
        String str = "";
        if(prolog!=null){
            str += getPrologString()+"\n";
        }
        str += root.toString();
        return str;
    }
    private String getPrologString(){
        String str = "";
        Iterator<String> iter = prolog.getAttributes().keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            String val = prolog.getAttributes().get(key);
            str += String.format("  %s=\"%s\"",key,val);
        }
        return String.format("<?%s%s?>",prolog.getTag(),str);
    }
}
