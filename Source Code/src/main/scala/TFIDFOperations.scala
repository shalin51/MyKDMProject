
import java.io.File

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF, Word2Vec, Word2VecModel}

import scala.collection.immutable.HashMap

/**
  * Created by shalin on 6/26/2017.
*/

object TF_IDF {
  def FindSynonyms(word: String, sparkContectObj: SparkContext): Array[(String,Double)] = {

    //Reading the processed Text File to find synonyms
    val processedFile = sparkContectObj.textFile("data/processedFile.txt")

    val wordSeq = processedFile.map(line => {
      val wordStrings = line.split(" ")
      wordStrings.toSeq
    })

    //HashingTF object creation
    val hashingTF = new HashingTF()

    //Creating Term Frequency of the document
    val tf = hashingTF.transform(wordSeq)
    tf.cache() // cache object for fats processing

    val idf = new IDF().fit(tf) //IDF for calculated TF


    val tf_idfValue = idf.transform(tf) //Creating IDF for TF

    val tfidfvalues = tf_idfValue.flatMap(value => {
      val newValue: Array[String] = value.toString.replace(",[", ";").split(";")
      val values = newValue(2).replace("]", "").replace(")", "").split(",")
      values
    })

    val tfidfindex = tf_idfValue.flatMap(value => {
      val newValue: Array[String] = value.toString.replace(",[", ";").split(";")
      val indices = newValue(1).replace("]", "").replace(")", "").split(",")
      indices
    })

    val tfidfData = tfidfindex.zip(tfidfvalues)

    var hmObj = new HashMap[String, Double]

    tfidfData.collect().foreach(f => {
      hmObj += f._1 -> f._2.toDouble
    })

    val mapp = sparkContectObj.broadcast(hmObj)

    val wordData = wordSeq.flatMap(_.toList)
    val rddofWords = wordData.map(wrd => {
      Seq(wrd)
    })


    //W2Vector
    val modelPath = "data/W2V"


    val modelFolder = new File(modelPath)
    val wordToFind=word
    if (modelFolder.exists()) {
      val sameModel = Word2VecModel.load(sparkContectObj, modelPath)
      val synonyms = sameModel.findSynonyms(wordToFind, 20)
      return synonyms
    }
    else {
      val word2vec = new Word2Vec().setVectorSize(1000)

      val model = word2vec.fit(rddofWords)

      val synonyms = model.findSynonyms(wordToFind, 20)

      /*for ((synonym, cosineSimilarity) <- synonyms) {
        println("synonym :" + s"$synonym $cosineSimilarity")
      }
      */
      // Save and load model
      model.save(sparkContectObj, modelPath)
      return synonyms
    }

  }

}







