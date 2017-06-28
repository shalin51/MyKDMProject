import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import sun.misc.CharacterEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shalin on 6/16/2017.
 */
public class QuestionAns {
   static NLPOperations nlpOp=new NLPOperations();
    static  String placeData="E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\placeData.txt";
    static  String nameData="E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\nameData.txt";
    static  String nounData="E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\nounData.txt";
    static  String data="E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\nounData.txt";


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
            case "is":formattedAnswer=ans;
                break;
            default:formattedAnswer=formattedAnswer;
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
            case "which":ans=GetPlace(lemmatizedQuestion);
                break;
            case "what":ans=GetNoun(lemmatizedQuestion);
                break;
            case "is":ans=MatchSentance(lemmatizedQuestion);
                break;
            default:ans="I don't have answer";
                break;
        }
        return ans;
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
        for (String word:data.split(" ")) {
            for (String lemma : lemmatizedQuestion)
                if (lemma.equals(word)) {
                    return  "Yes";
                }
        }
        return isMatched;
    }

    private static String GetPlace(List<String> lemmatizedQuestion) {
        String place="";
        for (String word:placeData.split(" ")) {
            for (String lemma : lemmatizedQuestion)
                if (lemma.equals(word)) {
                    String tokenNumber= GetTokenNumber(word);
                    place=SearchPlace(tokenNumber);
                    break;
                }
        }
        return place;
    }

    private static String SearchPlace(String tokenNumber) {
        String place="";
        for (String word:placeData.split(" ")) {
            if (word.contains(tokenNumber))
            {
                place= word.split("-")[0];
            }
        }
        return place;
    }

    private static String GetName(List<String> lemmatizedQuestion) {
        String name="";
        for (String word:data.split(" ")) {
            for (String lemma : lemmatizedQuestion)
                if (lemma.equals(word)) {
                    String tokenNumber= GetTokenNumber(word);
                    name=SearchName(tokenNumber);
                    break;
                }
        }
        return name;
    }

    private static String SearchName(String tokenNumber) {
        String name="";
        for (String word:nameData.split(" ")) {
            if (word.contains(tokenNumber))
            {
                name= word.split("-")[0];
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
                questionType = "when";
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
