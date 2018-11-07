package application;

import application.envirnomnets.MasterCryptoEnvironment;
import application.utils.CryptoConfigFile;
import application.utils.Editor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class Controller{

    public Scene scene;
    private FileChooser fc=new FileChooser();
    private MasterCryptoEnvironment masterEnv=new MasterCryptoEnvironment();

    //Editor
    @FXML TextArea textEditor;
    @FXML Button editorSave;
    @FXML Button editorDiscard;
    public static Editor editor;

    //#################################################################################################
    //#################################################################################################
    //#################################################################################################

    //AES
    //Key
    @FXML TextField AES_key_input;
    @FXML Button AES_key_select;
    @FXML Button AES_key_edit;
    @FXML Button AES_key_generate;

    //Input file
    @FXML TextField AES_inputFile_input;
    @FXML Button AES_inputFile_select;
    @FXML Button AES_inputFile_edit;

    //Output file
    @FXML TextField AES_outputFile_input;
    @FXML Button AES_outputFile_select;
    @FXML Button AES_outputFile_edit;

    //Encript
    @FXML RadioButton AES_encrypt;

    //Run
    @FXML Button AES_run;

    //#################################################################################################
    //#################################################################################################
    //#################################################################################################

    //RSA
    //PubKey
    @FXML TextField RSA_pubKey_input;
    @FXML Button RSA_pubKey_select;
    @FXML Button RSA_pubKey_edit;

    //SecKey
    @FXML TextField RSA_secKey_input;
    @FXML Button RSA_secKey_select;
    @FXML Button RSA_secKey_edit;


    //Input file
    @FXML TextField RSA_inputFile_input;
    @FXML Button RSA_inputFile_select;
    @FXML Button RSA_inputFile_edit;

    //Output file
    @FXML TextField RSA_outputFile_input;
    @FXML Button RSA_outputFile_select;
    @FXML Button RSA_outputFile_edit;

    //Encript
    @FXML RadioButton RSA_encrypt;

    //Run
    @FXML Button RSA_run;

    //#################################################################################################
    //#################################################################################################
    //#################################################################################################

    //SHA1

    //Input file
    @FXML TextField SHA1_inputFile_input;
    @FXML Button SHA1_inputFile_select;
    @FXML Button SHA1_inputFile_edit;

    //Output file
    @FXML TextField SHA1_outputFile_input;
    @FXML Button SHA1_outputFile_select;
    @FXML Button SHA1_outputFile_edit;

    //Run
    @FXML Button SHA1_run;

    //#################################################################################################
    //#################################################################################################
    //#################################################################################################

    //Digital envelope
    @FXML TextField DE_inputFile_input;
    @FXML TextField DE_publicKey_input;
    @FXML TextField DE_envelope_input;
    @FXML TextField DE_privateKey_input;
    @FXML TextField DE_outputFile_input;

    //#################################################################################################
    //#################################################################################################
    //#################################################################################################

    //Digital signature
    @FXML TextField DS_inputFile_input;
    @FXML TextField DS_privateKey_input;
    @FXML TextField DE_signature_input;
    @FXML TextField DS_publicKey_input;

    //#################################################################################################
    //#################################################################################################
    //#################################################################################################

    //Digital stamp
    @FXML TextField DST_inputFile_input;
    @FXML TextField DST_publicKeyR_input;
    @FXML TextField DST_privateKeyS_input;
    @FXML TextField DST_envelope_input;
    @FXML TextField DST_signature_input;
    @FXML TextField DST_publicKeyS_input;
    @FXML TextField DST_privateKeyR_input;
    @FXML TextField DST_outputFile_input;

    //#################################################################################################
    //#################################################################################################
    //#################################################################################################


    @FXML
    public void selectFile(ActionEvent event) throws IOException {
        String senderId=((Control)event.getSource()).getId();
        File file=fc.showOpenDialog(scene.getWindow());
        //Save changes
        masterEnv.selectFile(senderId,file);

        //Show changes
        String[] senderFields=senderId.split("_");
        String textId="#"+senderFields[0]+"_"+senderFields[1]+"_input";
        TextField textFiles=(TextField)scene.lookup(textId);
        textFiles.setText(file.getAbsolutePath());

    }

    @FXML
    public void editFile(ActionEvent event){
        String senderId=((Control)event.getSource()).getId();
        editor.clear();
        masterEnv.displayFile(senderId,editor);
    }

    @FXML
    public void modeChange(ActionEvent event){
        String senderId=((Control)event.getSource()).getId();
        masterEnv.setMode(senderId);
    }

    @FXML
    public void run(ActionEvent event){
        String senderId=((Control)event.getSource()).getId();
        String[] senderFields=senderId.split("_");
        String textId="#"+senderFields[0]+"_progress_indicator";
        ProgressIndicator progressIndicator=(ProgressIndicator)scene.lookup(textId);

        progressIndicator.setProgress(0);
        masterEnv.run(senderId);
        progressIndicator.setProgress(1);


    }

    @FXML
    public void runAdvanced(ActionEvent event){
        String senderId=((Control)event.getSource()).getId();
        String[] senderFields=senderId.split("_");
        String textId="#"+senderFields[0]+"_progress_indicator";
        ProgressIndicator progressIndicator=(ProgressIndicator)scene.lookup(textId);

        progressIndicator.setProgress(0);
        masterEnv.runAdvanced(senderId);
        progressIndicator.setProgress(1);


    }

    @FXML
    public void editorSaveChanges(ActionEvent event){
        editor.save();
    }

    @FXML
    public void editorDiscardChanges(ActionEvent event){
        editor.discard();
    }

    @FXML
    public void generateRSAKeys(ActionEvent event) throws IOException {
        if(!RSA_pubKey_input.getText().isEmpty() && !RSA_secKey_input.getText().isEmpty()){
            KeyPair keys=masterEnv.getRSAKeys(1024);
            //Save public key
            CryptoConfigFile pubConf=new CryptoConfigFile();
            File pubFile=new File(RSA_pubKey_input.getText());
            pubConf.put(CryptoConfigFile.DESCRIPTION,"Secret file");
            pubConf.put(CryptoConfigFile.METHOD,"RSA");

            //Pub key
            ByteArrayOutputStream pubObj=new ByteArrayOutputStream();
            new ObjectOutputStream(pubObj).writeObject(keys.getPublic());
            pubConf.put(CryptoConfigFile.PRIVATE_EXPONENT,new String(Base64.getEncoder().encode(pubObj.toByteArray())));
            //Modulus
            ByteArrayOutputStream modulusObj=new ByteArrayOutputStream();
            new ObjectOutputStream(modulusObj).writeObject(((RSAPublicKey) keys.getPublic()).getModulus());
            pubConf.put(CryptoConfigFile.MODULUS,new String(Base64.getEncoder().encode(modulusObj.toByteArray())));


            pubConf.saveToFile(pubFile);
            pubObj.close();

            //Save private key
            CryptoConfigFile secConf=new CryptoConfigFile();
            File secFile=new File(RSA_secKey_input.getText());
            secConf.put(CryptoConfigFile.DESCRIPTION,"Secret file");
            secConf.put(CryptoConfigFile.METHOD,"RSA");

            //SecKey
            ByteArrayOutputStream secObj=new ByteArrayOutputStream();
            new ObjectOutputStream(secObj).writeObject(keys.getPrivate());
            secConf.put(CryptoConfigFile.PRIVATE_EXPONENT,new String(Base64.getEncoder().encode(secObj.toByteArray())));
            //Modulus
            secConf.put(CryptoConfigFile.MODULUS,new String(Base64.getEncoder().encode(modulusObj.toByteArray())));

            secConf.saveToFile(secFile);
            secObj.close();
            modulusObj.close();
        }
    }

    @FXML
    public void generateAESKey(ActionEvent event){
        if(!AES_key_input.getText().isEmpty()){
            String key=masterEnv.getAESKeys(128);
            //Save public key
            CryptoConfigFile pubConf=new CryptoConfigFile();
            File pubFile=new File(AES_key_input.getText());
            pubConf.put(CryptoConfigFile.DESCRIPTION,"Secret file");
            pubConf.put(CryptoConfigFile.METHOD,"AES");
            pubConf.put(CryptoConfigFile.SECRET_KEY,key);
            pubConf.saveToFile(pubFile);
        }
    }

    //Used by the program \ non events
    void setScene(Scene scene) {
        this.scene = scene;
        this.initDefaults();
    }

    private void initDefaults() {
        //Window edits
        editor=new Editor(textEditor);

        //AES
        AES_key_input.setText(DefaultPaths.AES_KEY_PATH);
        AES_inputFile_input.setText(DefaultPaths.AES_INPUTFILE_PATH);
        AES_outputFile_input.setText(DefaultPaths.AES_OUTPUTFILE_PATH);
        //RSA
        RSA_pubKey_input.setText(DefaultPaths.RSA_PUBKEY_PATH);
        RSA_secKey_input.setText(DefaultPaths.RSA_SECKEY_PATH);
        RSA_inputFile_input.setText(DefaultPaths.RSA_INPUTFILE_PATH);
        RSA_outputFile_input.setText(DefaultPaths.RSA_OUTPUTFILE_PATH);
        //SHA1
        SHA1_inputFile_input.setText(DefaultPaths.SHA1_INPUTFILE_PATH);
        SHA1_outputFile_input.setText(DefaultPaths.SHA1_OUTPUTFILE_PATH);
        //Digital envelope
        DE_inputFile_input.setText(DefaultPaths.DE_INPUTFILE_PATH);
        DE_publicKey_input.setText(DefaultPaths.DE_PUBKEY_PATH);
        DE_envelope_input.setText(DefaultPaths.DE_ENVELOPE_PATH);
        DE_privateKey_input.setText(DefaultPaths.DE_PUBKEY_PATH);
        DE_outputFile_input.setText(DefaultPaths.DE_OUTPUTFILE_PATH);

        //Digital signature
        DS_inputFile_input.setText(DefaultPaths.DS_INPUTFILE_PATH);
        DS_privateKey_input.setText(DefaultPaths.DS_SECKEY_PATH);
        DE_signature_input.setText(DefaultPaths.DS_SIGNATURE_PATH);
        DS_publicKey_input.setText(DefaultPaths.DS_PUBKEY_PATH);

        //Digital stamp
        DST_inputFile_input.setText(DefaultPaths.DST_INPUTFILE_PATH);
        DST_publicKeyR_input.setText(DefaultPaths.DST_PUBKEY_RECI_PATH);
        DST_privateKeyS_input.setText(DefaultPaths.DST_SECKEY_SEND_PATH);
        DST_envelope_input.setText(DefaultPaths.DST_ENVELOPE_PATH);
        DST_signature_input.setText(DefaultPaths.DST_SIGNATURE_PATH);
        DST_publicKeyS_input.setText(DefaultPaths.DST_PUBKEY_SEND_PATH);
        DST_privateKeyR_input.setText(DefaultPaths.DST_SECKEY_RECI_PATH);
        DST_outputFile_input.setText(DefaultPaths.DST_OUTPUTFILE_PATH);

    }
}
