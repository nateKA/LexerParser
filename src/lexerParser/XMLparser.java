package lexerParser;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Don't use this XMLParser. It is very specialized and can only parse the xml files structured like the tokens.xml
 * file in resources.files folder. It is used for Lexer and Parser to interpret Tokens, Patterns, and Rules from
 * the xml files. This cannot parse general purpose xml files
 */
public class XMLparser {
    private String text;
    public XMLparser(String filePath){
        String str = "";
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(!line.equals("")){
                    str+=line;
                }
            }
            text = str;
        }catch (Exception e){
            text = "";
            e.printStackTrace();
        }

        text = text.replaceAll("<!--.*?-->","");
        text = fixXML(text);
    }

    protected List<XMLElement> getXMLElements(String path, String text){
        return getXMLElementsHelper(path,text,"idk_yet_lol", "none");
    }
    protected List<XMLElement> getXMLElements(String path){
        return getXMLElementsHelper(path,text,"idk_yet_lol", "none");
    }
    private List<XMLElement> getXMLElementsHelper(String path, String text,String tag, String attributes){
        List<XMLElement> tokens = new ArrayList<>();
        String[] pathParts = path.split("\\.");

        if(pathParts[0].trim().equals("")){
            XMLElement t = new XMLElement(tag);

            Matcher m2 = java.util.regex.Pattern.compile("(\\w+)\\s*=\\s*\"(.*?)\"").matcher(attributes);
            while(m2.find()){
                MatchResult r2 = m2.toMatchResult();
                t.addAttribute(r2.group(1),r2.group(2));
            }

            t.setInnerXML(text.trim());
            tokens.add(t);
        }else{
            String regex = "(<"+pathParts[0]+"\\s*((\\w+\\s*=\\s*(\"[^\"]*\")\\s*)*)>(.*?)</"+pathParts[0]+".*?>)";
            Matcher matcher = java.util.regex.Pattern
                    .compile(regex).matcher(text);

            String newPath = "";
            for(int i = 1; i < pathParts.length; i++){
                if(i>1){
                    newPath += ".";
                }
                newPath+=pathParts[i];
            }

            while(matcher.find()){
                MatchResult r = matcher.toMatchResult();
                    String newText = r.group(r.groupCount());
                    String atts = r.group(2);
                    tokens.addAll(getXMLElementsHelper(newPath, newText, pathParts[0], atts));
            }
        }

        return tokens;
    }

    protected String fixXML(String str){
        String regex = "<(\\w+)\\s*((\\w+\\s*=\\s*(\"[^\"]*\")\\s*)*)/>";
        Pattern p =  Pattern.compile(regex);
            Matcher m =p.matcher(str);
            while(m.find()){
                MatchResult r = m.toMatchResult();
                String match = r.group();
                String tag = r.group(1);
                String atts = r.group(2);
                String fix = String.format("<%s %s></%s>",tag,atts,tag);
                str = str.replaceFirst(regex,fix);

                m = p.matcher(str);
            }
        return str;

    }



}
