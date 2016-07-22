package nl.rubenschellekens.iconvert;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ruben Schellekens
 */
public class IconMaker {

    private List<Integer> sizes;
    private BufferedImage original;
    private Tracker tracker;
    private File output;

    public IconMaker(BufferedImage original, List<Integer> sizes, File output, Tracker tracker) {
        this.original = original;
        this.sizes = sizes;
        this.output = output;
        this.tracker = tracker;
    }

    public void make() {
        tracker.start();

        List<BufferedImage> images = new ArrayList<>();

        float count = 0;
        for (int size : sizes) {
            images.add(Util.scale(original, size));
            tracker.addProgress(0.3f / (float)images.size());
        }

        IconWriter writer = new IconWriter(output, images, tracker);

        try {
            writer.write();
        }
        catch (IOException | IllegalArgumentException | IllegalStateException e) {
            new Dialog(Program.resources.getString("dialog.title.oops"),
                    Program.resources.getString("dialog.message.error.write") + "\n" + e.getMessage(),
                    Program.mainStage).showLater();
            e.printStackTrace();
            tracker.setProgress(0d);
            return;
        }

        tracker.setProgress(0.99);
        TempFileManager.INSTANCE.cleanup();
        tracker.setProgress(1);
    }

}
