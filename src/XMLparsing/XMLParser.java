package XMLparsing;

import lexerParser.Parser;
import lexerParser.PatternResult;
import lexerParser.RuleResult;
import lexerParser.Token;
import resources.util.Utilities;

import javax.rmi.CORBA.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.awt.SystemColor.text;

/**
 * A sample class that uses the Lexer/Parser to demonstrate how to use the package
 */
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
        return xml.replaceAll("<!--[\\s\\S]*?-->", "")//remove comments
                .replaceAll("\n{2,}","\n");//remove extra \n
    }

    public static void main(String[] args){
        XMLParser xmlParser = new XMLParser();//This class
        //The Parser tool to help us
        Parser parser = new Parser();
        //Tells Lexer how to Tokenize, and Parser how to parse
        parser.compile("src/resources/files/xmlTokens.xml");

        String xml = xmlParser.prepareFile("src/resources/files/tokens.xml");

        Scanner xmlScanner = new Scanner(xml);
        List<Token> allTokens = new ArrayList<>();
        while(xmlScanner.hasNextLine()){
            String line = xmlScanner.nextLine();
            List<Token> lineTokens = parser.parse(line);
            allTokens.addAll(lineTokens);

            //Makes a visual representation of our Tokens, so we can verify correctness
            Utilities.printTokens(lineTokens,line,Utilities.FINDING_REGEX_FIELD_NAME,"type","value");
            System.out.println();
        }

        parser.printLexerReport();
    }
}
