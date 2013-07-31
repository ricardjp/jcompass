package com.arcanix.jcompass;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import java.io.*;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class CompassCompiler {

    private final File configFile;
    private final CompassNotifier compassNotifier;

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
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public CompassNotifier getCompassNotifier() {
        return this.compassNotifier;
    }

    public void compile() throws IOException, CompassCompilerException {
        if (!this.configFile.exists()) {
            throw new FileNotFoundException("Configuration file does not exist");
        }

        this.compassNotifier.onCompilationStarted();

        ScriptingContainer scriptingContainer = null;
        try {
            scriptingContainer = new ScriptingContainer(LocalContextScope.THREADSAFE);

            StringWriter writer = new StringWriter();
            scriptingContainer.setOutput(new PrintStream(new NullOutputStream()));

            StringBuilder script = new StringBuilder()
                .append("require 'rubygems'\n")
                .append("require 'compass'\n")
                .append("frameworks = Dir.new(Compass::Frameworks::DEFAULT_FRAMEWORKS_PATH).path\n")
                .append("Compass::Frameworks.register_directory(File.join(frameworks, 'compass'))\n")
                .append("Compass::Frameworks.register_directory(File.join(frameworks, 'blueprint'))\n")
                .append("Compass.add_project_configuration configLocation\n")

                // remove color codes since they cannot be proceeded correctly by IDE consoles
                .append("Compass.configuration.color_output = false\n")

                .append("Compass.configure_sass_plugin!\n")
                .append("Compass.configuration.on_stylesheet_saved { |filename| callback_logger.onStylesheetSaved(filename) }\n")
                .append("Compass.configuration.on_stylesheet_error { |filename, message| callback_logger.onStylesheetError(filename, message) }\n")
                .append("Compass.configuration.on_sprite_saved { |filename| callback_logger.onSpriteSaved(filename) }\n")
                .append("Dir.chdir(File.dirname(configLocation)) do\n")
                .append("  Compass.compiler.run\n")
                .append("end\n");

            // setting configLocation variable value
            scriptingContainer.put("configLocation", this.configFile.getAbsolutePath().replaceAll("\\\\", "/"));

            CallbackLogger callbackLogger = new CallbackLogger(this.configFile, this.compassNotifier);
            scriptingContainer.put("callback_logger", callbackLogger);
            scriptingContainer.runScriptlet(script.toString());

            this.compassNotifier.onCompilationEnded();

            if (callbackLogger.hasLoggedError()) {
                throw new CompassCompilerException("Compilation error occurred");
            }
        } finally {
            if (scriptingContainer != null) {
                scriptingContainer.terminate();
            }
        }
    }

}
