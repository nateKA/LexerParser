package lexerParser;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDProcessor;
import resources.util.RegexHelper;
import resources.util.Utilities;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private HashMap<String,Double> runTimeMap = new HashMap<>();
    private HashMap<String,Integer> tokenCountMap = new HashMap<>();
    private List<TokenGenerator> tokenGenerators = new ArrayList<>();

    /**
     * Reads an xml file to determine how it should tokenize strings
     * A single Lexer can compile multiple xml files and combine the logic for each
     * @param xmlFilePath
     */
    public void compile(String xmlFilePath){
        XMLparser parser = new XMLparser(xmlFilePath);

        List<XMLElement> tokenRules = getTokenRules(parser);

        for(XMLElement e: tokenRules){
            TokenGenerator tg = new TokenGenerator(e);
            //Get regex properties
            boolean boundaries = "true".equals(e.getAttribute("boundaries"));
            boolean addSlashes = "true".equals(e.getAttribute("addSlashes"));

            //Setup
            List<XMLElement> regexElements = parser.getXMLElements("regex",e.getInnerXML());
            List<String> strings = prepare(regexElements,addSlashes,tg.inheritMap);
            tg.regex = RegexHelper.createRegexFromList(strings,boundaries);

            tokenGenerators.add(tg);
        }

    }

    /**
     * Generates tokens from the string s
     * @param s
     * @return
     */
    public List<Token> tokenize(String s){

        List<Token> tokens = new ArrayList<>();
        for(TokenGenerator t: tokenGenerators){
            List<Token> newTokens = executeRule(tokens,t,s);
            tokens.addAll(newTokens);
            tokens = RegexHelper.breakApart(tokens);
        }

        return tokens;
    }

    /**
     * The Lexer will drop any logic built from previously compiled xml files
     */
    public void resetTokenizers(){
        tokenGenerators = new ArrayList<>();
    }

    public void resetRunTimeStats(){
        runTimeMap = new HashMap<>();
        tokenCountMap = new HashMap<>();
    }

    /**
     * Uses the provided XMLElement to generate tokens from String s. The tokens generated are added to the
     * provided list of tokens
     * @param tokens
     * @param s
     * @return
     */
    private List<Token> executeRule(List<Token> tokens, TokenGenerator tg, String s){
        long start = System.currentTimeMillis();

        //Get regex properties
        boolean ignoreCase = "true".equals(tg.e.getAttribute("ignoreCase"));

        //Run
        List<Token> newTokens = RegexHelper.search(s,tg.regex,ignoreCase);
        finalizeTokens(newTokens,tg.e,tg.inheritMap);

        updateRunTimeMap(tg.e.getAttribute("type"),((System.currentTimeMillis()-start)/1000.0));
        updateCountMap(tg.e.getAttribute("type"),newTokens.size());

        return newTokens;
    }

    private void updateRunTimeMap(String key,double runTime){
        if(!runTimeMap.containsKey(key)){
            runTimeMap.put(key,0.0);
        }
        double value = runTimeMap.get(key);
        runTimeMap.put(key,value+runTime);
    }
    private void updateCountMap(String key,int count){
        if(!tokenCountMap.containsKey(key)){
            tokenCountMap.put(key,0);
        }
        int value = tokenCountMap.get(key);
        tokenCountMap.put(key,value+count);
    }

    /**
     * Performs finalizing logic on the tokens
     * @param newTokens
     * @param e
     * @param inheritMap
     */
    private void finalizeTokens(List<Token> newTokens,XMLElement e,HashMap<Integer,HashMap<String, String>> inheritMap){
        for(Token t: newTokens){
            String[] ignore = Utilities.DO_NOT_COPY_ATTRIBUTES;
            t.copyAttributes(e.getAttributes(),ignore);

            if(t.getInt("group")!=0 && inheritMap.get(t.getInt("group"))!=null){
                t.copyAttributes(inheritMap.get(t.getInt("group")),ignore);
                crossHatch(t.getAttributes());
            }
            t.removeAttributesByMatch("group\\(\\d+\\)");
            t.getAttributes().remove("group");//we are done with it
        }
    }
    /**
     * Essentially converts the <token></token> XML into regex
     * and compiles the Lexer
     * @param regexElements
     * @param addSlashes
     * @param inheritMap
     * @return
     */
    private List<String> prepare(List<XMLElement> regexElements, boolean addSlashes, HashMap<Integer,HashMap<String, String>> inheritMap){
        List<String> strings = new ArrayList<>();

        //tracks the number of capture groups
        int index = 2;
        for(XMLElement xml: regexElements){
            String r;
            if(xml.getAttributes().containsKey("regex")){
                r = xml.getAttribute("regex");
                xml.getAttributes().remove("regex");
            }else{
                r = xml.getInnerXML();
            }

            if(addSlashes)
                r = RegexHelper.addSlashes(r);

            //The <regex> element has attributes for the token to inherit
            if(xml.getAttributeMap().size()>1){
                //put r inside a capture group in order to identify its match
                r = "("+r+")";
                adjustMap(xml.getAttributeMap(),index);
                inheritMap.put(index,xml.getAttributeMap());
            }

            index+= java.util.regex.Pattern.compile(r).matcher("").groupCount();

            strings.add(r);

        }
        return strings;
    }

    /**
     * Used for the tokenizing logic that stores data inside the attribute HashMap
     * Attributes
     *      value="group(6)"
     *      group(6)="23"
     *
     *      becomes
     *      value="23"
     *      group(6)="23"
     *
     *      where the attribute called group(6) was generated by lexer logic. This attribute will be removed
     *      before handed back to the user
     * @param map
     */
    private void crossHatch(HashMap<String,Object> map){
        Iterator<String> iter = map.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            String value = map.get(key).toString();
            if(value.matches("group\\(\\d+\\)")){
                map.put(key,map.get(value));
            }
        }
    }

    /**
     * Adjusts the group() function call in attributes for tokens
     * group(x) becomes group(x+adjustment)
     * @param map
     * @param adjustment
     */
    private void adjustMap(HashMap<String,String> map,int adjustment){
        Iterator<String> iter = map.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            String value = map.get(key);
            if(value.matches("group\\(\\d+\\)")){
                int val = Integer.parseInt(value.substring(6,value.length()-1));
                map.put(key,"group("+(val+adjustment)+")");
            }


        }
    }

    private List<XMLElement> getTokenRules(XMLparser xmLparser){
        return xmLparser.getXMLElements("XMLBody.tokens.token");
    }



    /**
     * Returns some strings to tokenize
     * @return
     */
    public static List<String> getTestStrings(){
        List<String> test = new ArrayList<>();

        test.add("Plaintiff Joe Rogan submitted a motion to dismiss");
        test.add("The Wonder Years' second full-length album, The Upsides, was released on January 26, 2010.");
        test.add("The Upsides sold 1852 units in its first week, landing it on multiple Billboard charts:");
        test.add("No. 5 on Alternative Artist, No. 9 on Top New Artist, No. 26 on Top Internet Album, and No. 43 on Indie Label Album.[3]");
        test.add("A few months after the release of The Upsides, on May 27, The Wonder Years announced that they had signed to Hopeless Records.");

        return test;
    }



    private class TokenGenerator{
        XMLElement e;
        HashMap<Integer,HashMap<String,String>> inheritMap = new HashMap<>();
        String regex;

        public TokenGenerator(XMLElement e){
            inheritMap = new HashMap<>();
            this.e = e;
        }
        public TokenGenerator(XMLElement element,HashMap<Integer,HashMap<String,String>> map){
            inheritMap = map;
            e = element;
        }
        public String toString(){
            return e.getAttribute("type");
        }
    }

}
