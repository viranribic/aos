package application.envirnomnets.SHA1;

import application.envirnomnets.CryptoEnvironment;
import application.utils.CryptoConfigFile;
import application.utils.FileUtils;
import application.utils.MODEUtil;

import java.io.*;
import java.util.Base64;

/**
 * Created by vribic on 28.04.17..
 */
public class SHA1Environment extends CryptoEnvironment {

    public static final String label="SHA1";

    private File inputFile;
    private File outputFile;
    private MODEUtil.MODE runMode;

    public SHA1Environment(String inputFile,String outputFile){
        this.inputFile=new File(inputFile);
        this.outputFile=new File(outputFile);
    }

    @Override
    public void setFile(String type, File file) {
        if(type.equals("inputFile")){
            inputFile=file;
        }else if(type.equals("outputFile")){
            outputFile=file;
        }
    }

    @Override
    public void setMode(MODEUtil.MODE runMode) {
        this.runMode=runMode;
    }

    @Override
    public File getFile(String type) {
        if(type.equals("inputFile")){
            return inputFile;
        }else if(type.equals("outputFile")){
            return outputFile;
        }
        return null;
    }


    @Override
    public void run() {
                String originalMessage= FileUtils.file2str(inputFile);
                String hashString;
                if(runMode== MODEUtil.MODE.DEFAULT_HASH)
                    hashString= SHA1.hash(originalMessage);
                else
                    hashString= application.envirnomnets.SHA1.myImplementation.SHA1.hash(originalMessage);
                //Save file
                CryptoConfigFile outputConf=new CryptoConfigFile();
                outputConf.put(CryptoConfigFile.DESCRIPTION,"Crypted file");
                outputConf.put(CryptoConfigFile.METHOD,"SHA1");
                outputConf.put(CryptoConfigFile.FILE_NAME,outputFile.getAbsolutePath());
                outputConf.put(CryptoConfigFile.DATA,new String(Base64.getEncoder().encode(hashString.getBytes())));
                outputConf.saveToFile(outputFile);
    }

    public String generateKey(int keysize) {
        return "";
    }
}
