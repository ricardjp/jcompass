package com.arcanix.jcompass;

import java.io.File;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public interface CompassNotifier {

    void onCompilationStarted();

    void onCompilationEnded();

    void onStylesheetSaved(File file);

    void onSpriteSaved(File file);

    void onStylesheetError(File file, String message);
}
