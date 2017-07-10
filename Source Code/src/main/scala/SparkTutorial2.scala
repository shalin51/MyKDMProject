import java.io.File
import java.nio.charset.StandardCharsets
import util.control.Breaks._
import scala.collection._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF, Word2Vec, Word2VecModel}

import scala.collection.immutable.HashMap
import com.sun.xml.internal.bind.api.impl.NameConverter.Standard

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.io.{Source, StdIn}

/**
  * Created by shalin on 7/2/2017.
  */
object SparkTutorials {

  def main(args: Array[String]): Unit = {
    val configurationSpark = new SparkConf()
      .setMaster("local[*]")
      .setAppName("Tutorial2-Spark")
    System.setProperty("hadoop.home.dir", "C:\\winutils")


    val processedData = "E:\\Knowledge Discovery Management\\Tutorials\\ScalaTutorial1\\output\\processedData.txt"
    val sContext = new SparkContext(configurationSpark)
    val lemmaFile = "data\\lemmaFile.txt"
    val POSFile = "data\\POSFile.txt"
    val NERFile = "data\\NERFile.txt"
    val sentencesFile = "C:\\Users\\shali\\OneDrive\\KDM\\Machine_Learning-\\lab 4\\Code\\data\\SentencesFile.txt"


    //val fileData = ReadWriteOperation.ReadFile("data\\inputDocuments.txt", StandardCharsets.UTF_8)

    //val nlpDataList = List[NLPData]()

    val nlpOper = new NLPOperations()
    val nlpObj = nlpOper.GetNLPObject()
   val annotatedInputData = nlpOper.AnnotateData(fileData, nlpObj)
    var sentenceNumber: Int = 0
    val stopwords = sContext.textFile("data/stopwords.txt").collect()

    ReadWriteOperation.RemoveEmptyLines();

    val annotatedSentaces = sContext.parallelize(nlpOper.GetSentencesFromAnnotatedData(annotatedInputData)).collect().map(
      sentence => {
        sentenceNumber = sentenceNumber + 1
        var isStopWord=false
        val brokenSentence = sentence.toString.split(" ")
        var listOfWords = new ListBuffer[String]
        brokenSentence.map(word => {
         breakable( stopwords.foreach(wr =>
            if (word.toLowerCase == wr) {
              isStopWord=true
              break
            }))

          if(!isStopWord)
              {
                listOfWords+=word
              }
          isStopWord=false
        })
          (listOfWords.mkString(" "), nlpOper.GetToken(sentence), nlpOper.GetLemmas(sentence, sentenceNumber.toString), nlpOper.GetPOS(sentence, sentenceNumber.toString),
            nlpOper.GetNER(sentence, sentenceNumber.toString))
        }).foreach(a => {
          ReadWriteOperation.WriteToFile(a._1.toString(), sentencesFile)
          ReadWriteOperation.WriteToFile(a._3.toString(), lemmaFile)
          ReadWriteOperation.WriteToFile(a._4.toString(), POSFile)
          ReadWriteOperation.WriteToFile(a._5.toString(), NERFile)
        })

    //Clustering Using LDA and KMeans
    //LDA.CallLDA(sContext, args)
    //KMeansOperation.MakeCluster(sContext,args)



    //TFIDF Operations
    /*val TfIdf=TF_IDF.GetTFIDF("data/AlltheSentences.txt",sContext)
  TF_IDF.TrainModel(sContext,TfIdf)
  TF_IDF.FindSynonyms("President",sContext).foreach(println(_))
  */


  //Question Asking part main
    println("Question Please..")
    val inputQuestion = StdIn.readLine()
    val questionTriplet = OpenIEOper.GetTriplet(inputQuestion)

    val brokenQuestion = QuestionAns.FormatQuestion(inputQuestion)
    val questionType = brokenQuestion(0)
    val formattedQuestion = brokenQuestion(1)

    val annotatedQuestionData = nlpOper.AnnotateData(formattedQuestion, nlpObj)
    val lemmatisedQuestionSentance = sContext.parallelize(nlpOper.GetSentencesFromAnnotatedData(annotatedQuestionData)).collect().map(
      sentance => (nlpOper.GetLemmas(sentance))
    )

    val lemmatisedQuestion = new ListBuffer[String]()
    lemmatisedQuestionSentance(0).foreach(a => lemmatisedQuestion += a)
    val ans = QuestionAns.FindOutAnswer(lemmatisedQuestion, questionType)

    println(ans)


  }


}


    //


    //ReadWriteOperation.ReadAllFileFromFolder("E:\\Knowledge Discovery Management\\Datasets\\WikiRef_dataset\\WikiRef_dataset\\WikiRef150","data/WholeDataSet.txt")



    /* if (questionTriplet.toString.equals("Rephrase the question please.")) {
      println(questionTriplet)
    }
    else {
      val brokenQuestion = QuestionAns.FormatQuestion(inputQuestion)
      val questionType = brokenQuestion(0)
      val formattedQuestion = brokenQuestion(1)

      val annotatedQuestionData = nlpOper.AnnotateData(formattedQuestion, nlpObj)
      val lemmatisedQuestionSentance = sContext.parallelize(nlpOper.GetSentencesFromAnnotatedData(annotatedQuestionData)).collect().map(
        sentance => (nlpOper.GetLemmas(sentance))
      )
*/
    /* val lemmatisedQuestion = new ListBuffer[String]()
      lemmatisedQuestionSentance(0).foreach(a => lemmatisedQuestion += a)

      val ans = QuestionAns.FindOutAnswer(lemmatisedQuestion, questionType)
      val wordToFindSynonyme = ""

      val FinalAnswer = QuestionAns.GetFinalAns(questionType, ans, formattedQuestion)

      if (QuestionAns.GotTheAnswer(FinalAnswer)) {
        println(FinalAnswer)
      }
      else {

        val topic = nlpOper.GetTopic(lemmatisedQuestion)
        if (topic.nonEmpty) {
          val synonyms = WordNet.GetSynoymnsUsingWordNet(topic).toList
          val FinalAnswer = QuestionAns.FindOutAnswer(synonyms, questionType)
          if (QuestionAns.GotTheAnswer(FinalAnswer)) {
            println(FinalAnswer)
          }
          else {
            val ngramWords = NGramOperations.getNGramsFromSentance(inputQuestion, 2)
            val ans = QuestionAns.FindOutAnswer(ngramWords, questionType)
            val FinalAnswer = QuestionAns.GetFinalAns(questionType, ans, formattedQuestion)
            if (QuestionAns.GotTheAnswer(FinalAnswer)) {
              val concepts = ConceptNet.GetConcept(topic, "10")
              println("Your may be form below list")
              concepts.foreach(c => println(c))
            }
          }
        }
        else {
          println("Sorry!!! not able to find answer....")
        }
     */








