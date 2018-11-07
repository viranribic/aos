package application;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Advanced Operating Systems");
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/application/cryptGUI.fxml"));
        Parent root = loader.load();
        Controller controller=loader.getController();
        Scene scene=new Scene(root, 1280, 800);
        controller.setScene(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
