import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

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
            FileWriter writer= new FileWriter(path,true);

                writer.write(lst+"\n");

            writer.close();
        } catch (IOException e) {

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

