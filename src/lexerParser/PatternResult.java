package lexerParser;

import resources.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
