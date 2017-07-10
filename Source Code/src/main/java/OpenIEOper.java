import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.Quadruple;

import java.util.Collection;

/**
 * Created by shalin on 7/3/2017.
 */
public class OpenIEOper {
    public static String GetTriplet(String sen) {

        Document docs = new Document(sen);
        String triplet="";
        for (Sentence sent : docs.sentences()) {
            Collection<Quadruple<String, String, String, Double>> lemma=sent.openie();
            for (Quadruple<String, String, String, Double> lem:lemma) {
                if (lem.fourth>0.5) {
                    triplet += lem.toString();
                    System.out.println(triplet);
                }
                else {
                    triplet="Rephrase the question please.";
                }
            }
        }
        return triplet;
    }

}
