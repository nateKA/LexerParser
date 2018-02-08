package lexerParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pattern {
    private List<Rule> rules = new ArrayList<>();
    private String name = "unnamed";
    private HashMap<String,String> attributes = new HashMap<>();

    public Pattern(Rule...rulesList){
        for(Rule r: rulesList){
            rules.add(r);
        }
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void put(String key, String value){
        attributes.put(key,value);
    }
    public String get(String key){
        return attributes.get(key);
    }


    public PatternResult testTokens(List<Token> rule, int startHere,String origin){
        List<RuleResult> findingTokens = new ArrayList<>();

        boolean soFarSoGood = true;
        for(Rule r: rules){
            List<RuleResult> found = r.testTokens(rule,startHere,origin);
            if(found.size() > 0 || r.isOptional()){
                findingTokens.addAll(found);
                for(RuleResult pr: found)
                    startHere+=pr.size();
            }else{
                soFarSoGood = false;
                break;
            }
        }

        if(soFarSoGood) {
            return new PatternResult(this,findingTokens,origin);
        }else
            return null;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }




    public String toString(){
        return attributes.get("type").toString();
    }
    public static void main(String[] args){
        Parser p = new Parser();
        p.compile("src/resources/files/rule.xml");
    }
}
