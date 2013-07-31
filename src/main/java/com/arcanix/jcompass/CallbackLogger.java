package com.arcanix.jcompass;

import java.io.File;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class CallbackLogger {

    private final File configFileDirectory;
    private final CompassNotifier compassNotifier;

    private boolean loggedError = false;

    public CallbackLogger(File configFile, CompassNotifier compassNotifier) {
        this.configFileDirectory = configFile.getParentFile();
        this.compassNotifier = compassNotifier;
    }

    private File getNormalizedFile(String filename) {
        String normalizedFilename = filename;
        if (filename.startsWith("./")) {
            normalizedFilename = filename.substring(2);
        }
        return new File(this.configFileDirectory, normalizedFilename);
    }

    public void onStylesheetSaved(String filename) {
        this.compassNotifier.onStylesheetSaved(getNormalizedFile(filename));
    }

    public void onSpriteSaved(String filename) {
        this.compassNotifier.onSpriteSaved(getNormalizedFile(filename));
    }

    public void onStylesheetError(String filename, String message) {
        this.loggedError = true;
        this.compassNotifier.onStylesheetError(getNormalizedFile(filename), message);
    }

    public boolean hasLoggedError() {
        return this.loggedError;
    }

}
