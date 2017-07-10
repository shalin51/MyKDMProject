
import rita.RiWordNet

/**
  * Created by shalin on 7/2/2017.
  */
object WordNet {
    def GetSynoymnsUsingWordNet(wordToFind:String): Array[String] ={
    val wordnet = new RiWordNet("E:\\Knowledge Discovery Management\\WordNet-3.0\\WordNet-3.0")
    println(wordToFind)
    val pos=wordnet.getPos(wordToFind)
    println(pos.mkString(" "))
    val syn=wordnet.getAllSynonyms(wordToFind, pos(0), 10)
    return syn
  }
}
