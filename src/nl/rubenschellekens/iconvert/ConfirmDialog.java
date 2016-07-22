package nl.rubenschellekens.iconvert;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Pop-up confirm dialog.
 *
 * @author Ruben Schellekens
 */
public class ConfirmDialog implements Initializable {

    /**
     * The title of the dialog.
     */
    private final String title;

    /**
     * The message to show in the dialog.
     */
    private final String message;

    /**
     * The stage that displays the dialog.
     */
    private Stage stage;

    /**
     * YES when 'yes' was clicked. NO when 'no' was clicked. ERROR when an error happened.
     */
    private DialogResult result;

    /**
     * The parent stage to center the dialog on.
     * <p>
     * <code>null</code> for centering on screen.
     */
    private Stage parent;

    @FXML private Text textMessage;

    public ConfirmDialog(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public ConfirmDialog(String title, String message, Stage parent) {
        this(title, message);
        this.parent = parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stage.setTitle(title);
        textMessage.setText(message);
    }

    /**
     * Shows the dialog and waits for the user to close it.
     */
    public DialogResult show() {
        try {
            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConfirmDialog.fxml"));
            loader.setController(this);
            loader.setResources(Program.resources);
            Parent root = loader.load();
            root.getStylesheets().add("/style.css");

            stage.setTitle(title);
            double width = 380;
            double height = 110 + textMessage.prefHeight(-1);
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);

            if (parent != null) {
                stage.setX(parent.getX() + parent.getWidth() / 2 - width / 2);
                stage.setY(parent.getY() + parent.getHeight() / 2 - height / 2);
            }

            stage.showAndWait();

            return result;
        }
        catch (IOException ioe) {
            this.result = DialogResult.ERROR;
            throw new RuntimeException("Problem with opening the Dialog.", ioe);
        }
    }

    public void close() {
        stage.close();
    }

    public void yes() {
        this.result = DialogResult.YES;
        close();
    }

    public void no() {
        this.result = DialogResult.NO;
        close();
    }

    public DialogResult getResult() {
        return result;
    }
}
