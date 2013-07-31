package com.arcanix.jcompass;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;

import java.io.File;
import java.io.IOException;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public class CompassWatchListener implements FileListener {

    private static final String SASS_EXTENSION = "scss";

    private final CompassCompiler compassCompiler;

    public CompassWatchListener(CompassCompiler compassCompiler) {
        this.compassCompiler = compassCompiler;
    }

    public void recompile() throws CompassCompilerException, IOException {
        this.compassCompiler.compile();
    }

    public boolean isScss(FileObject fileObject) {
        return SASS_EXTENSION.equals(fileObject.getName().getExtension());
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
        if (isScss(fileChangeEvent.getFile())) {
            this.compassCompiler.getCompassNotifier().onFileChanged(
                    new File(fileChangeEvent.getFile().getName().getPath()));
            recompile();
        }
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {
        if (isScss(fileChangeEvent.getFile())) {
            this.compassCompiler.getCompassNotifier().onFileCreated(
                    new File(fileChangeEvent.getFile().getName().getPath()));
            recompile();
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
        if (isScss(fileChangeEvent.getFile())) {
            this.compassCompiler.getCompassNotifier().onFileDeleted(
                    new File(fileChangeEvent.getFile().getName().getPath()));
            recompile();
        }
    }
}
