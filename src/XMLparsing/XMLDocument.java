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
    public List<XMLElement> collect(String path, String...tags){
        List<XMLElement> list = new ArrayList<>();

        for(XMLElement e: getElementsByPath(path)){
            XMLElement retEl = new XMLElement(e.getTag());
            retEl.setAttributes(e.getAttributes());
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
