package application.envirnomnets.AES;

import application.envirnomnets.CryptoEnvironment;
import application.utils.CryptoConfigFile;
import application.utils.FileUtils;
import application.utils.MODEUtil;

import java.io.*;

/**
 * Created by vribic on 28.04.17..
 */
public class AESEnvironment extends CryptoEnvironment {

    public static final String label="AES";

    private File keyFile;
    private File inputFile;
    private File outputFile;
    private MODEUtil.MODE runMode;

    public AESEnvironment(String keyFile,String inputFile,String outputFile,MODEUtil.MODE runMode){
        this.keyFile=new File(keyFile);
        this.inputFile=new File(inputFile);
        this.outputFile=new File(outputFile);
        this.runMode=runMode;
    }

    @Override
    public void setFile(String type, File file) {
        if (type.equals("key")) {
            keyFile = file;
        }else if(type.equals("inputFile")){
            inputFile=file;
        }else if(type.equals("outputFile")){
            outputFile=file;
        }
    }

    @Override
    public void setMode(MODEUtil.MODE runMode) {
        this.runMode = runMode;
    }

    @Override
    public File getFile(String type) {
        if (type.equals("key")) {
            return keyFile;
        }else if(type.equals("inputFile")){
            return inputFile;
        }else if(type.equals("outputFile")){
            return outputFile;
        }
        return null;
    }

    @Override
    public void run() {
        try{
            if(runMode.equals(MODEUtil.MODE.ENCRYPT)){
                CryptoConfigFile keyConf=new CryptoConfigFile(this.keyFile);

                String secretKey = keyConf.get(CryptoConfigFile.SECRET_KEY);
                String originalMessage= FileUtils.file2str(inputFile);
                String encriptedString= AES.encrypt(originalMessage,secretKey);

                //Save file
                CryptoConfigFile outputConf=new CryptoConfigFile();
                outputConf.put(CryptoConfigFile.DESCRIPTION,"Crypted file");
                outputConf.put(CryptoConfigFile.METHOD,"AES");
                outputConf.put(CryptoConfigFile.FILE_NAME,outputFile.getAbsolutePath());
                outputConf.put(CryptoConfigFile.DATA,encriptedString);
                outputConf.saveToFile(outputFile);
            }else { //MODE.DECRYPT
                CryptoConfigFile keyConf=new CryptoConfigFile(this.keyFile);
                CryptoConfigFile inputConf=new CryptoConfigFile(this.inputFile);

                String secretKey = keyConf.get(CryptoConfigFile.SECRET_KEY);
                String encryptedMessage= inputConf.get(CryptoConfigFile.DATA);
                String decriptedMessage= AES.decrypt(encryptedMessage,secretKey);

                FileUtils.writeWholeString(outputFile,decriptedMessage);
            };
        }catch (NullPointerException|IOException e){

        }
    }

    public String generateKey(int keysize) {
        return AES.generateKey(keysize);
    }
}
