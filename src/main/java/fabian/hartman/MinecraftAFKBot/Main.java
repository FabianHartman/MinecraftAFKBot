package fabian.hartman.MinecraftAFKBot;

import org.apache.commons.cli.*;

import java.awt.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("help", false, "shows help message");
        options.addOption("logsdir", true, "specifies where to save the logs");
        options.addOption("config", true, "specifies the path to the config");
        options.addOption("refreshToken", "accountfile", true, "specifies the path to the refreshToken which is used to login to Microsoft");
        options.addOption("onlyCreateConfig", false, "shut down the bot after the config is created");

        CommandLineParser optionsParser = new DefaultParser();
        try {
            CommandLine cmd = optionsParser.parse(options, args);

            if (cmd.hasOption("help")) {
                new HelpFormatter().printHelp("MinecraftAFKBot", options);
                return;
            }

            new MinecraftAFKBot(cmd);

            if (cmd.hasOption("onlyCreateConfig"))
                return;

            MinecraftAFKBot.getInstance().startBot();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}