package application.envirnomnets.DigitalSignature;

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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Created by vribic on 09.05.17..
 */
public class DigitalSignatureEnvironment extends CryptoEnvironment {
    public static final String label="DS";

    private File inputFile;
    private File privKeyFile;
    private File signatureFile;
    private File pubKeyFile;
    private MODEUtil.MODE runMode;

    public DigitalSignatureEnvironment(String inputFile, String privKeyFile, String signatureFile, String pubKeyFile,  MODEUtil.MODE runMode ){
        this.inputFile=new File(inputFile);
        this.privKeyFile=new File(privKeyFile);
        this.signatureFile=new File(signatureFile);
        this.pubKeyFile=new File(pubKeyFile);
        this.runMode=runMode;
    }

    @Override
    public void run() {
        try{
            if(runMode.equals(MODEUtil.MODE.GENERATE)){
                CryptoConfigFile privKeyConf=new CryptoConfigFile(this.privKeyFile);
                PrivateKey privateKey = (PrivateKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(privKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();

                //MAKE THE SIGNATURE
                String originalMessage= FileUtils.file2str(inputFile);
                String digestMessage= SHA1.hash(originalMessage);
                String encryptedKey= RSA.encrypt(digestMessage, privateKey);

                //Save file
                CryptoConfigFile outputConf=new CryptoConfigFile();
                outputConf.put(CryptoConfigFile.DESCRIPTION,"Signature");
                outputConf.put(CryptoConfigFile.METHOD,"DigitalSignature");
                outputConf.put(CryptoConfigFile.FILE_NAME,signatureFile.getAbsolutePath());
                outputConf.put(CryptoConfigFile.SIGNATURE,encryptedKey);
                outputConf.saveToFile(signatureFile);
            }else { //MODE.DECRYPT
                //MAKE THE SIGNATURE
                String originalMessage= FileUtils.file2str(inputFile);
                String digestMessage= SHA1.hash(originalMessage);

                // GET THE MESSAGE USED
                CryptoConfigFile signatureConf=new CryptoConfigFile(this.signatureFile);
                CryptoConfigFile pubKeyConf=new CryptoConfigFile(this.pubKeyFile);
                PublicKey pubKey = (PublicKey) new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(pubKeyConf.get(CryptoConfigFile.PRIVATE_EXPONENT)))).readObject();

                String digestMessageGiven= RSA.decrypt(signatureConf.get(CryptoConfigFile.SIGNATURE).getBytes(), pubKey);


                if(digestMessage.equals(digestMessageGiven)){
                    Controller.editor.discard();
                    Controller.editor.writeLine("SUCCESS\nSignature given in "+signatureFile.getName()+"\n using "+pubKeyFile.getName()+"\nmatches the one derived from "+inputFile.getName()+" using "+this.privKeyFile.getName());
                }else{
                    Controller.editor.discard();
                    Controller.editor.writeLine("ERROR\nSignature given in "+signatureFile.getName()+"\n using "+privKeyFile.getName()+"\nDOES NOT MATCH the one derived from "+inputFile.getName()+" using "+this.pubKeyFile.getName());
                }

            };
        }catch (ClassNotFoundException|NullPointerException|IOException e){

        }
    }

    @Override
    public void setFile(String type, File file) {
        if (type.equals("publicKey")) {
            pubKeyFile=file;
        }else if (type.equals("privateKey")) {
            privKeyFile=file;
        }else if(type.equals("inputFile")){
            inputFile=file;
        }else if(type.equals("signature")){
            signatureFile=file;
        }
    }

    @Override
    public File getFile(String marker) {
        if (marker.equals("publicKey")) {
            return pubKeyFile;
        }else if (marker.equals("privateKey")) {
            return privKeyFile;
        }else if(marker.equals("inputFile")){
            return inputFile;
        }else if(marker.equals("signature")){
            return signatureFile;
        }else if(marker.equals("signature")){
            return signatureFile;
        }
        return null;
    }

    @Override
    public void setMode(MODEUtil.MODE runMode) {
        this.runMode=runMode;
    }
}
