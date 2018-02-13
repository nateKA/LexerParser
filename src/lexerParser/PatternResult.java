package lexerParser;

import org.omg.CORBA.MARSHAL;
import resources.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class PatternResult {
    private List<RuleResult> rules = new ArrayList<>();
    private Token group;
    private int size = 0;
    private int start;
    private int end;

    public PatternResult(Pattern pattern, List<RuleResult> rules, String origin){
        group = new Token();
        this.rules = rules;

        for(RuleResult p: rules){
            size += p.size();
        }

        start = rules.get(0).getStartOffset();
        end = rules.get(rules.size()-1).getEndOffset();
        group.putInfo(Utilities.FINDING_START_INDEX_NAME,start);
        group.putInfo(Utilities.FINDING_END_INDEX_NAME,end);
        group.putInfo(Utilities.FINDING_TEXT_NAME,origin.substring(start,end));
        group.copyAttributes(pattern.getAttributes(),Utilities.TAG);
        inherit(group.getAttributes(),getTokensAtLexerLevel());

    }
    private void inherit(HashMap<String,Object> map,List<Token> tokens){
        Iterator<String> iter = map.keySet().iterator();
        HashMap<String,String> toInherit = new HashMap<>();
        List<String> toRemove = new ArrayList<>();
        while(iter.hasNext()){
            String key = iter.next();
            String value = map.get(key).toString();
            if(value.matches("inherit\\(.*?\\(\\s*\\d+\\s*\\)\\)")){
                Matcher m = java.util.regex.Pattern.compile("inherit\\((.*?)\\((\\d+)\\)\\)").matcher(value);
                if(m.find()){
                    MatchResult res = m.toMatchResult();
                    String getKey = res.group(1);
                    int index = Integer.parseInt(res.group(2).trim());
                    boolean found = false;
                    int count = 0;
                    for(Token t: tokens){
                        if(t.getString(getKey)!=null){

                            if(count == index) {
                                found= true;
                                toInherit.put(key, t.getString(getKey));
                                break;
                            }
                            count++;
                        }
                    }
                    if(!found)
                        toRemove.add(key);
                }
            }else if(value.matches("inherit\\(.*?\\(\\s*last\\s*\\)\\)")){
                Matcher m = java.util.regex.Pattern.compile("inherit\\((.*?)\\((last)\\)\\)").matcher(value);
                if(m.find()){
                    MatchResult res = m.toMatchResult();
                    String getKey = res.group(1);
                    boolean found = false;
                    for(Token t: tokens){
                        if(t.getString(getKey)!=null){

                                found= true;
                                toInherit.put(key, t.getString(getKey));
                        }
                    }
                    if(!found)
                        toRemove.add(key);
                }
            }else
            if(value.matches("inherit\\(.*\\)")){
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

    public int getStartOffset(){
        return start;
    }
    public int getEndOffset(){
        return end;
    }
    public Token getGroup(){
        return group;
    }
    public List<RuleResult> getRuleMatches(){
        return rules;
    }
    public int size(){
        return size;
    }

    public String toString(){
        String str = "";
        for(RuleResult pr: rules){
            str+=pr.getGroup().toString();
        }
        return str;
    }

    public void mergeWithRules(){
        for(RuleResult rr: rules){
            group.copyAttributes(convertToStringMap(rr.getAttributes()),Utilities.DO_NOT_COPY_ATTRIBUTES);
        }
    }
    public Token getTokensAtPatternLevel(){
        return getGroup();
    }
    private HashMap<String,String> convertToStringMap(HashMap<String,Object> map){
        HashMap<String,String> newMap = new HashMap<>();
        Iterator<String> iter = map.keySet().iterator();

        while(iter.hasNext()){
            String key = iter.next();
            String val = map.get(key).toString();
            newMap.put(key,val);
        }
        return newMap;
    }

    public List<Token> getTokensAtLexerLevel(){
        List<Token> tokens = new ArrayList<>();
        for(RuleResult pr: this.rules){
            tokens.addAll(pr.getTokens());
        }
        return tokens;
    }
    public Token getTokensAtLexerLevel(int i){
        return getTokensAtLexerLevel().get(i);
    }
    public List<Token> getTokensAtLexerLevel(String key,String value){
        List<Token> tokens = new ArrayList<>();
        for(Token t: getTokensAtLexerLevel()){
            if(value.equals(t.getString(key))){
                tokens.add(t);
            }
        }

        return tokens;
    }
    public List<Token> getTokensAtRuleLevel(){
        List<Token> tokens = new ArrayList<>();
        for(RuleResult pr: this.rules){
            tokens.add(pr.getGroup());
        }
        return tokens;
    }
    public Token getTokensAtRuleLevel(int i){
        return getTokensAtRuleLevel().get(i);
    }
    public List<Token> getTokensAtRuleLevel(String key,String value){
        List<Token> tokens = new ArrayList<>();
        for(Token t: getTokensAtRuleLevel()){
            if(value.equals(t.getString(key))){
                tokens.add(t);
            }
        }

        return tokens;
    }
}
