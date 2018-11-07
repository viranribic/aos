package application.utils;

import java.io.*;
import java.util.*;

/**
 * Created by vribic on 28.04.17..
 */
public class CryptoConfigFile {

    //Supported labels
    public static final String DESCRIPTION="Description";
    public static final String FILE_NAME="File name";
    public static final String METHOD="Method";
    public static final String KEY_LENGHT="Key length";
    public static final String SECRET_KEY="Secret key";
    public static final String INITIALIZATION_VECTOR="Initialization vector";
    public static final String MODULUS="Modulus";
    public static final String PUBLIC_EXPONENT="Public exponent";
    public static final String PRIVATE_EXPONENT="Private exponent";
    public static final String SIGNATURE="Signature";
    public static final String DATA="Data";
    public static final String ENVELOPE_DATA="Envelope data";
    public static final String ENVELOPE_CRYPT_DATA="Envelope crypt data";
    private static final List<String > labelsSorted= Arrays.asList(
            DESCRIPTION,
            FILE_NAME,
            METHOD,
            KEY_LENGHT,
            SECRET_KEY,
            INITIALIZATION_VECTOR,
            MODULUS,
            PUBLIC_EXPONENT,
            PRIVATE_EXPONENT,
            SIGNATURE,
            DATA,
            ENVELOPE_DATA,
            ENVELOPE_CRYPT_DATA);

    //Crypto block markers
    public static final String DATA_START="---BEGIN OS2 CRYPTO DATA---";
    public static final String DATA_END="---END OS2 CRYPTO DATA---";

    private final Map<String,String > dataMap=new HashMap<>();
    private String processingLabel;
    private String processingData;

    public CryptoConfigFile(File file) throws IOException {
        boolean parseLine=false;
        String line;
        boolean beginOnce=false;
        boolean endOnce=false;

        try(BufferedReader reader=new BufferedReader(new FileReader(file))){
            while (true){
                line=reader.readLine();
                if (line==null) {
                    finish();
                    break;
                }else if(line.equals(""))
                    continue;
                else if(line.equals(DATA_START)) {
                    parseLine = true;
                    beginOnce=true;
                }else if(line.equals(DATA_END)) {
                    parseLine = false;
                    endOnce=true;
                }else if(parseLine)
                    process(line);
            }
        }
        if(!beginOnce || !endOnce){
            throw new RuntimeException("Invalid file format.");
        }
    }

    private void finish( ) {
        if(processingLabel==null)
            return;
        //A new label has been found
        dataMap.put(processingLabel,processingData);
    }

    public CryptoConfigFile() {

    }

    private void process(String line) {
        if(processingData==null){
            //First entrance -> parse label
            processingLabel=findLabel(line);
            processingData="";
        }else if(line.startsWith("\t")){
            //this is a data line
            processingData+=line.replace("\t","");
        }else{
            //A new label has been found
            dataMap.put(processingLabel,processingData);
            processingLabel=findLabel(line);
            processingData="";
        }
    }

    private String findLabel(String line) {
        if(line.startsWith(DESCRIPTION))
            return DESCRIPTION;
        if(line.startsWith(FILE_NAME))
        return FILE_NAME;
        if(line.startsWith(METHOD))
        return METHOD;
        if(line.startsWith(KEY_LENGHT))
        return KEY_LENGHT;
        if(line.startsWith(SECRET_KEY))
        return SECRET_KEY;
        if(line.startsWith(INITIALIZATION_VECTOR))
        return INITIALIZATION_VECTOR;
        if(line.startsWith(MODULUS))
        return MODULUS;
        if(line.startsWith(PUBLIC_EXPONENT))
        return PUBLIC_EXPONENT;
        if(line.startsWith(PRIVATE_EXPONENT))
        return PRIVATE_EXPONENT;
        if(line.startsWith(SIGNATURE))
        return SIGNATURE;
        if(line.startsWith(DATA))
        return DATA;
        if(line.startsWith(ENVELOPE_DATA))
        return ENVELOPE_DATA;
        if(line.startsWith(ENVELOPE_CRYPT_DATA))
        return ENVELOPE_CRYPT_DATA;
        else
            return null;
    }

    public String get(String key){
        return dataMap.get(key);
    }

    public String put(String  key, String value){
        return dataMap.put(key,value);
    }

    public boolean saveToFile(File file){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(file))){
            writer.write(DATA_START+"\n");

            for (String label:labelsSorted)
                if(dataMap.containsKey(label))
                    writePrettyData(writer,label,dataMap.get(label));

            writer.write(DATA_END+"\n");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void writePrettyData(BufferedWriter writer, String label, String s) throws IOException {
        writer.write(label+":\n");
        String buff;
        int buffIndex;
        while (s.length()!=0){
            buffIndex=Math.min(60,s.length());
            buff=s.substring(0,buffIndex);
            writer.write("\t"+buff+"\n");
            if(buffIndex==s.length())
                break;
            s=s.substring(buffIndex,s.length());
        }
    }
}
