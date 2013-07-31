package com.arcanix.jcompass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class Slf4jCompassNotifier implements CompassNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slf4jCompassNotifier.class);

    @Override
    public void onCompilationStarted() {
        LOGGER.info("Updating stylesheets...");
    }

    @Override
    public void onCompilationEnded() {
        LOGGER.info("Done updating stylesheets");
    }

    @Override
    public void onFileChanged(File file) {
        LOGGER.info("File change detected to: " + file.getAbsolutePath());
    }

    @Override
    public void onFileCreated(File file) {
        LOGGER.info("File creation detected: " + file.getAbsolutePath());
    }

    @Override
    public void onFileDeleted(File file) {
        LOGGER.info("File deletion detected to: " + file.getAbsolutePath());
    }

    public void onStylesheetSaved(File file) {
        LOGGER.info("  - File: " + file.getAbsolutePath() + " successfully updated");
    }

    public void onSpriteSaved(File file) {
        LOGGER.info("  - Sprite: " + file.getAbsolutePath() + " successfully updated");
    }

    public void onStylesheetError(File file, String message) {
        LOGGER.error("  - An error occured while updating file: " + file.getAbsolutePath() + ": " + message);
    }

}
