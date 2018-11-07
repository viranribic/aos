package application.utils;

import javafx.scene.control.TextArea;

import java.io.*;

/**
 * Created by vribic on 28.04.17..
 */
public class Editor {
    private final TextArea txtArea;
    private File displayFile;

    public Editor(TextArea txtArea) {
        this.txtArea=txtArea;
    }

    public void writeLine(String line) {
        if(txtArea!=null)
            txtArea.appendText(line);
    }

    public void setDisplayFile(File displayFile) {
        this.displayFile = displayFile;
    }

    public void showDisplayFile() {
        if(displayFile==null)
            return;
        try {
            try(BufferedReader reader=new BufferedReader(new FileReader(displayFile))){
                while(true){
                    String line=reader.readLine();
                    if (line==null) break;
                    this.writeLine(line+"\n");
                }
            }
        } catch (IOException e) {
            this.writeLine("File:\n"+displayFile.getName()+"\nCould not be opened.");
        }
    }

    public void clear() {
        if(displayFile!=null)
            this.txtArea.clear();
    }

    public void save() {
        if(displayFile==null)
            return;

        try {
            FileUtils.writeWholeString(displayFile,txtArea.getText());
        } catch (IOException e) {
            this.writeLine("File:\n"+displayFile.getName()+"\nCould not be saved.");
        }
    }

    public void discard() {
        this.clear();
        this.displayFile=null;
    }
}
