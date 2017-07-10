import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import sun.misc.CharacterEncoder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by shalin on 6/16/2017.
 */
public class QuestionAns {

    static  String placeData="E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\placeData.txt";
    static  String nameData="E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\nameData.txt";
    static  String sentenceFile="data\\SentencesFile.txt";
    static  String nounData="E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\nounData.txt";

    final static NLPOperations nlpOp=new NLPOperations();
    final static StanfordCoreNLP nlpobj= nlpOp.GetNLPObject();

    static String GetFinalAns(String questionType, String ans, String formattedQuestion){
        String formattedAnswer="Sorry I don't have Answer";
        ans=ans.toLowerCase();
        switch(questionType) {
            case "who":{
                ans=ans.toUpperCase();
                formattedAnswer=ans+formattedQuestion;
            }
                break;
            case "which":formattedAnswer=formattedQuestion+" "+ans;
                break;
            case "what":formattedAnswer=formattedQuestion+" "+ans;
                break;
            case "when":formattedAnswer=formattedQuestion+" "+ans;
                break;
            case "where":formattedAnswer=formattedQuestion+" "+ans;
                break;
            default:formattedAnswer=ans;
                break;
        }

        formattedAnswer=formattedAnswer.replace(formattedAnswer.charAt(1), Character.toUpperCase(formattedAnswer.charAt(1)));

        return  formattedAnswer;
    }

  /*  static List<String> LemmatiseTheQuestion(String question){
        List<String> lemmatizedQuestion;
        StanfordCoreNLP nlpObj= nlpOp.GetNLPObject();
        Annotation annotedData= nlpOp.AnnotateData(question,nlpObj);
        List<CoreMap> annotedQuestion= nlpOp.GetSentencesFromAnnotatedData(annotedData);
        lemmatizedQuestion=nlpOp.GetLemmas(annotedQuestion);
        return lemmatizedQuestion;
    }*/

    static String FindOutAnswer( List<String> lemmatizedQuestion,String questionType) {
        String ans="";
        switch(questionType) {
            case "who":ans=GetName(lemmatizedQuestion);
                break;
            case "where":ans=GetPlace(lemmatizedQuestion);
                break;
            case "what":ans=GetNoun(lemmatizedQuestion);
                break;
            case "when":ans=GetTime(lemmatizedQuestion);
                break;
            default:ans=MatchSentance(lemmatizedQuestion);;
                break;
        }
        return ans;
    }

    private static String GetTime(List<String> lemmatizedQuestion) {
        String time="";
        try {
            Scanner lineScanner = new Scanner(new File(sentenceFile));
            while (lineScanner.hasNextLine()){
                String line=lineScanner.nextLine();
                Scanner wordScanner=new Scanner(line);
                while(wordScanner.hasNext()) {
                    String word = wordScanner.next();
                    for (String lemma : lemmatizedQuestion)
                        if (lemma.contains(word)) {
                            time = SearchDate(line);
                            if(time!="")
                            break;
                        }
                    if(time!="")
                        break;
                }
            }
        }catch (IOException io) {
            time="";
        }finally {
            return time;
        }
    }

    private static String SearchDate(String line) {
        String date="";
        Annotation annotatedInputData = nlpOp.AnnotateData(line, nlpobj);
        List<CoreMap> annotatedSentace= nlpOp.GetSentencesFromAnnotatedData(annotatedInputData);
        for (CoreMap sentance:annotatedSentace) {
            Map<String, List<String>> NEData = nlpOp.GetNER(sentance);
            SortedSet<String> keyset = new TreeSet<String>(NEData.keySet());
            String mapKey=keyset.first();
            List<String> mapValue= NEData.get(mapKey);
            for (int i=0;i<=mapValue.size();i++) {
                if(mapValue.get(i).contains("DATE")){
                    date =date+mapValue.get(i-1).toString()+" ";
                    if (!mapValue.get(i+2).contains("DATE"))
                        break;
                }
            }
        }
        return date;
    }
    private static String GetNoun(List<String> lemmatizedQuestion) {
        String noun="";
        for (String word:nounData.split(" ")) {
            for (String lemma : lemmatizedQuestion)
                if (lemma.equals(word)) {
                   String tokenNumber= GetTokenNumber(word);
                    noun=SearchNoun(tokenNumber);
                    break;
                }
        }
        return noun;
    }

    private static String SearchNoun(String tokenNumber) {
        String noun="";
        for (String word:nounData.split(" ")) {
            if (word.contains(tokenNumber))
            {
                noun= word.split("-")[0];
            }
        }
        return noun;
    }

    private static String GetTokenNumber(String word) {
        return word.split("-")[1];
    }

    private static String MatchSentance(List<String> lemmatizedQuestion) {
        String isMatched="No";
        for (String word:nounData.split(" ")) {
            for (String lemma : lemmatizedQuestion)
                if (lemma.equals(word)) {
                    return  "Yes";
                }
        }
        return isMatched;
    }

