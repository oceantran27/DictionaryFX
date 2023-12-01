package uet.cs.dictionaryfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DictionaryApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menuGUI.fxml"));
        Parent root = loader.load();
        StageManager.setPrimaryStage(primaryStage);
        StageManager.showStage(new Scene(root));
    }

    public static void main(String[] args) {
        launch(args);
    }
}