import java.io.PrintStream

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

import scala.collection.immutable.HashMap

/**
  * Created by shalin on 7/9/2017.
  */

  object KMeansOperation {

  def MakeCluster(sc:SparkContext,args: Array[String]): Unit = {

    val inputDirectory = Seq("data/Dataset")
    Logger.getRootLogger.setLevel(Level.WARN)

    val topics = new PrintStream("data/KMeans_Result.txt")
    // Load documents, and prepare them for KMeans.
    val preprocessStartTime = System.nanoTime()
    val (corpusVector, data, vocabSize) = preprocess(sc, inputDirectory)

    val actualCorpusSize = corpusVector.count()
    val actualVocabSize = vocabSize
    val preprocessElapsed = (System.nanoTime() - preprocessStartTime) / 1e9

    println()
    println(s"Corpus summary:")
    println(s"\t Training set size: $actualCorpusSize documents")
    println(s"\t Vocabulary size: $actualVocabSize terms")
    println(s"\t Preprocessing time: $preprocessElapsed sec")
    println()


    topics.println()
    topics.println(s"Corpus summary:")
    topics.println(s"\t Training set size: $actualCorpusSize documents")
    topics.println(s"\t Vocabulary size: $actualVocabSize terms")
    topics.println(s"\t Preprocessing time: $preprocessElapsed sec")
    topics.println()

    // Run KMeans.
    val startTime = System.nanoTime()

    val k = 10
    val numIterations = 20

    val corpusKM = corpusVector.map(_._2)
    val model = KMeans.train(corpusKM, k, numIterations)


    val elapsed = (System.nanoTime() - startTime) / 1e9

    println(s"Finished training KMeans model.  Summary:")
    println(s"\t Training time: $elapsed sec")


    topics.println(s"Finished training KMeans model.  Summary:")
    topics.println(s"\t Training time: $elapsed sec")

    val predictions = model.predict(corpusKM)

    val error = model.computeCost(corpusKM)
    val results = data.zip(predictions)
    val resultsA = results.collect()
    var hm = new HashMap[Int, Int]
    resultsA.foreach(f => {
      topics.println(f._1._1 + ";" + f._2)
      if (hm.contains(f._2)) {
        var v = hm.get(f._2).get
        v = v + 1
        hm += f._2 -> v
      }
      else {
        hm += f._2 -> 1
      }
    })
    topics.close()
    sc.stop()
  }

  private def preprocess(sc: SparkContext, paths: Seq[String]): (RDD[(Long, Vector)], RDD[(String, String)], Long) = {

    //Reading Stop Words
    val stopWords = sc.textFile("data/stopwords.txt").collect()
    val stopWordsBroadCast = sc.broadcast(stopWords)

    val df = sc.wholeTextFiles(paths.mkString(",")).map(f => {
      val splitString = f._2.split(" ").toSeq
      (f._1, splitString)
    })


    val stopWordRemovedDF = df.map(f => {
      //Filtered numeric and special characters out
      val filteredF = f._2.map(_.replaceAll("[^a-zA-Z]", ""))
        //Filter out the Stop Words
        .filter(ff => {
        if (stopWordsBroadCast.value.contains(ff.toLowerCase))
          false
        else
          true
      })
      (f._1, filteredF)
    })

    val data = stopWordRemovedDF.map(f => {
      (f._1, f._2.mkString(" "))
    })
    val dfseq = stopWordRemovedDF.map(_._2.toSeq)

    //Creating an object of HashingTF Class
    val hashingTF = new HashingTF(stopWordRemovedDF.count().toInt) // VectorSize as the Size of the Vocab

    //Creating Term Frequency of the document
    val tf = hashingTF.transform(dfseq)
    tf.cache()

    val idf = new IDF().fit(tf)
    //Creating Inverse Document Frequency
    val tfidf1 = idf.transform(tf)
    tfidf1.cache()


    val tfidf = tfidf1.zipWithIndex().map(_.swap)

    val dff = stopWordRemovedDF.flatMap(f => f._2)
    val vocab = dff.distinct().collect()
    tfidf.collect()
    (tfidf, data, dff.count()) // Vector, Data, total token count
  }
}