    private static String GetPlace(List<String> lemmatizedQuestion) {
        String place="";
        try {
            Scanner lineScanner = new Scanner(new File(sentenceFile));
            while (lineScanner.hasNextLine()){
                String line=lineScanner.nextLine();
                Scanner wordScanner=new Scanner(line);
                while(wordScanner.hasNext()) {
                    String word = wordScanner.next();
                    for (String lemma : lemmatizedQuestion)
                        if (lemma.contains(word)) {
                            place = SearchPlace(line);
                            if(place!="")
                                break;
                        }
                    if(place!="")
                        break;
                }
            }
        }catch (IOException io) {
            place="";
        }finally {
            return place;
        }
    }

    private static String SearchPlace(String line) {
        String place="";
        Annotation annotatedInputData = nlpOp.AnnotateData(line, nlpobj);
        List<CoreMap> annotatedSentace= nlpOp.GetSentencesFromAnnotatedData(annotatedInputData);
        for (CoreMap sentance:annotatedSentace) {
            Map<String, List<String>> NEData = nlpOp.GetNER(sentance);
            SortedSet<String> keyset = new TreeSet<String>(NEData.keySet());
            String mapKey=keyset.first();
                List<String> mapValue= NEData.get(mapKey);
                for (String val:mapValue) {
                    if(val.contains("LOCATION")){
                        place =place+mapValue.get( mapValue.indexOf(val)-1).toString()+" ";
                        if (place!="")
                        break;
                    }
                }
        }
        return place;
    }

    private static String GetName(List<String> lemmatizedQuestion) {
        String name="";
        try {
            Scanner lineScanner = new Scanner(new File(sentenceFile));
            while (lineScanner.hasNextLine()){
                String line=lineScanner.nextLine();
                Scanner wordScanner=new Scanner(line);
                while(wordScanner.hasNext()) {
                    String word = wordScanner.next();
                        for (String lemma : lemmatizedQuestion)
                            if (lemma.contains(word)) {
                                name = SearchName(line);
                                break;
                            }
                }
            }
        }catch (IOException io) {
        name="";
        }finally {
            return name;
        }
    }

    private static String SearchName(String line) {
        String name="";
        Annotation annotatedInputData = nlpOp.AnnotateData(line, nlpobj);
        List<CoreMap> annotatedSentace= nlpOp.GetSentencesFromAnnotatedData(annotatedInputData);
        for (CoreMap sentance:annotatedSentace) {
            Map<String, List<String>> NEData = nlpOp.GetNER(sentance);
            SortedSet<String> keyset = new TreeSet<String>(NEData.keySet());
            String mapKey=keyset.first();
            List<String> mapValue= NEData.get(mapKey);
            for (String val:mapValue) {
                if(val.contains("PERSON")){
                    name =name+mapValue.get( mapValue.indexOf(val)-1).toString()+" ";
                    if (name!="")
                        break;
                }
            }
        }
        return name;
    }

    static Boolean GotTheAnswer(String answer){
          if (answer.equals("I don't have answer")){
                return  false;
          }
          else
              return  true;
        }

    static List<String> FormatQuestion(String question){
        question=question.toLowerCase();
        question= removePunctuationAndClean(question);
        String formattedQuestion="";
        String questionType="";
        List<String> questionLst=new ArrayList<>();

        String[] questionWords= question.split(" ");

        switch(questionWords[0]) {
            case "who": {
                String temp = "";
                questionType = "who";
                for (int i = 1; i <= questionWords.length - 1; i++) {
                    temp = temp + " " + questionWords[i];
                }
                formattedQuestion = temp;
            }
                break;
            case "which":{
                String temp = "";
                questionType = "which";
                for (int i = 2; i <= questionWords.length - 1; i++) {
                    temp = temp + " " + questionWords[i];
                }
                formattedQuestion = temp+" "+questionWords[1];
            }
                break;
            case "what":{
                String temp = "";
                questionType = "what";
                for (int i = 2; i <= questionWords.length - 1; i++) {
                    temp = temp + " " + questionWords[i];
                }
                formattedQuestion = temp+" "+questionWords[1];
            }
            break;
            case "when":{
                String temp = "";
                questionType = "when";
                for (int i = 3; i <= questionWords.length - 1; i++) {
                    temp = temp + " " + questionWords[i];
                }
                formattedQuestion = " "+questionWords[2]+" "+questionWords[1]+temp+" "+"on";
            }
                break;
            case "where":{
                String temp = "";
                questionType = "where";
                for (int i = 3; i <= questionWords.length - 1; i++) {
                    temp = temp + " " + questionWords[i];
                }
                formattedQuestion = " "+questionWords[2]+" "+questionWords[1]+temp+" "+"to";
            }
            break;
            case "is":{
                String temp = "";
                questionType = "is";
                for (int i = 2; i <= questionWords.length - 1; i++) {
                    temp = temp + " " + questionWords[i];
                }
                formattedQuestion = temp+" "+questionWords[1];
            }
                break;
            default:formattedQuestion="";
                break;
        }

        questionLst.add(questionType);
        questionLst.add(formattedQuestion);
        return questionLst;
    }

    private static String removePunctuationAndClean(String stringToBeClean) {
        String cleanStr="";
        cleanStr=stringToBeClean.replaceAll("\\s+"," ").replaceAll("[?]","");
        return cleanStr;
    }


}
