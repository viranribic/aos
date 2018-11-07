package application.envirnomnets;

import application.DefaultPaths;
import application.envirnomnets.AES.AESEnvironment;
import application.envirnomnets.DigitalEnvelope.DigitalEnvelopeEnvironment;
import application.envirnomnets.DigitalSignature.DigitalSignatureEnvironment;
import application.envirnomnets.DigitalStamp.DigitalStampEnvironment;
import application.envirnomnets.RSA.RSAEnvironment;
import application.envirnomnets.SHA1.SHA1Environment;
import application.utils.Editor;
import application.utils.MODEUtil;

import java.io.File;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vribic on 28.04.17..
 */
public class MasterCryptoEnvironment {
    private static Map<String,CryptoEnvironment> envs=new HashMap<>();

    static {
        envs.put(AESEnvironment.label,new AESEnvironment(DefaultPaths.AES_KEY_PATH,DefaultPaths.AES_INPUTFILE_PATH,DefaultPaths.AES_OUTPUTFILE_PATH, MODEUtil.MODE.ENCRYPT));
        envs.put(RSAEnvironment.label,new RSAEnvironment(DefaultPaths.RSA_PUBKEY_PATH,DefaultPaths.RSA_SECKEY_PATH,DefaultPaths.RSA_INPUTFILE_PATH,DefaultPaths.RSA_OUTPUTFILE_PATH, MODEUtil.MODE.ENCRYPT));
        envs.put(SHA1Environment.label,new SHA1Environment(DefaultPaths.SHA1_INPUTFILE_PATH,DefaultPaths.SHA1_OUTPUTFILE_PATH));
        envs.put(DigitalEnvelopeEnvironment.label,new DigitalEnvelopeEnvironment(DefaultPaths.DE_INPUTFILE_PATH,DefaultPaths.DE_PUBKEY_PATH,DefaultPaths.DE_ENVELOPE_PATH,DefaultPaths.DE_SECKEY_PATH,DefaultPaths.DE_OUTPUTFILE_PATH, MODEUtil.MODE.GENERATE));
        envs.put(DigitalSignatureEnvironment.label,new DigitalSignatureEnvironment(DefaultPaths.DS_INPUTFILE_PATH,DefaultPaths.DS_SECKEY_PATH,DefaultPaths.DS_SIGNATURE_PATH,DefaultPaths.DS_PUBKEY_PATH,MODEUtil.MODE.GENERATE));
        envs.put(DigitalStampEnvironment.label,new DigitalStampEnvironment(DefaultPaths.DST_INPUTFILE_PATH,DefaultPaths.DST_PUBKEY_RECI_PATH,DefaultPaths.DST_SECKEY_SEND_PATH,DefaultPaths.DST_ENVELOPE_PATH,DefaultPaths.DST_SIGNATURE_PATH,DefaultPaths.DST_PUBKEY_SEND_PATH,DefaultPaths.DST_SECKEY_RECI_PATH,DefaultPaths.DST_OUTPUTFILE_PATH, MODEUtil.MODE.GENERATE));
    }

    public void selectFile(String senderId, File file) {
        String[] markers=senderId.split("_");
        envs.get(markers[0]).setFile(markers[1],file);
    }

    public void displayFile(String senderId, Editor editor) {
        String[] markers=senderId.split("_");
        editor.setDisplayFile(envs.get(markers[0]).getFile(markers[1]));
        editor.showDisplayFile();
    }

    public void setMode(String senderId) {
        String[] markers=senderId.split("_");
        envs.get(markers[0]).setMode(MODEUtil.str2mode(markers[0]));
    }

    public void run(String senderId) {
        String[] markers=senderId.split("_");
        envs.get(markers[0]).run();
    }

    public String getAESKeys(int keysize) {
        return ((AESEnvironment)envs.get(AESEnvironment.label)).generateKey(keysize);
    }

    public KeyPair getRSAKeys(int keysize) {
        return ((RSAEnvironment)envs.get(RSAEnvironment.label)).generateKeys(keysize);
    }

    public void runAdvanced(String senderId) {
        String[] markers=senderId.split("_");
        String mode=markers[0].charAt(markers[0].length()-1)=='1'?"generate":"verify";
        String key=markers[0].substring(0,markers[0].length()-1);
        envs.get(key).setMode(MODEUtil.str2mode(mode));
        envs.get(key).run();
    }
}
