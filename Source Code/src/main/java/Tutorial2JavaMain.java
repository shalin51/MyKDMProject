/*
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
;
import edu.stanford.nlp.util.CoreMap;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

*/
/**
 * Created by shali on 6/16/2017.
 *//*

public class Tutorial2JavaMain {
    public static void main(String[] args) {

        try {

            String inputFileName = "E:\\Knowledge Discovery Management\\Datasets\\WikiRef_dataset\\WikiRef_dataset\\WikiRef220\\barack.obama.1.txt";
            String outputFileName = "E:\\Knowledge Discovery Management\\Tutorials\\ScalaTutorial1\\output\\t2output.txt";
            String processedData = "E:\\Knowledge Discovery Management\\Tutorials\\ScalaTutorial1\\output\\processedData.txt";
            String lemmaOutput = "E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\lemmaOutput.txt";
            String textOutput = "E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\lemmaOutput.txt";
            String POSOutput = "E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\lemmaOutput.txt";
            String NEROutput = "E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\lemmaOutput.txt";
            String treeOutput = "E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\treeOutput.txt";
            String semanticGraphOutput = "E:\\Knowledge Discovery Management\\Tutorials\\Tutorial2\\output\\semanticGraphOutput.txt";
            NLPOperations nlp=new NLPOperations();

            // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
            Properties prop = new Properties();
            prop.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(prop);

            // reading text from file
            String text = ReadFile(inputFileName,StandardCharsets.UTF_8);

            // create an empty Annotation just with the given text
            Annotation document = new Annotation(text);

            // run all Annotators on this text
            pipeline.annotate(document);

            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
            Set<String> lemmas= nlp.GetLemmas(sentences);



        } catch (IOException io) {
            System.out.print(io.toString());
        }
    }


    static String ReadFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    static String ReadProcessedData(String outputFileName,String question){
        String ans="Not able to find answer!!!!";
        String[] splitedQuestion = question.split("\\s+");
        String line="";
        String que="who is President";
        boolean hasWord=false;
        try {
            FileReader fileReader =
                    new FileReader(outputFileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);
            List<String> ignoreList = GetIgnoreList();
            String[] questionWords = question.split(" ");

            while ((line = bufferedReader.readLine()) != null) {
                String[] anws= line.split("\"");
                for(String queWord:questionWords)
                {
                    for(String anw:anws)
                    {
                        if(anw.contains(queWord))
                        {
                            ans=anw;
                            break;
                        }
                    }
                }


            }
        }
        catch (IOException io) {
        }
        finally {
            return ans;
        }
    }

    static void WriteToFile(Set<String> lst, String path)
    {
        try{
            FileWriter writer= new FileWriter(path);
            for(String line:lst){
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }

    static void WriteToFile(String str, String path)
    {
        try{
            FileWriter writer= new FileWriter(path);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }



    static List<String> GetIgnoreList(){
        List<String> ignoreList= new ArrayList<>();
        ignoreList.add("who");
        ignoreList.add("what");
        ignoreList.add("how");
        ignoreList.add("is");
        ignoreList.add("that");
        ignoreList.add("are");
        ignoreList.add("why");
        ignoreList.add("the");
        ignoreList.add("he");
        ignoreList.add("of");
        ignoreList.add("the");
        ignoreList.add("in");
        ignoreList.add("from");
        ignoreList.add("his");
        ignoreList.add("her");
        ignoreList.add("a");
        return ignoreList;
    }

}
*/
