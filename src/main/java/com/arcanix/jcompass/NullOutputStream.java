package com.arcanix.jcompass;

import java.io.OutputStream;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class NullOutputStream extends OutputStream {

    @Override
    public void write(int b) {}

    @Override
    public void write(byte[] b) {}

    @Override
    public void write(byte[] b, int off, int len) {}

}
