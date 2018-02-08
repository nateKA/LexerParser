package lexerParser;

import resources.util.Utilities;

import java.util.ArrayList;
import java.util.List;

public class PatternResult {
    private List<RuleResult> tokens = new ArrayList<>();
    private Token group;
    private int size = 0;
    private int start;
    private int end;

    public PatternResult(Pattern pattern, List<RuleResult> tokens, String origin){
        group = new Token();
        this.tokens = tokens;

        for(RuleResult p: tokens){
            size += p.size();
        }

        start = tokens.get(0).getStartOffset();
        end = tokens.get(tokens.size()-1).getEndOffset();
        group.putInfo(Utilities.FINDING_START_INDEX_NAME,start);
        group.putInfo(Utilities.FINDING_END_INDEX_NAME,end);
        group.putInfo(Utilities.FINDING_REGEX_FIELD_NAME,origin.substring(start,end));
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
        return tokens;
    }
    public int size(){
        return size;
    }

    public String toString(){
        String str = "";
        for(RuleResult pr: tokens){
            str+=pr.getGroup().toString();
        }
        return str;
    }

    public List<RuleResult> getRules(String key,String value){
        List<RuleResult> results = new ArrayList<>();
        for(RuleResult r: tokens){
            if(value.equals(r.getGroup().getString(key))){
                results.add(r);
            }
        }
        return results;
    }

    public List<Token> getTokens(){
        List<Token> tokens = new ArrayList<>();
        for(RuleResult pr: this.tokens){
            tokens.addAll(pr.getTokens());
        }
        return tokens;
    }
}
