package com.arcanix.jcompass;

import java.io.File;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public interface CompassNotifier {

    void onCompilationStarted();

    void onCompilationEnded();

    void onFileChanged(File file);

    void onFileCreated(File file);

    void onFileDeleted(File file);

    void onStylesheetSaved(File file);

    void onSpriteSaved(File file);

    void onStylesheetError(File file, String message);
}
