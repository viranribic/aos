package application.envirnomnets.SHA1;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by vribic on 09.05.17..
 */
public class SHA1 {

    public static String hash(String message){
        try {
            MessageDigest cript = MessageDigest.getInstance("SHA-1");
            cript.reset();
            cript.update(message.getBytes("utf8"));
            return new String(cript.digest());
        } catch (NoSuchAlgorithmException |UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
