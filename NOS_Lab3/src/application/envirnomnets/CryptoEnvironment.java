package application.envirnomnets;

import application.utils.MODEUtil;

import java.io.File;

/**
 * Created by vribic on 28.04.17..
 */
public abstract class CryptoEnvironment {

    public abstract void run();

    public abstract void setFile(String type, File file);

    public abstract File getFile(String marker);

    public abstract void setMode(MODEUtil.MODE runMode);

}
