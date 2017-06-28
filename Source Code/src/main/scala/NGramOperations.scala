import scala.collection.mutable.ListBuffer

/**
  * Created by shalin on 6/27/2017.
  */
object NGramOperations {
  def getNGramsFromSentance(sen: String, n:Int): List[String] = {
    val words = sen
    val ngram = words.split(' ').sliding(n)

    val ngList=new ListBuffer[String]()
     ngram.foreach(ng=> {
          ngList += ng.toString
        })

    return  ngList.toList
  }
}
