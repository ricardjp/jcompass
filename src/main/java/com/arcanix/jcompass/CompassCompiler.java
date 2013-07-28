package com.arcanix.jcompass;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public final class CompassCompiler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompassCompiler.class);

    private final File configFile;

    public CompassCompiler(File configFile) {
        if (configFile == null) {
            throw new NullPointerException("Configuration file cannot be null");
        }
        this.configFile = configFile;
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public void compile() throws IOException, CompassCompilerException {
        if (!this.configFile.exists()) {
            throw new FileNotFoundException("Configuration file does not exist");
        }

        LOGGER.info("Updating stylesheets...");

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

            CallbackLogger callbackLogger = new CallbackLogger(this.configFile);
            scriptingContainer.put("callback_logger", callbackLogger);
            scriptingContainer.runScriptlet(script.toString());
            LOGGER.info("Done updating stylesheets");

            if (callbackLogger.hasLoggedError()) {
                throw new CompassCompilerException("Compilation error occurred");
            }
        } finally {
            if (scriptingContainer != null) {
                scriptingContainer.terminate();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new CompassCompiler(new File(args[0])).compile();
    }

}
