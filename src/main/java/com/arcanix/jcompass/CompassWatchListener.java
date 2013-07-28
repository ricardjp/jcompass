package com.arcanix.jcompass;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public class CompassWatchListener implements FileListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompassWatchListener.class);

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
            LOGGER.info("File change detected to: " + fileChangeEvent.getFile().getName().getPath());
            recompile();
        }
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {
        if (isScss(fileChangeEvent.getFile())) {
            LOGGER.info("File creation detected: " + fileChangeEvent.getFile().getName().getPath());
            recompile();
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
        if (isScss(fileChangeEvent.getFile())) {
            LOGGER.info("File deletion detected to: " + fileChangeEvent.getFile().getName().getPath());
            recompile();
        }
    }
}
