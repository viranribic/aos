package application.envirnomnets.RSA;

import application.envirnomnets.CryptoEnvironment;
import application.utils.CryptoConfigFile;
import application.utils.FileUtils;
import application.utils.MODEUtil;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Created by vribic on 28.04.17..
 */
public class RSAEnvironment extends CryptoEnvironment {

    public static final String label="RSA";

    private File pubKeyFile;
    private File privKeyFile;
    private File inputFile;
    private File outputFile;
    private MODEUtil.MODE runMode;

    public RSAEnvironment(String pubKeyFile,String secKeyFile,String inputFile,String outputFile,MODEUtil.MODE runMode){
        this.pubKeyFile=new File(pubKeyFile);
        this.privKeyFile=new File(secKeyFile);
        this.inputFile=new File(inputFile);
        this.outputFile=new File(outputFile);
        this.runMode=runMode;
    }

    @Override
    public void setFile(String type, File file) {
        if (type.equals("pubKey")) {
            pubKeyFile=file;
        }else if (type.equals("secKey")) {
            privKeyFile=file;
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
        if (type.equals("pubKey")) {
            return pubKeyFile;
        }else if (type.equals("secKey")) {
            return privKeyFile;
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
                CryptoConfigFile pubKeyConf=new CryptoConfigFile(this.pubKeyFile);
                PublicKey pubKey = (PublicKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(pubKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();

                String originalMessage= FileUtils.file2str(inputFile);
                String encryptedString= RSA.encrypt(originalMessage,pubKey);

                //Save file
                CryptoConfigFile outputConf=new CryptoConfigFile();
                outputConf.put(CryptoConfigFile.DESCRIPTION,"Crypted file");
                outputConf.put(CryptoConfigFile.METHOD,"RSA");
                outputConf.put(CryptoConfigFile.FILE_NAME,outputFile.getAbsolutePath());
                outputConf.put(CryptoConfigFile.DATA,encryptedString);
                outputConf.saveToFile(outputFile);
            }else { //MODE.DECRYPT
                CryptoConfigFile privKeyConf=new CryptoConfigFile(this.privKeyFile);
                PrivateKey privKey = (PrivateKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(privKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();


                CryptoConfigFile inputConf=new CryptoConfigFile(this.inputFile);
                String encryptedMessage= inputConf.get(CryptoConfigFile.DATA);
                String decriptedMessage= RSA.decrypt(encryptedMessage.getBytes(),privKey);

                FileUtils.writeWholeString(outputFile,decriptedMessage);
            };
        }catch (ClassNotFoundException|NullPointerException|IOException e){

        }
    }

    public KeyPair generateKeys(int keysize) {
        return RSA.generateKey(keysize);
    }
}
