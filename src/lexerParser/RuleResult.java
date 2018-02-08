package lexerParser;

import resources.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
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
        group.putInfo(Utilities.FINDING_REGEX_FIELD_NAME,origin.substring(s,e));
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
        return group.getString(Utilities.FINDING_REGEX_FIELD_NAME);
    }
}
