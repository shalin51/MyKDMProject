import java.io.PrintStream

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.{DistributedLDAModel, EMLDAOptimizer, LDA, OnlineLDAOptimizer}
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import scopt.OptionParser

/**
  * Created by shalin on 7/3/2017.
  */
object LDA {
  private case class Params(
                             input: Seq[String] = Seq.empty,
                             k: Int = 10,
                             algorithm: String = "em")

  def CallLDA(sContext: SparkContext,args: Array[String]): Unit ={

    val defaultParams = Params()
    val parser = new OptionParser[Params]("QuestionAnswerSystem") {
      head("QuestionAnswerSystem: Used for pipelineing for system")
      opt[Int]("k")
        .text(s"number of topics. default: ${defaultParams.k}")
        .action((x, c) => c.copy(k = x))
      opt[String]("algorithm")
        .text(s"inference algorithm to use. em and online are supported." +
          s" default: ${defaultParams.algorithm}")
        .action((x, c) => c.copy(algorithm = x))
      arg[String]("<input>...")
        .text("input paths (directories) to plain text corpora." +
          "  Each text file line should hold 1 document.")
        .unbounded()
        .required()
        .action((x, c) => c.copy(input = c.input :+ x))
    }

    parser.parse(args, defaultParams).map { params =>
      run(params)
    }.getOrElse {
      parser.showUsageAsError
      sys.exit(1)
    }


     def run(params: Params) {

      Logger.getRootLogger.setLevel(Level.WARN)

      val topic_output = new PrintStream("data/LDAOutput.txt")
      // Load documents, and prepare them for LDA.
      val preprocessStart = System.nanoTime()
      val (corpus, vocabArray, actualNumTokens) =
        preprocess(sContext, params.input)
      corpus.cache()
      val actualCorpusSize = corpus.count()
      val actualVocabSize = vocabArray.length
      val preprocessElapsed = (System.nanoTime() - preprocessStart)

      // Run LDA.
      val lda = new LDA()
       println("in midddle")
      val optimizer = params.algorithm.toLowerCase match {
        case "em" => new EMLDAOptimizer
        // add (1.0 / actualCorpusSize) to MiniBatchFraction be more robust on tiny datasets.
        case "online" => new OnlineLDAOptimizer().setMiniBatchFraction(0.05 + 1.0 / actualCorpusSize)
        case _ => throw new IllegalArgumentException(
          s"Only em, online are supported but got ${params.algorithm}.")
      }

      lda.setOptimizer(optimizer)
        .setK(params.k)
        .setMaxIterations(10)
       println("in midddle")
      val startTime = System.nanoTime()
      val ldaModel = lda.run(corpus)
      val elapsed = (System.nanoTime() - startTime) / 1e9


      if (ldaModel.isInstanceOf[DistributedLDAModel]) {
        val distLDAModel = ldaModel.asInstanceOf[DistributedLDAModel]
        val avgLogLikelihood = distLDAModel.logLikelihood / actualCorpusSize.toDouble
        println(s"\t Training data average log likelihood: $avgLogLikelihood")
        println()
        topic_output.println(s"\t Training data average log likelihood: $avgLogLikelihood")
        topic_output.println()
      }

       println("in midddle")
      // Print the topics, showing the top-weighted terms for each topic.
      val topicIndices = ldaModel.describeTopics(maxTermsPerTopic = actualVocabSize)
      val topics = topicIndices.map { case (terms, termWeights) =>
        terms.zip(termWeights).map { case (term, weight) => (vocabArray(term.toInt), weight) }
      }
      println(s"${params.k} topics:")
      topic_output.println(s"${params.k} topics:")
      topics.zipWithIndex.foreach { case (topic, i) =>
        println(s"TOPIC $i")
        // topic_output.println(s"TOPIC $i")
        topic.foreach { case (term, weight) =>
          println(s"$term\t$weight")
          topic_output.println(s"TOPIC_$i;$term;$weight")
        }
        println()
        topic_output.println()
      }
      topic_output.close()
      sContext.stop()
    }

    /**
      * Load documents, tokenize them, create vocabulary, and prepare documents as term count vectors.
      *
      * @return (corpus, vocabulary as array, total token count in corpus)
      */
     def preprocess(sc: SparkContext,paths: Seq[String]): (RDD[(Long, Vector)], Array[String], Long) = {

      //Reading Stop Words
      val stopWords=sContext.textFile("data/stopwords.txt").collect()
      val stopWordsBroadCast=sContext.broadcast(stopWords)

      val df = sContext.textFile(paths.mkString(",")).map(f => {
        val lemmatised=NLPOperations.returnLemma(f)
        val splitString = lemmatised.split(" ")
        splitString
      })



      val stopWordRemovedDF=df.map(f=>{
        //Filtered numeric and special characters out
        val filteredF=f.map(_.replaceAll("[^a-zA-Z]"," "))
          //Filter out the Stop Words
          .filter(ff=>{
          if(stopWordsBroadCast.value.contains(ff.toLowerCase))
            false
          else
            true
        })
        filteredF
      })

      val dfseq=df.map(_.toSeq)

      //Creating an object of HashingTF Class
      val hashingTF = new HashingTF(df.count().toInt)  // VectorSize as the Size of the Vocab

      //Creating Term Frequency of the document
      val tf = hashingTF.transform(dfseq)
      tf.cache()

      val idf = new IDF().fit(tf)

      //Creating Inverse Document Frequency
      val tfidf = idf.transform(tf).zipWithIndex().map(_.swap)

      val dff= df.flatMap(f=>f)
      val vocab=dff.distinct().collect()
      (tfidf, vocab, dff.count()) // Vector, Vocab, total token count
    }
  }


}
