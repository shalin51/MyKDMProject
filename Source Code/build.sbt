name := "Tutorial2-Spark"

version := "1.0"

scalaVersion := "2.11.8"

// spark 1.6.1 and satndfor Libaries 3.3.0

libraryDependencies ++= Seq("edu.stanford.nlp" % "stanford-corenlp" % "3.3.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.3.0" classifier "models",
  "edu.stanford.nlp" % "stanford-parser" % "3.3.0",
  "org.apache.spark" %% "spark-mllib" % "1.6.1",
  "com.google.protobuf" % "protobuf-java" % "2.6.1",
  "org.apache.spark" %% "spark-core" % "1.6.1"
)

