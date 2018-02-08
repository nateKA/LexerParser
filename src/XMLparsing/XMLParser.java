package XMLparsing;

import lexerParser.Parser;
import lexerParser.PatternResult;
import lexerParser.RuleResult;
import lexerParser.Token;
import resources.util.Utilities;

import javax.rmi.CORBA.Util;
import java.io.File;
import java.util.List;
import java.util.Scanner;

import static java.awt.SystemColor.text;

public class XMLParser {
    public String prepareFile(String xmlPath){
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
        return xml.replaceAll("<!--[\\s\\S]*?-->", "");
    }

    public static void main(String[] args){
        XMLParser xmlParser = new XMLParser();
        String xml = xmlParser.prepareFile("src/resources/files/tokens.xml");
        System.out.println(xml);
    }
}
