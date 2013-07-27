package com.arcanix.jcompass;

import org.jruby.RubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class CallbackLogger {

    private final Logger logger;
    private final File configFileDirectory;

    public CallbackLogger(Logger logger, File configFile) {
        this.logger = logger;
        this.configFileDirectory = configFile.getParentFile();
    }

    private String getNormalizedFilename(String filename) {
        String normalizedFilename = filename;
        if (filename.startsWith("./")) {
            normalizedFilename = filename.substring(2);
        }
        return new File(this.configFileDirectory, normalizedFilename).getAbsolutePath();
    }

    public void onStylesheetSaved(String filename) {
        this.logger.info("  - File: " + getNormalizedFilename(filename) + " successfully updated");
    }

    public void onSpriteSaved(String filename) {
        this.logger.info("  - Sprite: " + getNormalizedFilename(filename) + " successfully updated");
    }

    public void onStylesheetError(String filename, String message) {
        this.logger.error("  - An error occured while updating file: " + getNormalizedFilename(filename) + ": " + message);
    }

}
