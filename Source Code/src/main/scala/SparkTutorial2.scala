import java.io.File
import java.nio.charset.StandardCharsets

import scala.collection._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF, Word2Vec, Word2VecModel}

import scala.collection.immutable.HashMap
import com.sun.xml.internal.bind.api.impl.NameConverter.Standard

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.io.{Source, StdIn}

/**
  * Created by shalin on 6/16/2017.
  */
object SparkTutorials {

  def main(args: Array[String]): Unit = {
    val configurationSpark = new SparkConf()
      .setMaster("local[*]")
      .setAppName("Tutorial2-Spark")
    System.setProperty("hadoop.home.dir", "C:\\winutils")



    val processedData = "E:\\Knowledge Discovery Management\\Tutorials\\ScalaTutorial1\\output\\processedData.txt"
    val sContext = new SparkContext(configurationSpark)
    val outputFile = "E:\\Knowledge Discovery Management\\Tutorials\\Tutorial3\\output\\processedFile.txt"

   val fileData = ReadWriteOperation.ReadFile("data\\inputDocuments.txt",StandardCharsets.UTF_8)




    //val nlpDataList = List[NLPData]()

    val nlpOper = new NLPOperations()
    val nlpObj = nlpOper.GetNLPObject()
    val annotatedInputData = nlpOper.AnnotateData(fileData, nlpObj)

    val annotatedSentaces = sContext.parallelize(nlpOper.GetSentencesFromAnnotatedData(annotatedInputData)).collect().map(
      sentence => (sentence, nlpOper.GetToken(sentence), nlpOper.GetLemmas(sentence), nlpOper.GetPOS(sentence), nlpOper.GetNER(sentence))
    ).foreach(a => {
                    ReadWriteOperation.WriteToFile(a._1.toString(), outputFile)
                    ReadWriteOperation.WriteToFile(a._2.toString(), outputFile)
                    ReadWriteOperation.WriteToFile(a._3.toString(), outputFile)
                    ReadWriteOperation.WriteToFile(a._4.toString(), outputFile)
        }
    )


    val inputQuestion=StdIn.readLine()

    val brokenQuestion=QuestionAns.FormatQuestion(inputQuestion)
    val questionType=brokenQuestion(0)
    val formattedQuestion=brokenQuestion(1)



    val annotatedQuestionData = nlpOper.AnnotateData(formattedQuestion, nlpObj)


    val lemmatisedQuestionSentance=sContext.parallelize(nlpOper.GetSentencesFromAnnotatedData(annotatedQuestionData)).collect().map(
      sentance=>(nlpOper.GetLemmas(sentance))
    )

    val lemmatisedQuestion=new ListBuffer[String]()
    lemmatisedQuestionSentance(0).foreach(a=> lemmatisedQuestion+=a._1)

    val ans=QuestionAns.FindOutAnswer(lemmatisedQuestion,questionType)
    val wordToFindSynonyme=""

    val FinalAnswer=QuestionAns.GetFinalAns(questionType,ans,formattedQuestion)

    if(QuestionAns.GotTheAnswer(FinalAnswer)){
      println(FinalAnswer)
    }
    else {
      val synonyms=TF_IDF.FindSynonyms(wordToFindSynonyme,sContext)
      val synoList=synonyms.map(synonym=>synonym._1).toList
      val FinalAnswer =QuestionAns.FindOutAnswer(synoList, questionType)
        if(QuestionAns.GotTheAnswer(FinalAnswer)){
          println(FinalAnswer)
        }
        else {
            val ngramWords=NGramOperations.getNGramsFromSentance(inputQuestion, 2)
            val ans = QuestionAns.FindOutAnswer(ngramWords,questionType)
            val FinalAnswer =QuestionAns.GetFinalAns(questionType,ans,formattedQuestion)
            println(FinalAnswer)
          }
      }
    }
  }
