package application.utils;

/**
 * Created by vribic on 09.05.17..
 */
public class MODEUtil {
    /**
     * Created by vribic on 28.04.17..
     */
    public static enum MODE {
        ENCRYPT,DECRYPT,DEFAULT_HASH, MYIMPLEMENTATION, GENERATE, VERIFY, NONE
    }

    public static MODE str2mode(String s){
        if(s.equals("encript")){
            return MODE.ENCRYPT;
        }else if(s.equals("decript")){
            return MODE.DECRYPT;
        }else if(s.equals("default")){
            return MODE.DEFAULT_HASH;
        }else if(s.equals("mine")){
            return MODE.MYIMPLEMENTATION;
        }else if(s.equals("generate")){
            return MODE.GENERATE;
        }else if(s.equals("verify")){
            return MODE.VERIFY;
        }else{
            return MODE.NONE;
        }

    }
}
