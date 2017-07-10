import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by shalin on 6/16/2017.
 */
public class ReadWriteOperation {
    static String ReadFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    static void WriteToFile(String lst, String path)
    {
        try{

            File file = new File(path);

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer= new FileWriter(path,true);
                writer.write(lst+"\n");
            writer.close();
        } catch (IOException e) {

        }
    }

        static List<String> GetListOfFileFromFolder(String directoryPath){
            List<String> allFiles=new ArrayList<>();
            try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
                paths
                           .forEach(new Consumer<Path>() {
                            @Override
                            public void accept(Path file) {
                                allFiles.add(file.toString());
                            }
                        });
            }catch (IOException io){
                System.out.println(io);
            }
            return allFiles;
        }

        static void RemoveEmptyLines() {
            try {
                FileReader fr = new FileReader("data/WholeDataSet.txt");
                BufferedReader br = new BufferedReader(fr);
                FileWriter fw = new FileWriter("data/AlltheSentences.txt");
                String line;

                while ((line = br.readLine()) != null) {
                    line = line.trim(); // remove leading and trailing whitespace
                    if (!line.equals("")) // don't write out blank lines
                    {
                        fw.write(line, 0, line.length());
                    }
                }
                fr.close();
                fw.close();
            }catch (IOException io)
            {

            }
        }


   static void ReadAllFileFromFolder(String inputDirectoryPath,String outputPath) {

       List<String> allFile = GetListOfFileFromFolder(inputDirectoryPath);
       for (String fileName : allFile) {
           try {
               WriteToFile(ReadFile(fileName, StandardCharsets.UTF_8), outputPath);
           } catch (IOException io) {

           }
       }
   }

    static void CreateandWriteToCSV(List<NLPData> nlpdata, String fileName){

          String COMMA = ",";
          String NEW_LINE = "\n";

          String headers="Token,Lamma,POS,NER";

          FileWriter fWriter=null ;
        try {

            fWriter = new FileWriter(fileName);
            fWriter.append(headers);

            fWriter.append(NEW_LINE);

            for (NLPData nlp : nlpdata) {
                fWriter.append(nlp.token);

                fWriter.append(COMMA);

                fWriter.append(nlp.lemma);

                fWriter.append(COMMA);

                fWriter.append(nlp.POS);

                fWriter.append(COMMA);

                fWriter.append(nlp.NER);

                fWriter.append(COMMA);

                fWriter.append(NEW_LINE);

            }

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {

            System.out.println("Error in CsvFileWriter !!!");

            e.printStackTrace();

        } finally {

            try {

                fWriter.flush();

                fWriter.close();

            } catch (IOException e) {

                System.out.println("Error while flushing/closing fileWriter !!!");

                e.printStackTrace();

            }

        }



    }
}

