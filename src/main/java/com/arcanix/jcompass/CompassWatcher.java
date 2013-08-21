package com.arcanix.jcompass;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class CompassWatcher {

    private final CompassCompiler compassCompiler;

    private Thread monitor;

    public CompassWatcher(CompassCompiler compassCompiler) {
        if (compassCompiler == null) {
            throw new NullPointerException("Compass compiler cannot be null");
        }
        this.compassCompiler = compassCompiler;
    }

    public void watch() {
        this.monitor = new Thread(new RecusiveDirectoryWatcher(
                this.compassCompiler.getConfigFile().getParentFile().toPath(),
                new CompassWatchListener(this.compassCompiler),
                true));
        this.monitor.start();
    }

    public void stop() {
        if (this.monitor != null) {
            this.monitor.interrupt();
        }
    }

}
