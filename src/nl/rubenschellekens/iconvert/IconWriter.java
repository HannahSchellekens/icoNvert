package nl.rubenschellekens.iconvert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ruben Schellekens
 */
public class IconWriter {

    private final File destination;
    private final List<BufferedImage> images;
    private List<File> pngFiles;
    private OutputStream out;
    private Tracker tracker;

    /**
     * @throws IllegalArgumentException
     *         when there are no images to write to the icon file OR when there are too many images
     *         OR when some images are bigger than 256x256.
     */
    public IconWriter(File destination, List<BufferedImage> images, Tracker tracker) throws
            IllegalArgumentException {
        if (images.size() <= 0) {
            throw new IllegalArgumentException("There are no images to write.");
        }

        if (images.size() > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("There are too many images (>" + Byte.MAX_VALUE +
                    ").");
        }

        for (BufferedImage image : images) {
            if (image.getWidth() > 256 || image.getHeight() > 256) {
                throw new IllegalArgumentException("Some images are too big (>256x256).");
            }
        }

        this.destination = destination;
        this.images = images;
        this.tracker = tracker;
    }

    public void write() throws IOException {
        generatePng();

        out = new FileOutputStream(destination);

        writeHeader();
        writeImages();

        out.close();
    }

    private void writeHeader() throws IOException {
        // Reserved
        out.write(ByteBuffer.allocate(2).putShort((short)0).array());

        // Image type (1 ICO, 2 CUR)
        out.write((byte)1);
        out.write((byte)0);

        // Number of images
        out.write((byte)pngFiles.size());
        out.write((byte)0);
    }

    private void writeImages() throws IOException {
        for (int i = 0; i < images.size(); i++) {
            writeImageData(i);
            tracker.addProgress(0.05f / (float)images.size());
        }

        for (int i = 0; i < images.size(); i++) {
            writeImage(i);
            tracker.addProgress(0.65f / (float)images.size());
        }
    }

    private void writeImageData(int index) throws IOException {
        BufferedImage image = images.get(index);
        File file = pngFiles.get(index);

        // Image width
        byte width = (byte)(image.getWidth() == 256 ? 0 : image.getWidth());
        out.write(width);

        // Image height
        byte height = (byte)(image.getHeight() == 256 ? 0 : image.getHeight());
        out.write(height);

        // Colour palette
        out.write((byte)0);

        // Reserved (0)
        out.write((byte)0);

        // Colour planes (1)
        write(ByteBuffer.allocate(2).putShort((short)1).array());

        // Bits per pixel (8)
        write(ByteBuffer.allocate(2).putShort((short)8).array());

        // Size in bytes
        write(ByteBuffer.allocate(4).putInt((int)file.length()).array());

        // Offset
        int offset = 6 + 16 * images.size() + fileSizeSumUpUntil(index);
        write(ByteBuffer.allocate(4).putInt(offset).array());
    }

    /**
     * First flips the byte order.
     */
    private void write(byte[] bytes) throws IOException {
        for (int i = 0; i < bytes.length / 2; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
        }
        out.write(bytes);
    }

    private int fileSizeSumUpUntil(int i) {
        int sum = 0;
        for (int ii = 0; ii < i; ii++) {
            sum += pngFiles.get(ii).length();
        }
        return sum;
    }

    private void writeImage(int index) throws IOException {
        InputStream is = new FileInputStream(pngFiles.get(index));

        int b = -1;
        while ((b = is.read()) != -1) {
            out.write(b);
        }

        is.close();
    }

    private void generatePng() throws IOException {
        pngFiles = new ArrayList<>();

        float count = 0;
        for (BufferedImage image : images) {
            File output = TempFileManager.INSTANCE.newFile();
            ImageIO.write(image, "png", output);
            pngFiles.add(output);

            if (output.length() > Integer.MAX_VALUE) {
                throw new IllegalStateException("File " + output + " is too big (" + output
                        .length() + ">" + Integer.MAX_VALUE + ").");
            }

            tracker.addProgress(++count * 0.3f / (float)images.size());
        }
    }

}
