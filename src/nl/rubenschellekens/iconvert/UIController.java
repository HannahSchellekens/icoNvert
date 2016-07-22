package nl.rubenschellekens.iconvert;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Ruben Schellekens
 */
public class UIController implements Initializable {

    @FXML private TextField txtSource;
    @FXML private TextField txtOutput;
    @FXML private ImageView imgPreview;
    @FXML private Text textSize;
    @FXML private Text textSizeWarning;
    @FXML private Text textDone;
    @FXML private Tab tabSimple;
    @FXML private Tab tabAdvanced;
    @FXML private Tab tabSettings;
    @FXML private CheckBox checkSize256;
    @FXML private CheckBox checkSize180;
    @FXML private CheckBox checkSize108;
    @FXML private CheckBox checkSize96;
    @FXML private CheckBox checkSize72;
    @FXML private CheckBox checkSize64;
    @FXML private CheckBox checkSize48;
    @FXML private CheckBox checkSize32;
    @FXML private CheckBox checkSize24;
    @FXML private CheckBox checkSize16;
    @FXML private ProgressBar progSimple;
    @FXML private ProgressIndicator progPreview;
    @FXML private RadioButton radioScale;
    @FXML private RadioButton radioCrop;
    @FXML private Button btnConvertSimple;

    private ResourceBundle resources;
    private Stage stage;

    private File input;
    private File output;
    private List<Integer> sizes;
    private BufferedImage image;

    public UIController(Stage stage) {
        this.stage = stage;
        this.sizes = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        txtSource.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                File file = new File(txtSource.getText().trim());
                if (!file.exists()) {
                    new Dialog(resources.getString("dialog.title.oops"),
                            resources.getString("dialog.message.file-doesnt-exist"), stage).show();
                    return;
                }

                input = file;
                processInput();
            }
        });
    }

    public void reload() {
        File file = new File(txtSource.getText().trim());
        if (!file.exists()) {
            new Dialog(resources.getString("dialog.title.oops"),
                    resources.getString("dialog.message.file-doesnt-exist"), stage).show();
            return;
        }

        input = file;
        processInput();
    }

    public void onConvertSimple() {
        output = new File(txtOutput.getText().trim());
        if (output.exists()) {
            ConfirmDialog dialog = new ConfirmDialog(
                    resources.getString("dialog.title.are-you-sure"),
                    resources.getString("dialog.message.already-exists"), stage);
            if (dialog.show() != DialogResult.YES) {
                return;
            }
        }

        // Check sizes
        sizes.clear();
        addIf(256, checkSize256);
        addIf(180, checkSize180);
        addIf(108, checkSize108);
        addIf(96, checkSize96);
        addIf(72, checkSize72);
        addIf(64, checkSize64);
        addIf(48, checkSize48);
        addIf(32, checkSize32);
        addIf(24, checkSize24);
        addIf(16, checkSize16);

        if (sizes.size() <= 0) {
            new Dialog(resources.getString("dialog.title.wrong-input"),
                    resources.getString("dialog.message.no-sizes-selected"),
                    stage).show();
            return;
        }

        // Start making
        Tracker tracker = new Tracker(-1);
        tracker.setListener(d -> Platform.runLater(() -> progSimple.setProgress(d)));
        textDone.setText("");
        IconMaker maker = new IconMaker(image, sizes, output, tracker);
        new Thread(() -> {
            maker.make();
            Platform.runLater(() -> textDone.setText(resources.getString("general.done") + " (" +
                    tracker.getSeconds() + "s)"));
            Platform.runLater(() -> btnConvertSimple.setDisable(false));
        }).start();
    }

    private void addIf(int number, CheckBox box) {
        if (box.isSelected()) {
            sizes.add(number);
        }
    }

    public void onBrowseSimple() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("dialog.title.select-output"));
        fileChooser.getExtensionFilters().add(Util.getIconExtensionFilter());

        if (input != null) {
            fileChooser.setInitialDirectory(input.getParentFile());
        }

        File selected = fileChooser.showSaveDialog(stage);
        if (selected == null) {
            return;
        }

        txtOutput.setText(selected.getAbsolutePath());
    }

    public void onSelectSimple() {
        // Select the image to convert.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("general.select-image"));
        fileChooser.getExtensionFilters().add(Util.getImageExtensionFilter());

        File selected = fileChooser.showOpenDialog(stage);
        if (selected == null) {
            return;
        }

        input = selected;

        processInput();
    }

    private void processInput() {
        progPreview.setVisible(true);
        imgPreview.setImage(null);
        txtSource.setText(input.getAbsolutePath());

        // Determine an auto-generated output path.
        output = new File(input.getPath().replaceAll("(\\.[a-zA-Z]{3}[a-zA-Z]?)$", ".ico"));
        txtOutput.setText(output.getAbsolutePath());

        // Load the image.
        new Thread(() -> {
            BufferedImage previewImage = null;
            try {
                previewImage = ImageIO.read(input);
            }
            catch (IOException e) {
                new Dialog(resources.getString("dialog.title.problem"),
                        resources.getString("dialog.message.error.couldn-read-image"), stage);
                e.printStackTrace();
            }

            // Add image data.
            int width = previewImage.getWidth();
            int height = previewImage.getHeight();
            Platform.runLater(() -> textSize.setText(width + "×" + height));

            // Resize
            if (width != height) {
                int newSize = Math.min(width, height);

                if (radioScale.isSelected()) {
                    image = Util.scale(previewImage, newSize);
                }
                else {
                    image = Util.crop(previewImage, newSize);
                }

                Platform.runLater(() -> textSize.setText(newSize + "×" + newSize + " (" +
                        resources.getString("general.original") + " " + width + "×" + height + ")" +
                        ""));

                if (radioScale.isSelected()) {
                    Platform.runLater(() -> textSizeWarning.setText(resources.getString("general" +
                            ".resized-scale")));
                }
                else {
                    Platform.runLater(() -> textSizeWarning.setText(resources.getString("general" +
                            ".resized-crop")));
                }
            }
            else {
                image = previewImage;
            }

            Platform.runLater(() -> imgPreview.setImage(SwingFXUtils.toFXImage(image, null)));
            Platform.runLater(() -> progPreview.setVisible(false));
        }).start();
    }

}
