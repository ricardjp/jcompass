package com.arcanix.jcompass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public class CompassWatchListener implements FileListener {

    private static final String SASS_EXTENSION = ".scss";

    private final CompassCompiler compassCompiler;

    public CompassWatchListener(CompassCompiler compassCompiler) {
        this.compassCompiler = compassCompiler;
    }

    public void recompile() throws CompassCompilerException, IOException {
        this.compassCompiler.compile();
    }

    public boolean isScss(Path path) {
        return path.toString().toLowerCase().endsWith(SASS_EXTENSION);
    }

    @Override
    public void fileChanged(Path path) throws Exception {
        if (isScss(path)) {
            this.compassCompiler.getCompassNotifier().onFileChanged(path.toFile());
            recompile();
        }
    }

    @Override
    public void fileCreated(Path path) throws Exception {
        if (isScss(path)) {
            this.compassCompiler.getCompassNotifier().onFileCreated(path.toFile());
            recompile();
        }
    }

    @Override
    public void fileDeleted(Path path) throws Exception {
        if (isScss(path)) {
            this.compassCompiler.getCompassNotifier().onFileDeleted(path.toFile());
            recompile();
        }
    }
}
