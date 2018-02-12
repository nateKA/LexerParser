package lexerParser;

import resources.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RuleResult {
    private List<Token> tokens = new ArrayList<>();
    private Token group;

    public RuleResult(Rule rule, List<Token> tokens, String origin){
        group = new Token();
        this.tokens = tokens;

        int s = tokens.get(0).getInt(Utilities.FINDING_START_INDEX_NAME);
        int e = tokens.get(tokens.size()-1).getInt(Utilities.FINDING_END_INDEX_NAME);

        group.copyAttributes(rule.getAttributes(),Utilities.TAG);
        inherit(group.getAttributes(),tokens);
        group.putInfo(Utilities.FINDING_TEXT_NAME,origin.substring(s,e));
    }

    private void inherit(HashMap<String,Object> map,List<Token> tokens){
        Iterator<String> iter = map.keySet().iterator();
        HashMap<String,String> toInherit = new HashMap<>();
        List<String> toRemove = new ArrayList<>();
        while(iter.hasNext()){
            String key = iter.next();
            String value = map.get(key).toString();
            if(value.matches("inherit\\(.*?\\)")){
                String getKey = value.substring(8,value.length()-1);
                boolean found = false;
                int count = 0;
                for(Token t: tokens){
                    if(t.getString(getKey)!=null){

                        found= true;
                        if(count == 0) {
                            toInherit.put(key, t.getString(getKey));
                        }else if(count == 1){
                            String temp = toInherit.get(key).toString();
                            toInherit.remove(key);
                            toRemove.add(key);
                            toInherit.put(key+"_0",temp);
                            toInherit.put(key+"_"+count,t.getString(getKey));
                        }else{
                            toInherit.put(key+"_"+count,t.getString(getKey));
                        }
                        count++;
                    }
                }
                if(!found)
                    toRemove.add(key);
            }
        }
        iter = toInherit.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            String value = toInherit.get(key);
            map.put(key,value);
        }
        for(String str: toRemove){
            map.remove(str);
        }
    }
    public HashMap<String, Object> getAttributes(){
        return group.getAttributes();
    }
    public Token getFirstToken(){
        return tokens.get(0);
    }
    public Token getLastToken(){
        return tokens.get(tokens.size()-1);
    }
    public int getStartOffset(){
        return getFirstToken().getInt(Utilities.FINDING_START_INDEX_NAME);
    }
    public int getEndOffset(){
        return getLastToken().getInt(Utilities.FINDING_END_INDEX_NAME);
    }
    public Token getGroup(){
        return group;
    }
    public List<Token> getTokens(){
        return tokens;
    }
    public int size(){
        return tokens.size();
    }
    public String toString(){
        return group.getString(Utilities.FINDING_TEXT_NAME);
    }
}
