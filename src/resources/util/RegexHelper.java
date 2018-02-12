package resources.util;

import lexerParser.PatternResult;
import lexerParser.Token;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {
    public static List<Token> search(String text,String regex, boolean ignoreCase){
        List<Token> tokens = new ArrayList<>();
        if(ignoreCase){
            regex = "(?i)"+regex;
        }

        Matcher m = Pattern.compile(regex).matcher(text);
        while(m.find()){
            Token t = new Token(m.toMatchResult());

            MatchResult r = m.toMatchResult();
            int group = 0;
            for(int i = 2; i <= r.groupCount(); i++){
                if(r.group(i)!=null) {
                    if(t.getInfo("group")==null)
                    t.putInfo("group",i);
                    t.putInfo("group("+i+")",r.group(i));
                }
            }

            tokens.add(t);
        }
        return tokens;
    }

    public static String createRegexFromList(List<String> list, boolean boundaries){
        if(list.size()==0){
            return ".";
        }
        String regex = "";

        for(int i = 0; i < list.size(); i++){
            if(i > 0){
                regex += "|";
            }
            regex += list.get(i);
        }

        regex = "("+regex+")";
        if(boundaries){
            regex = "\\b"+regex+"\\b";
        }

        return  regex;
    }

    public static String addSlashes(String str){
        return str.replaceAll("\\.","\\.")
                .replaceAll("\\*","\\*")
                .replaceAll("\\{","\\{")
                .replaceAll("\\?","\\?")
                .replaceAll("\\[","\\[");
    }

    public static void sortTokensByStart(List<Token> tokens){
        Collections.sort(tokens, new Comparator<Token>() {
            @Override
            public int compare(Token a1, Token a2) {
                return a1.getInt(Utilities.FINDING_START_INDEX_NAME) - a2.getInt(Utilities.FINDING_START_INDEX_NAME);
            }
        });
    }

    public static List<PatternResult> breakApartPatterns(List<PatternResult> patterns){
        HashMap<Token,PatternResult> map = new HashMap<>();
        List<Token> tokens = new ArrayList<>();
        for(PatternResult pr: patterns){
            tokens.add(pr.getTokensAtPatternLevel());
            map.put(pr.getTokensAtPatternLevel(),pr);
        }
        tokens = breakApart(tokens);
        List<PatternResult> retList = new ArrayList<>();
        for(Token t: tokens){
            PatternResult pr = map.get(t);
            retList.add(pr);
        }

        return retList;
    }
    public static List<Token> breakApart(List<Token> tokens){
        sortTokensByStart(tokens);

        List<Token> ret = new ArrayList<>(tokens);

        for(int i = 0; i < ret.size() - 1; i++){
            for(int j = i+1; j < ret.size(); j++){
                Token left = ret.get(i);
                Token right = ret.get(j);

                if(left.getInt(Utilities.FINDING_END_INDEX_NAME) <
                        right.getInt(Utilities.FINDING_START_INDEX_NAME))
                    break;

                boolean changed = breakHelper(ret, left,right);
                if(changed){
                    i--;
                    break;
                }
            }
        }
        return ret;
    }
    private static boolean breakHelper(List<Token> tokens, Token left, Token right){
        //<check>type=word|month</check>
        String sleft = left.getString(Utilities.FINDING_TEXT_NAME);
        String sright = right.getString(Utilities.FINDING_TEXT_NAME);
        int lstart = left.getInt(Utilities.FINDING_START_INDEX_NAME);
        int lend = left.getInt(Utilities.FINDING_END_INDEX_NAME);
        int rstart = right.getInt(Utilities.FINDING_START_INDEX_NAME);
        int rend = right.getInt(Utilities.FINDING_END_INDEX_NAME);

        //left and right are same space - drop left, keep right
        if(lstart == rstart && lend == rend){
            tokens.remove(left);
            right.copyWithPreservation(left);
            return true;
        }
        //left wraps around right - drop right, keep left
        if(lstart <= rstart && lend >= rend){
            tokens.remove(right);
            return true;
        }

        //right wraps around left - drop left, keep right
        if(rstart == lstart && lend < rend){
            tokens.remove(left);
            return true;
        }

        //left and right overlap partly and are - maintain left, right loses overlap
        if(lstart <= rstart && lend > rstart && lend < rend){
                right.putInfo(Utilities.FINDING_START_INDEX_NAME, lend);
                int overlap = Math.abs(rstart - lend);
                right.putInfo(Utilities.FINDING_TEXT_NAME,
                        right.getString(Utilities.FINDING_TEXT_NAME).substring(overlap));
                return true;
        }
        return false;
    }
}
