package application.envirnomnets.DigitalStamp;

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
public class DigitalStampEnvironment extends CryptoEnvironment {
    public static final String label="DST";

    private File inputFile;
    private File pubKeyRecFile;
    private File privKeySendFile;
    private File envelopeFile;
    private File signatureFile;
    private File pubKeySendFile;
    private File privKeyReciFile;
    private File outputFile;
    private MODEUtil.MODE runMode;

    public DigitalStampEnvironment(String inputFile,String pubKeyRecFile,String privKeySendFile, String envelopeFile, String signatureFile, String pubKeySendFile, String privKeyReciFile, String outputFile, MODEUtil.MODE runMode){
        this.inputFile= new File(inputFile);
        this.pubKeyRecFile=new File(pubKeyRecFile);
        this.privKeySendFile=new File(privKeySendFile);
        this.envelopeFile=new File(envelopeFile);
        this.signatureFile=new File(signatureFile);
        this.pubKeySendFile=new File(pubKeySendFile);
        this.privKeyReciFile=new File(privKeyReciFile);
        this.outputFile=new File(outputFile);
        this.runMode=runMode;
    }

    @Override
    public void run() {
        try{
            if(runMode.equals(MODEUtil.MODE.GENERATE)){
                CryptoConfigFile publicKeyConf=new CryptoConfigFile(this.pubKeyRecFile);
                PublicKey publicKey = (PublicKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(publicKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();

                //MAKE THE ENVELOPE
                String secretKey = AES.generateKey(128);
                String originalMessage= FileUtils.file2str(inputFile);
                String aesMessage= AES.encrypt(originalMessage,secretKey);
                String envelopeKey= RSA.encrypt(secretKey, publicKey);

                //Save envelope
                CryptoConfigFile envelopeConf=new CryptoConfigFile();
                envelopeConf.put(CryptoConfigFile.DESCRIPTION,"Envelope");
                envelopeConf.put(CryptoConfigFile.METHOD,"DigitalEnvelope");
                envelopeConf.put(CryptoConfigFile.FILE_NAME,envelopeFile.getAbsolutePath());
                envelopeConf.put(CryptoConfigFile.ENVELOPE_DATA,aesMessage);
                envelopeConf.put(CryptoConfigFile.ENVELOPE_CRYPT_DATA,envelopeKey);
                envelopeConf.saveToFile(envelopeFile);

                //Make signature
                CryptoConfigFile privateKeyConf=new CryptoConfigFile(this.privKeySendFile);
                PrivateKey privateKey = (PrivateKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(privateKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();
                String envelopeFileMessage= FileUtils.file2str(envelopeFile);
                String digestEnvelopeMessage= SHA1.hash(envelopeFileMessage);
                String sigantureKey= RSA.encrypt(digestEnvelopeMessage, privateKey);

                //Save signature
                CryptoConfigFile outputConf=new CryptoConfigFile();
                outputConf.put(CryptoConfigFile.DESCRIPTION,"Signature");
                outputConf.put(CryptoConfigFile.METHOD,"DigitalSignature");
                outputConf.put(CryptoConfigFile.FILE_NAME,signatureFile.getAbsolutePath());
                outputConf.put(CryptoConfigFile.SIGNATURE,sigantureKey);
                outputConf.saveToFile(signatureFile);
            }else { //MODE.DECRYPT
                //MAKE THE SIGNATURE
                String originalMessage= FileUtils.file2str(envelopeFile);
                String digestMessage= SHA1.hash(originalMessage);

                // GET THE MESSAGE USED
                CryptoConfigFile signatureConf=new CryptoConfigFile(this.signatureFile);
                CryptoConfigFile pubKeyConf=new CryptoConfigFile(this.pubKeySendFile);
                PublicKey pubKey = (PublicKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(pubKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();

                String envelopeHashMessage= RSA.decrypt(signatureConf.get(CryptoConfigFile.SIGNATURE).getBytes(), pubKey);


                if(!digestMessage.equals(envelopeHashMessage)){
                    Controller.editor.discard();
                    Controller.editor.writeLine("ERROR\nSignature given in "+signatureFile.getName()+"\n using "+pubKeySendFile.getName()+"\nDOES NOT MATCH the one derived from "+envelopeFile.getName()+" using "+this.privKeySendFile.getName());
                    return;
                }

                CryptoConfigFile privateKeyConf=new CryptoConfigFile(this.privKeyReciFile);
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
        if (type.equals("inputFile")) {
            inputFile=file;
        }else if (type.equals("publicKeyR")) {
            pubKeyRecFile=file;
        }else if(type.equals("privateKeyS")){
            privKeySendFile=file;
        }else if(type.equals("envelope")){
            envelopeFile=file;
        }else if (type.equals("signature")) {
            signatureFile=file;
        }else if (type.equals("publicKeyS")) {
            pubKeySendFile=file;
        }else if(type.equals("privateKeyR")){
            privKeyReciFile=file;
        }else if(type.equals("outputFile")){
            outputFile=file;
        }
    }

    @Override
    public File getFile(String marker) {
        if (marker.equals("inputFile")) {
            return inputFile;
        }else if (marker.equals("publicKeyR")) {
            return pubKeyRecFile;
        }else if(marker.equals("privateKeyS")){
            return privKeySendFile;
        }else if(marker.equals("envelope")){
            return envelopeFile;
        }else if (marker.equals("signature")) {
            return signatureFile;
        }else if (marker.equals("publicKeyS")) {
            return pubKeySendFile;
        }else if(marker.equals("privateKeyR")){
            return privKeyReciFile;
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
