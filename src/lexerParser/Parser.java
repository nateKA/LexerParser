package lexerParser;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import resources.util.RegexHelper;
import resources.util.Utilities;

import javax.rmi.CORBA.Util;
import java.util.*;

public class Parser {

    private List<Pattern> patternList = new ArrayList<>();
    private Lexer lexer = null;

    public void compile(String xmlPath){
        if(lexer==null)
            lexer = new Lexer();
        lexer.compile(xmlPath);
        XMLparser xml = new XMLparser(xmlPath);

        List<XMLElement> partternElements = xml.getXMLElements("XMLBody.patterns.pattern");
        for(XMLElement pXML: partternElements){
            Pattern p = new Pattern();
            copyMap(p.getAttributes(),pXML,Utilities.TAG,Utilities.MIN,Utilities.MAX);
            p.getRules().addAll(getRules(pXML, xml));
            if(p.getAttributes().containsKey("sequence")){
                int i = Integer.parseInt(p.get("sequence"));
                if(i <= patternList.size())
                    patternList.add(i,p);
                else
                    patternList.add(p);
                p.getAttributes().remove("sequence");
            }else {
                patternList.add(p);
            }
        }
    }

    private List<Rule> getRules(XMLElement pXML,XMLparser xml){
        List<XMLElement> ruleXMLs = xml.getXMLElements("rule",pXML.getInnerXML());
        List<Rule> rules = new ArrayList<>();
        for(XMLElement rXML: ruleXMLs){
            Rule rule = new Rule();
            copyMap(rule.getAttributes(),rXML,Utilities.TAG,Utilities.MIN,Utilities.MAX);
            if(rXML.getAttributes().containsKey(Utilities.MIN))
                rule.setMin(Integer.parseInt(rXML.getAttribute(Utilities.MIN)));
            if(rXML.getAttributes().containsKey(Utilities.MAX))
                rule.setMax(Integer.parseInt(rXML.getAttribute(Utilities.MAX)));
            rule.getRubrics().addAll(getRubrics(rXML,xml));
            rules.add(rule);
        }

        return rules;
    }

    private List<Rubric> getRubrics(XMLElement RuleXML, XMLparser xml){
        List<XMLElement> ruleXMLs = xml.getXMLElements("rubric",RuleXML.getInnerXML());
        List<Rubric> rubrics = new ArrayList<>();
        for(XMLElement rXML: ruleXMLs){
            String[] checks = getChecks(rXML,xml);
            int min = 1;
            int max = 1;
            if(rXML.getAttributes().containsKey("min")){
                min = Integer.parseInt(rXML.getAttribute("min"));
            }
            if(rXML.getAttributes().containsKey("max")){
                max = Integer.parseInt(rXML.getAttribute("max"));
            }
            Rubric rubric = new Rubric(min,max,checks);
            rubrics.add(rubric);
        }

        return rubrics;
    }
    private String[] getChecks(XMLElement rXML, XMLparser xml){
        List<String> list = new ArrayList<>();
        List<XMLElement> ruleXMLs = xml.getXMLElements("check",rXML.getInnerXML());
        for(XMLElement cXML: ruleXMLs){
            list.add(cXML.getInnerXML().trim());
        }

        String[] retList = new String[list.size()];
        for(int i = 0; i < list.size(); i++){
            retList[i] = list.get(i);
        }
        return retList;
    }
    private void copyMap(HashMap<String,String> p, XMLElement x,String...ignore){
        HashMap<String,String> map = x.getAttributeMap();
        Iterator<String> iter = map.keySet().iterator();
        while(iter.hasNext()){
            String key = iter.next();
            boolean add = true;
            for(String s: ignore){
                if(s.equals(key)) {
                    add = false;
                    break;
                }
            }
            if(add)
            p.put(key,map.get(key));
        }
    }

    public List<PatternResult> extractPatterns(String text){
        List<Token> tokens = lexer.tokenize(text);
        return extractPatterns(text,tokens);
    }
    public List<PatternResult> extractPatterns(String text,List<Token> tokens){
        List<PatternResult> list = new ArrayList<>();
        for(int i = 0; i < tokens.size(); i++){
            for(Pattern p: patternList){
                PatternResult result = p.testTokens(tokens,i,text);
                if(result!=null) {
                    list.add(result);
                    i += result.size()-1;
                }
            }
        }
        list = RegexHelper.breakApartPatterns(list);

        return list;
    }

    public List<Token> parse(String text){
        List<Token> tokens = lexer.tokenize(text);
        List<PatternResult> patterns = extractPatterns(text,tokens);
        return mergePatternsToTokens(tokens,patterns);
    }
    private void breakApartPatterns(List<PatternResult> patterns){
        List<Token> tokens = new ArrayList<>();
        for(PatternResult pr: patterns){
            tokens.add(pr.getTokensAtPatternLevel());
        }
        RegexHelper.breakApart(tokens);
    }

    public List<Token> mergePatternsToTokens(List<Token> tokens, List<PatternResult> patterns){
        List<Token> pTokens = new ArrayList<>();
        pTokens.addAll(tokens);
        for(PatternResult pr: patterns){
            pTokens.add(pr.getGroup());
        }
        return RegexHelper.breakApart(pTokens);
    }


    public static void main(String[] args){
        Parser p = new Parser();
        String text = "The Wonder Years' second full-length album, The Upsides, was released on January 26, 2010.";
        p.compile("src/resources/files/tokens.xml");
        List<Token> list = null;
        list = p.parse(text);
        Utilities.printTokensAndIgnoreAtts(list,text);

        System.out.println();

        Utilities.printTokensAndIgnoreAtts(
                p.parse("Mayday Parade's debut EP 'Tales Told by Dead Friends' was released in 2006, and sold over 50,000 copies without any label support.")
                ,"Mayday Parade's debut EP 'Tales Told by Dead Friends' was released in 2006, and sold over 50,000 copies without any label support.");
        p.lexer.printReport();

    }
}
