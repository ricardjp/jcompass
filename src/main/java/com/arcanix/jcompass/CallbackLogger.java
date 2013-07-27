package com.arcanix.jcompass;

import org.jruby.RubyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class CallbackLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackLogger.class);

    private final File configFileDirectory;

    private boolean loggedError = false;

    public CallbackLogger(File configFile) {
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
        LOGGER.info("  - File: " + getNormalizedFilename(filename) + " successfully updated");
    }

    public void onSpriteSaved(String filename) {
        LOGGER.info("  - Sprite: " + getNormalizedFilename(filename) + " successfully updated");
    }

    public void onStylesheetError(String filename, String message) {
        LOGGER.error("  - An error occured while updating file: " + getNormalizedFilename(filename) + ": " + message);
        this.loggedError = true;
    }

    public boolean hasLoggedError() {
        return this.loggedError;
    }

}
