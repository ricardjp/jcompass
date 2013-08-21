package com.arcanix.jcompass;

import java.nio.file.Path;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public interface FileListener {

    void fileCreated(Path path) throws Exception;
    void fileDeleted(Path path) throws Exception;
    void fileChanged(Path path) throws Exception;

}
