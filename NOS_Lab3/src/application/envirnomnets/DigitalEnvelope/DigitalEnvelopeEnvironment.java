package application.envirnomnets.DigitalEnvelope;

import application.Controller;
import application.envirnomnets.AES.AES;
import application.envirnomnets.CryptoEnvironment;
import application.envirnomnets.RSA.RSA;
import application.envirnomnets.SHA1.SHA1;
import application.utils.CryptoConfigFile;
import application.utils.FileUtils;
import application.utils.MODEUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Created by vribic on 09.05.17..
 */
public class DigitalEnvelopeEnvironment extends CryptoEnvironment {

    public static final String label="DE";

    private File inputFile;
    private File pubKeyFile;
    private File envelopeFile;
    private File privKeyFile;
    private File outputFile;
    private MODEUtil.MODE runMode;

    public DigitalEnvelopeEnvironment(String inputFile, String pubKeyFile, String envelopeFile, String privKeyFile, String outputFile, MODEUtil.MODE runMode ){
        this.inputFile=new File(inputFile);
        this.pubKeyFile=new File(pubKeyFile);
        this.envelopeFile=new File(envelopeFile);
        this.privKeyFile=new File(privKeyFile);
        this.outputFile=new File(outputFile);
        this.runMode=runMode;
    }

    @Override
    public void run() {
        try{
            if(runMode.equals(MODEUtil.MODE.GENERATE)){
                CryptoConfigFile publicKeyConf=new CryptoConfigFile(this.pubKeyFile);
                PublicKey publicKey = (PublicKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(publicKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();

                //MAKE THE ENVELOPE
                String secretKey = AES.generateKey(128);
                String originalMessage= FileUtils.file2str(inputFile);
                String aesEncriptedString= AES.encrypt(originalMessage,secretKey);

                String encryptedKey= RSA.encrypt(secretKey, publicKey);

                //Save file
                CryptoConfigFile outputConf=new CryptoConfigFile();
                outputConf.put(CryptoConfigFile.DESCRIPTION,"Envelope");
                outputConf.put(CryptoConfigFile.METHOD,"DigitalEnvelope");
                outputConf.put(CryptoConfigFile.FILE_NAME,envelopeFile.getAbsolutePath());
                outputConf.put(CryptoConfigFile.ENVELOPE_DATA,aesEncriptedString);
                outputConf.put(CryptoConfigFile.ENVELOPE_CRYPT_DATA,encryptedKey);
                outputConf.saveToFile(envelopeFile);
            }else { //MODE.DECRYPT
                CryptoConfigFile privateKeyConf=new CryptoConfigFile(this.privKeyFile);
                PrivateKey privateKey = (PrivateKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(privateKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();


                CryptoConfigFile inputConf=new CryptoConfigFile(this.envelopeFile);

                String decriptedKey= RSA.decrypt(inputConf.get(CryptoConfigFile.ENVELOPE_CRYPT_DATA).getBytes(),privateKey);
                String aesDecriptedString= AES.decrypt(inputConf.get(CryptoConfigFile.ENVELOPE_DATA),decriptedKey);

                FileUtils.writeWholeString(outputFile,aesDecriptedString);
            };
        }catch (ClassNotFoundException|NullPointerException|IOException e){

        }
    }

    @Override
    public void setFile(String type, File file) {
        if (type.equals("publicKey")) {
            pubKeyFile=file;
        }else if (type.equals("envelope")) {
            envelopeFile=file;
        }else if(type.equals("inputFile")){
            inputFile=file;
        }else if(type.equals("privateKey")){
            privKeyFile=file;
        }else if(type.equals("outputFile")){
            outputFile=file;
        }
    }

    @Override
    public File getFile(String marker) {
        if (marker.equals("publicKey")) {
            return pubKeyFile;
        }else if (marker.equals("envelope")) {
            return envelopeFile;
        }else if(marker.equals("inputFile")){
            return inputFile;
        }else if(marker.equals("privateKey")){
            return privKeyFile;
        }else if(marker.equals("outputFile")){
            return outputFile;
        }
        return null;
    }

    @Override
    public void setMode(MODEUtil.MODE runMode) {
        this.runMode=runMode;
    }
}
