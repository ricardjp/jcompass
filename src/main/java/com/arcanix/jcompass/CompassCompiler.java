package com.arcanix.jcompass;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.javasupport.JavaEmbedUtils;

import java.io.*;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class CompassCompiler {

    private final File configFile;
    private final CompassNotifier compassNotifier;
    private final CallbackLogger callbackLogger;

    private JavaEmbedUtils.EvalUnit unit = null;

    public CompassCompiler(File configFile) {
        this(configFile, new Slf4jCompassNotifier());
    }

    public CompassCompiler(File configFile, CompassNotifier compassNotifier) {
        if (configFile == null) {
            throw new NullPointerException("Configuration file cannot be null");
        }
        if (compassNotifier == null) {
            throw new NullPointerException("Compass notifier cannot be null");
        }
        this.configFile = configFile;
        this.compassNotifier = compassNotifier;
        this.callbackLogger = new CallbackLogger(this.configFile, this.compassNotifier);
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public CompassNotifier getCompassNotifier() {
        return this.compassNotifier;
    }

    public JavaEmbedUtils.EvalUnit getScript() throws FileNotFoundException {
        if (this.unit == null) {
            if (!this.configFile.exists()) {
                throw new FileNotFoundException("Configuration file does not exist");
            }

            ScriptingContainer scriptingContainer = new ScriptingContainer(
                    LocalContextScope.THREADSAFE,
                    LocalVariableBehavior.PERSISTENT);

            scriptingContainer.setOutput(new PrintStream(new NullOutputStream()));

            StringBuilder script = new StringBuilder()
                    .append("require 'compass'\n")
                    .append("require 'compass/sass_compiler'\n")
                    .append("Compass.reset_configuration!\n")
                    .append("Compass.add_project_configuration configLocation\n")

                            // remove color codes since they cannot be proceeded correctly by IDE consoles
                    .append("Compass.configuration.color_output = false\n")

                    .append("Compass.configure_sass_plugin!\n")
                    .append("Compass.configuration.on_stylesheet_saved { |filename| callback_logger.onStylesheetSaved(filename) }\n")
                    .append("Compass.configuration.on_stylesheet_error { |filename, message| callback_logger.onStylesheetError(filename, message) }\n")
                    .append("Compass.configuration.on_sprite_saved { |filename| callback_logger.onSpriteSaved(filename) }\n")
                    .append("Dir.chdir(File.dirname(configLocation)) do\n")
                    .append("  Compass.sass_compiler.compile!\n")
                    .append("end\n");

            scriptingContainer.put("configLocation", this.configFile.getAbsolutePath().replaceAll("\\\\", "/"));
            scriptingContainer.put("callback_logger", callbackLogger);
            this.unit = scriptingContainer.parse(script.toString());
        }

        return this.unit;
    }

    public void compile() throws IOException, CompassCompilerException {
        this.compassNotifier.onCompilationStarted();
        this.getScript().run();
        this.compassNotifier.onCompilationEnded();
    }

}
