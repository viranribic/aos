package application.utils;

import java.io.*;
import java.util.Scanner;

/**
 * Created by vribic on 28.04.17..
 */
public class FileUtils {

    public static String file2str(File f) {
        String content="";
        String line;
        try {
            try(BufferedReader reader=new BufferedReader(new FileReader(f))){
                    while(true){
                        line=reader.readLine();
                        if(line==null) break;
                        else
                            content+=line+"\n";
                    }
            }
            return content;
        }catch (IOException e){
            return null;
        }
    }

    public static String readWholeFile(File f) throws FileNotFoundException {
        return new Scanner(new File("filename")).useDelimiter("\\Z").next();
    }

    public static void writeWholeString(File f,String txt) throws IOException {
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(f))){
            writer.write(txt);
        }
    }
}
