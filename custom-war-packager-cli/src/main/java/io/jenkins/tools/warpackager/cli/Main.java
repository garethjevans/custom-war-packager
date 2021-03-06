package io.jenkins.tools.warpackager.cli;

import java.io.File;
import java.io.IOException;

import io.jenkins.tools.warpackager.lib.config.Config;
import io.jenkins.tools.warpackager.lib.impl.Builder;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        CliOptions options = new CliOptions();
        CmdLineParser p = new CmdLineParser(options);
        if (args.length == 0) {
            System.out.println("Usage: java -jar war-packager-cli.jar -configPath=mywar.yml [-version=1.0-SNAPSHOT] [-tmpDir=tmp]\n");
            p.printUsage(System.out);
            return;
        }

        try {
            p.parseArgument(args);
        } catch (CmdLineException ex) {
            p.printUsage(System.out);
            throw new IOException("Failed to read command-line arguments", ex);
        }



        final Config cfg;
        if (options.isDemo()) {
            System.out.println("Running build in the demo mode");
            cfg = Config.loadDemoConfig();
        } else {
            final File configPath = options.getConfigPath();
            if (configPath == null) {
                throw new IOException("-configPath or -demo must be defined");
            }
            cfg = Config.loadConfig(configPath);
        }

        // Override Build Settings by CLI arguments
        cfg.buildSettings.setTmpDir(options.getTmpDir());
        cfg.buildSettings.setVersion(options.getVersion());

        final Builder bldr = new Builder(cfg);
        bldr.build();
    }
}