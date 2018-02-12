package resources.util;

import lexerParser.Lexer;
import lexerParser.Parser;
import lexerParser.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utilities {

    public static final String FINDING_REGEX_FIELD_NAME = "annotationText()";
    public static final String FINDING_START_INDEX_NAME = "startOffset()";
    public static final String FINDING_END_INDEX_NAME = "endOffset()";
    public static final String TAG = "XMLtag";
    public static final String IGNORE_CASE = "ignoreCase";
    public static final String BOUNDARIES = "boundaries";
    public static final String ADD_SLASHES = "addSlashes";
    public static final String MIN = "min";
    public static final String MAX = "max";

    public static final String[] DO_NOT_COPY_ATTRIBUTES = new String[]{
            TAG,IGNORE_CASE,BOUNDARIES,ADD_SLASHES
    };
    public static final String[] RESERVED_ATTRIBUTES = new String[]{
            FINDING_REGEX_FIELD_NAME,FINDING_START_INDEX_NAME,FINDING_END_INDEX_NAME,
    };

    public static Lexer getCommonLexer(){
        Lexer lexer = new Lexer();
        lexer.compile("src/resources/files/tokens.xml");
        return lexer;
    }
    public static Parser getCommonParser(){
        Parser parser = new Parser();
        parser.compile("src/resources/files/tokens.xml");
        return parser;
    }

    public static void printTokens(List<Token> tokens, String origin, String...fields){
        System.out.println("Origin = \""+origin+"\"");
        String[] row = new String[fields.length];
        int max = 1;
        for(String s: fields){
            if(s==null)continue;
            if(s.length() > max)
                max = s.length();
        }
        for(int i = 0; i < row.length; i++){
            row[i] = String.format("|%1$-" + max + "s", fields[i]);
        }
        for(Token t: tokens) {
            int maxL = 1;
            for(String s: fields){
                s = t.getString(s);
                if(s==null)continue;
                if(s.length() > maxL)
                    maxL = s.length();
            }
            for(int i = 0; i < row.length; i++) {
                String s = t.getString(fields[i]);
                row[i] += String.format("|%1$-" + maxL + "s", (s==null)?"":s);
            }
        }

        for(String s: row){
            System.out.println(s);
        }
        System.out.println();
    }
    public static void printTokensAndIgnoreAtts(List<Token> tokens,String origin,String...fields){
        List<String> list = new ArrayList<>();
        for(Token t: tokens){
            Iterator<String> iter = t.getAttributes().keySet().iterator();
            while(iter.hasNext()){
                String key = iter.next();

                if(list.contains(key))
                    continue;

                boolean include = true;
                for(String s: fields){
                    if(s.equals(key)){
                        include = false;
                        break;
                    }
                }
                if(include)
                    list.add(key);
            }
        }
        String[] use = new String[list.size()];
        for(int i = 0; i < use.length; i++){
            use[i] = list.get(i);
        }
        printTokens(tokens,origin, use);
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
}
