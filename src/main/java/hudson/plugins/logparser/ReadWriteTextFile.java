package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

public final class ReadWriteTextFile {
    private static final Logger LOGGER = Logger.getLogger(ReadWriteTextFile.class.getName());

    private ReadWriteTextFile() {
        // to suppress PMD warning
    }

    static public String getContents(final File aFile) {
        final StringBuilder contents = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new FileReader(aFile))) {
            String line = null; // not declared within while loop
            while ((line = input.readLine()) != null) {
                contents.append(line).append("\n");
            }
        } catch (IOException ex) {
            LOGGER.warning("Failure reading from " + aFile.getPath());
        }

        return contents.toString();
    }

    static public void setContents(final File aFile, final String aContents)
            throws FileNotFoundException, IOException {
        if (aFile == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!aFile.exists()) {
            boolean created = aFile.createNewFile();
            if (created) {
                LOGGER.fine(aFile.getPath() + " created");
            } else {
                LOGGER.fine(aFile.getPath() + " already exists");
            }
        }
        if (!aFile.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: "
                    + aFile);
        }
        if (!aFile.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: "
                    + aFile);
        }

        // use buffering
        try (Writer output = new BufferedWriter(new FileWriter(aFile))) {
            // FileWriter always assumes default encoding is OK!
            output.write(aContents);
        }
    }

}
