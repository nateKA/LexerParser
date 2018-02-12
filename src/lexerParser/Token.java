package lexerParser;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import resources.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;

public class Token {
    HashMap<String,Object> attributes = new HashMap<>();

    public Token(){
        //nothing
    }
    public Token(String annot, String type, int start, int end){
        attributes.put("type",type);
        attributes.put(Utilities.FINDING_TEXT_NAME,annot);
        attributes.put(Utilities.FINDING_START_INDEX_NAME,start);
        attributes.put(Utilities.FINDING_END_INDEX_NAME,end);
    }
    public Token(MatchResult result){
        attributes.put(Utilities.FINDING_TEXT_NAME,result.group());
        attributes.put(Utilities.FINDING_START_INDEX_NAME,result.start());
        attributes.put(Utilities.FINDING_END_INDEX_NAME,result.end());
    }
    public Token(String type, MatchResult result){
        this(result.group(),type,result.start(),result.end());
    }

    public void putInfo(String key, Object value){
        attributes.put(key,value);
    }
    public Object getInfo(String key){
        return attributes.get(key);
    }
    public int getInt(String key){
        Object o = attributes.get(key);
        if(o==null)return 0;
        return Integer.parseInt(o.toString().trim());
    }
    public String getString(String key){
        Object o = attributes.get(key);
        if(o==null){
            return null;
        }
        return attributes.get(key).toString();
    }

    public String toString(){
        String str = "";
        Iterator<String> iter = attributes.keySet().iterator();

        int i = 0;
        while(iter.hasNext()){
            String key = iter.next();
            Object val = attributes.get(key).toString();
            if(i > 0){
                str+=",";
            }
            str += key+"="+val;
            i++;
        }
        return "{"+str+"}";
    }


    public void copyToken(Token t,String...ignore){
        HashMap<String,Object> map = t.getAttributes();
        Iterator<String> iter = map.keySet().iterator();

        while(iter.hasNext()){
            String key = iter.next();
            String val = map.get(key).toString();

            boolean ignoreThis = false;
            for(String s: ignore){
                if(s.equals(key)){
                    ignoreThis = true;
                    break;
                }
            }
            if(ignoreThis)continue;

            attributes.put(key,val);
        }
    }

    /**
     * The token will copy all attributes from t, unless is already has an attribute of that type
     * @param t
     */
    public void copyWithPreservation(Token t){
        HashMap<String,Object> map = t.getAttributes();
        Iterator<String> iter = map.keySet().iterator();

        while(iter.hasNext()){
            String key = iter.next();
            String val = map.get(key).toString();

            if(attributes.containsKey(key))
                continue;

            attributes.put(key,val);
        }
    }
    public void copyAttributes(HashMap<String,String> map, String...ignore){
        Iterator<String> iter = map.keySet().iterator();

        while(iter.hasNext()){
            String key = iter.next();
            String val = map.get(key).toString();

            boolean ignoreThis = false;
            for(String s: ignore){
                if(s.equals(key)){
                    ignoreThis = true;
                    break;
                }
            }
            if(ignoreThis)continue;

            attributes.put(key,val);
        }
    }

    public void removeAttributesByMatch(String regex){
        Iterator<String> iter = attributes.keySet().iterator();
        List<String> toRemove = new ArrayList<>();

        while(iter.hasNext()){
            String key = iter.next();
            if(key.matches(regex)){
                toRemove.add(key);
            }
        }
        
        for(String s: toRemove){
            attributes.remove(s);
        }
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }
}
