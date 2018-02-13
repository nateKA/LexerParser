package lexerParser;

import resources.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static resources.util.Utilities.getCommonLexer;

public class Rubric {
    public enum Comparator {
        EQUAL("="),
        NOT_EQUAL("!=");
        //GT(">"),
       // EGT(">="),
        //LT("<"),
        //ELT("<=");
        private String value;
        private Comparator(String value) {
            this.value = value;
        }
    };
    private static final String AND = "AND";
    private static final String OR = "OR";

    private HashMap<String,Comparator> compareMap = new HashMap<>();
    private List<String> logic = new ArrayList<>();
    private int min = 1;
    private int max = 1;
    private HashMap<String,Object> checkMap = new HashMap<>();

    public Rubric(String...checks){
        compile(checks);
    }
    public Rubric(int min, int max, String...checks){
        this(checks);
        this.min = min;
        this.max = max;
    }
    public Rubric(int max,String...checks){
        this(checks);
        this.max = max;
    }

    public boolean isOptional(){
        return min==0;
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

    public boolean testToken(Token t){
        boolean passing = true;

        Iterator<String> iter = checkMap.keySet().iterator();
        int i = 0;
        while(iter.hasNext()){
            String key = iter.next();
            if(logic.get(i).equals(AND)) {
                passing = passing && check(key, t);
            }else{
                passing = passing || check(key, t);
            }

            i++;
        }

        return passing;
    }
    
    public List<Token> testTokens(List<Token> tokens,int startHere){
        List<Token> findingTokens = new ArrayList<>();
        
        int timesMatched = 0;
        while(timesMatched < max && timesMatched+startHere < tokens.size() ){
            Token testToken = tokens.get(timesMatched+startHere);
            if(testToken(testToken)){
                findingTokens.add(testToken);

                timesMatched++;
            }else{
                break;
            }
        }
        if(timesMatched >= min) {
            return findingTokens;
        }else{
            return new ArrayList<>();
        }
    }

    private boolean check(String key, Token t){
        Object tokenVal = t.getInfo(key);
        if(tokenVal == null){
            return false;
        }
        if(Integer.class.isInstance(tokenVal)){
            return checkInt(key,(int)tokenVal);
        }else{
            return checkString(key,tokenVal.toString());
        }

    }

    private boolean checkString(String key, String val){
        String[] checks = (String[]) checkMap.get(key);
        boolean passing = false;

        if(compareMap.get(key).value.equals("=")){
            for(String check: checks){
                if(check.equals(val)){
                    return true;
                }
            }
        }else if(compareMap.get(key).value.equals("!=")){
            passing = true;
            for(String check: checks){
                if(check.equals(val)){
                    return false;
                }
            }
        }

        return passing;
    }
    private boolean checkInt(String key, int val){
        return true;
    }

    public void compile(String...checks){
        for(String s: checks){
            Matcher m = Pattern.compile("(("+AND+"|"+OR+");)?(\\w+)(!=|<=|>=|<|>|=)(.+)").matcher(s);
            if(m.find()){
                MatchResult result = m.toMatchResult();

                String key = result.group(3);
                Comparator c = fillComparator(result.group(4),result.group());
                Object value = getValue(result.group(5));
                String logic = result.group(2);
                if(logic==null){
                    this.logic.add(AND);
                }else{
                    this.logic.add(logic);
                }

                checkMap.put(key,value);
                compareMap.put(key,c);
            }
        }
    }

    private Object getValue(String s){
        try{
            int i = Integer.parseInt(s.trim());
            return i;
        }catch (Exception e){}

        String[] strings = s.split("\\|");
        return strings;
    }

    private Comparator fillComparator(String s,String forThrowing) throws IllegalArgumentException{
        for(Comparator c: Comparator.values()){
            if(c.value.equals(s)){
                return c;
            }
        }

        throw new IllegalArgumentException("Invalid Comparator in Rubric : '"+forThrowing+"'");
    }


    public static void main(String[] args){
        Rubric rubric = new Rubric(1,3,"type=month|number");

        Lexer lexer = getCommonLexer();
        List<Token> tokens = lexer.tokenize("Some one was born on August 3, 1983");

        for(Token t: tokens){
            System.out.println(t.getInfo(Utilities.FINDING_TEXT_NAME)+" - "+rubric.testToken(t));
        }
    }
}
