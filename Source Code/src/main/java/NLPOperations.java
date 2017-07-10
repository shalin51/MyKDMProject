
import java.io.IOException;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import sun.nio.cs.StandardCharsets;

/**
 * Created by shalin on 6/16/2017.
 */
public class NLPOperations {



    public StanfordCoreNLP GetNLPObject() {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties prop = new Properties();
        prop.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(prop);
        return pipeline;
    }

    public  static  String returnLemma(String sentence) {

        Document doc = new Document(sentence);
        String lemma="";
        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences

            List<String> l=sent.lemmas();
            for (int i = 0; i < l.size() ; i++) {
                lemma+= l.get(i) +" ";
            }
            //   System.out.println(lemma);
        }

        return lemma;
    }

    public Annotation AnnotateData(String data,StanfordCoreNLP pipe){
        Annotation annotatedDatadata = new Annotation(data);
        pipe.annotate(annotatedDatadata);
    return  annotatedDatadata;
    }

    public List<CoreMap> GetSentencesFromAnnotatedData(Annotation annotatedDatadata){
        List<CoreMap> SentencesFromAnnotatedData = annotatedDatadata.get(CoreAnnotations.SentencesAnnotation.class);
        return SentencesFromAnnotatedData;
    }

    public List<String> GetLemmas(CoreMap sentence,String sentenceNumber) {
        List<String> lemmaList= new ArrayList<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                 String tokenNumber=token.toString().split("-")[1];
                String tokenString=token.toString().split("-")[0];
                lemmaList.add("("+sentenceNumber+","+tokenNumber+","+tokenString+","+token.get(CoreAnnotations.LemmaAnnotation.class)+")");
            }
        return lemmaList;
    };

    public List<String> GetLemmas(CoreMap sentence) {
        List<String> lemmaList= new ArrayList<>();
        for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            lemmaList.add(token.get(CoreAnnotations.LemmaAnnotation.class));
        }
        return lemmaList;
    };

    public List<String> GetToken(CoreMap sentence){
            List<String> tokenList=new ArrayList<>();
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    tokenList.add(token.toString());
            }
        return tokenList;
    }

    public String GetTopic(List<String> queLst){
        String topic="";
        String ldaFilePath="data/LDAOutput.txt";
        try {
           String ldaOutput= ReadWriteOperation.ReadFile(ldaFilePath, java.nio.charset.StandardCharsets.UTF_8);
            for (String que : queLst) {
                if(ldaOutput.contains(que)){
                    topic=que;
                    break;
                }
            }
        }
        catch (IOException ioExe)
        {
             topic="";
        }
        finally {
            return topic;
        }

    }

    public List<String> GetPOS(CoreMap sentence,String sentenceNumber) {
        List<String> POSList= new ArrayList<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String tokenNumber=token.toString().split("-")[1];
                String tokenString=token.toString().split("-")[0];
                POSList.add("("+sentenceNumber+","+tokenNumber+","+tokenString+","+token.get(CoreAnnotations.PartOfSpeechAnnotation.class)+")");
        }
        return POSList;
    }

    public Set<String> GetTextAnnotation(List<CoreMap> sentences) {
        Set<String> textList = new HashSet<>();
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                textList.add(token.get(CoreAnnotations.TextAnnotation.class));
            }
        }
        return textList;
    }



    public List<String> GetNER(CoreMap sentence,String sentenceNumber) {
        List<String> NERList = new ArrayList<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String tokenNumber=token.toString().split("-")[1];
                String tokenString=token.toString().split("-")[0];
                NERList.add("("+sentenceNumber+","+tokenNumber+","+tokenString+","+token.get(CoreAnnotations.NamedEntityTagAnnotation.class)+")");
            }

        return NERList;
    }

    public Map<String,String> GetNERR(CoreMap sentence) {
        Map<String,String> NERList = new HashMap<>();
        for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String tokenNumber=token.toString().split("-")[1];
            String tokenString=token.toString().split("-")[0];
            NERList.put(tokenString,token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
        }

        return NERList;
    }

    public Map<String,List<String>> GetNER(CoreMap sentence) {
        Map<String,List<String>> NERList = new HashMap<>();
        List<String> tokenList=new ArrayList<>();
        for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
            String tokenNumber=token.toString().split("-")[1];
            String tokenString=token.toString().split("-")[0];
            tokenList.add(tokenString);
            tokenList.add(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
            NERList.put(tokenNumber,tokenList);
        }

        return NERList;
    }
    public Set<Tree> GetTree(List<CoreMap> sentences) {
        Set<Tree> treeList = new HashSet<>();
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                treeList.add(sentence.get(TreeCoreAnnotations.TreeAnnotation.class));
            }
        }
        return treeList;
    }



    public Set<SemanticGraph> GetSemanticGraph(List<CoreMap> sentences) {
        Set<SemanticGraph> semanticGraphList = new HashSet<>();
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                semanticGraphList.add(sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class));
            }
        }
        return semanticGraphList;
    }

}
