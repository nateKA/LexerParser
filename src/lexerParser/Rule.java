package lexerParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rule {
    private List<Rubric> rubrics = new ArrayList<>();
    private int min = 1;
    private int max = 1;
    private int minSize;
    private int potentialSize;
    private HashMap<String,String> attributes = new HashMap<>();

    public Rule(Rubric...rubricList){
        for(Rubric r: rubricList){
            rubrics.add(r);
        }

        minSize = 0;
        potentialSize = 0;
        for(Rubric r: rubrics){
            minSize += r.getMin();
            potentialSize += r.getMax();
        }
    }
    public Rule(int min, int max, Rubric...rubricList){
        this(rubricList);
        this.min = min;
        this.max = max;
    }

    /**
     * Returns the size of this rule
     * The 'size' represents the minimum number of tokens it requires to fill this rule.
     * @return
     */
    public int getSize(){
        return minSize;
    }

    /**
     * A Rule is considered optional if min == 0
     * @return
     */
    public boolean isOptional(){
        return min == 0;
    }

    /**
     * Returns the potential size of this rule
     * The 'potential size' represents the maximum number of tokens it can take to fill this rule.
     * @return
     */
    public int getPotential(){
        return potentialSize;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public List<RuleResult> testTokens(List<Token> tokens, int startHere, String origin){
        List<RuleResult> findingTokens = new ArrayList<>();
        int offset = 0;

        int timesMatched = 0;
        boolean soFarSoGood = true;
        while(timesMatched < max && startHere+getSize() <= tokens.size() && soFarSoGood){
            List<Token> thisFound = new ArrayList<>();
            for(Rubric rubric: rubrics){
                List<Token> matchedTokens = rubric.testTokens(tokens,startHere+offset);

                if(matchedTokens.size() == 0){
                    soFarSoGood = false;
                    break;
                }else{
                    thisFound.addAll(matchedTokens);
                    offset+=matchedTokens.size();
                }
            }
            if(thisFound.size()>0)
                findingTokens.add(new RuleResult(this,thisFound,origin));

            if(soFarSoGood)
                timesMatched++;
        }

        if(timesMatched >= min) {
            return findingTokens;
        }else
            return new ArrayList<>();
    }

    public void put(String key, String value){
        attributes.put(key,value);
    }
    public String get(String key){
        return attributes.get(key);
    }
    public HashMap<String,String> getAttributes(){
        return attributes;
    }
    public List<Rubric> getRubrics(){
        return rubrics;
    }




}
