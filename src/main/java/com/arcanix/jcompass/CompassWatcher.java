package com.arcanix.jcompass;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class CompassWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompassWatcher.class);

    private final CompassCompiler compassCompiler;

    public CompassWatcher(CompassCompiler compassCompiler) {
        if (compassCompiler == null) {
            throw new NullPointerException("Compass compiler cannot be null");
        }
        this.compassCompiler = compassCompiler;
    }

    public void watch() throws FileSystemException {

        LOGGER.info("Lauching compass watcher...");
        FileSystemManager manager = VFS.getManager();
        FileObject file = manager.resolveFile(
                this.compassCompiler.getConfigFile().getParentFile().getAbsolutePath());

        DefaultFileMonitor fm = new DefaultFileMonitor(new CompassWatchListener(this.compassCompiler));
        fm.setRecursive(true);
        fm.setDelay(2000);
        fm.addFile(file);
        fm.start();
    }

}
