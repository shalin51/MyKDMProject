import java.nio.charset.StandardCharsets;
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

    public Annotation AnnotateData(String data,StanfordCoreNLP pipe){
        Annotation annotatedDatadata = new Annotation(data);
        pipe.annotate(annotatedDatadata);
    return  annotatedDatadata;
    }

    public List<CoreMap> GetSentencesFromAnnotatedData(Annotation annotatedDatadata){
        List<CoreMap> SentencesFromAnnotatedData = annotatedDatadata.get(CoreAnnotations.SentencesAnnotation.class);
        return SentencesFromAnnotatedData;
    }

    public Map<String,String> GetLemmas(CoreMap sentence) {
        Map<String,String> lemmaList = new HashMap<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmaList.put("Token : "+ token.toString(),"Lemma : "+token.get(CoreAnnotations.LemmaAnnotation.class));

        }
        return lemmaList;
    }

    public List<String> GetToken(CoreMap sentence){
            List<String> tokenList=new ArrayList<>();
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    tokenList.add(token.toString());
            }
        return tokenList;
    }

    public Map<String,String> GetPOS(CoreMap sentence) {
        Map<String,String> POSDictrionary=new HashMap<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                POSDictrionary.put("Token : "+token.toString(),"POS : "+token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
        }
        return POSDictrionary;
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

    public  Map<String,String> GetNER(CoreMap sentence) {
        Map<String,String> NERList = new HashMap<>();

            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                NERList.put("Token : "+token.toString(),"NER : "+token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
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
