package nl.rubenschellekens.iconvert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ruben Schellekens
 */
public enum TempFileManager {

    INSTANCE;

    private List<File> tempFiles;

    TempFileManager() {
        this.tempFiles = new ArrayList<>();
    }

    /**
     * Makes a new temp file with a given name.
     */
    public File newFile() throws IOException {
        File file = File.createTempFile(Program.TEMP_FILE_NAME, Program.TEMP_FILE_EXT);
        tempFiles.add(file);
        return file;
    }

    /**
     * Clears all temporary files.
     */
    public void cleanup() {
        for (Iterator<File> it = tempFiles.listIterator(); it.hasNext();) {
            File next = it.next();
            boolean delete = next.delete();

            if (delete) {
                it.remove();
            }
        }
    }

}
