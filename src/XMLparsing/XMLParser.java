package XMLparsing;

import lexerParser.*;
import org.omg.CORBA.MARSHAL;
import resources.util.Utilities;

import javax.rmi.CORBA.Util;
import java.io.File;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import static java.awt.SystemColor.text;

public class XMLParser {
    private String prepareFile(String xmlPath){
        String xml = "";
        try {
            Scanner s = new Scanner(new File(xmlPath));
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.trim().equals("")) continue;
                xml+=line.trim()+"\n";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return xml.replaceAll("<!--[\\s\\S]*?-->", "")
                .replaceAll("\n+"," ");
    }

    public XMLDocument parseXML(String filePath){
        String xml = prepareFile(filePath);

        Scanner scanner = new Scanner(xml);
        Parser parser = new Parser();
        parser.compile("src/resources/files/xmlTokens.xml");
        List<PatternResult> allPatterns = new ArrayList<>();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            List<PatternResult> patterns = parser.extractPatterns(line);
            allPatterns.addAll(patterns);
        }


        XMLElement tree = buildTree(allPatterns);
        XMLDocument doc = new XMLDocument(tree);
        if(isProlog(allPatterns.get(0))){
            doc.setProlog(xmlElementFromPattern(allPatterns.get(0)));
        }

        return doc;
    }

    private XMLElement buildTree(List<PatternResult> patterns){
        int start = 0;
        if(isProlog(patterns.get(0))){
            start = 1;
        }
        if(isOpenTag(patterns.get(start))){
            XMLElement root = xmlElementFromPattern(patterns.get(start));
            HashMap<Integer,XMLElement> rootMap = new HashMap<>();
            rootMap.put(start,root);
            int depth = start+1;
            for(int i = depth; i < patterns.size();i++){
                PatternResult curPR = patterns.get(i);
                if(isOpenTag(curPR)){
                    XMLElement open = xmlElementFromPattern(patterns.get(i));
                    rootMap.get(depth-1).addSubElement(open);
                    rootMap.put(depth++,open);
                }else if(isCloseTag(curPR)){
                    depth--;
                }else if(isPhrase(curPR)){
                    rootMap.get(depth-1).setInnerXML(
                            curPR.getGroup().getString(Utilities.FINDING_REGEX_FIELD_NAME));
                }else if(isProlog(curPR)){
                    System.out.println("Also found Prolog");
                }

            }
            return rootMap.get(start);

        }else{
            throw new IllegalArgumentException("Start of XML is not an open tag format:\n\t"+
            patterns.get(0) +" : should look like -> <tagName attribute=\"attValue\">");
        }
    }

    private XMLElement xmlElementFromPattern(PatternResult pr){
        XMLElement e = new XMLElement(getTag(pr));
        for(Token t: pr.getTokensAtRuleLevel("type","attribute")){
            Matcher m = java.util.regex.Pattern.compile("(\\w+)=\"(.*?)\"")
                    .matcher(t.getString(Utilities.FINDING_REGEX_FIELD_NAME));
            if(m.find()){
                MatchResult res = m.toMatchResult();
                e.addAttribute(res.group(1),res.group(2));
            }
        }
        return e;
    }

    private String getTag(PatternResult p){
        return p.getTokensAtRuleLevel("type","tag").get(0).getString(Utilities.FINDING_REGEX_FIELD_NAME);
    }

    private boolean isOpenTag(PatternResult p){
        return p.getGroup().getString("type").equals("openTag");
    }
    private boolean isPhrase(PatternResult p){
        return p.getGroup().getString("type").equals("phrase");
    }
    private boolean isCloseTag(PatternResult p){
        return p.getGroup().getString("type").equals("closeTag");
    }
    private boolean isProlog(PatternResult p){ return p.getGroup().getString("type").equals("prolog");}

    public static void main(String[] args){
        XMLParser parser = new XMLParser();
        XMLDocument doc = parser.parseXML("src/resources/files/books.xml");
        for(XMLElement e: doc.collect("catalog.book", "title","price")){
            System.out.println(e);
        }
    }
}
