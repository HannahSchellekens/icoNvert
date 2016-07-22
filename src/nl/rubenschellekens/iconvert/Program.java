package nl.rubenschellekens.iconvert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Ruben Schellekens
 */
public class Program extends Application {

    public static final String VERSION = "1";
    public static final String TEMP_FILE_NAME = "iconvert-png";
    public static final String TEMP_FILE_EXT = ".tmp";

    public static ResourceBundle resources;
    public static Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI.fxml"));
        loader.setController(new UIController(primaryStage));
        resources = ResourceBundle.getBundle("bundles.lang", new Locale("en", "GB"));
        loader.setResources(resources);
        Parent root = loader.load();
        root.getStylesheets().add("style.css");

        primaryStage.setTitle("icoNvert " + VERSION);
        primaryStage.getIcons().add(new Image("/icon.png"));
        primaryStage.setScene(new Scene(root, 678, 555));
        primaryStage.setOnCloseRequest(e -> TempFileManager.INSTANCE.cleanup());
        primaryStage.show();
    }

}
