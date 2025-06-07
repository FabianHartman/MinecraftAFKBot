package fabian.hartman.MinecraftAFKBot;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.CommandLine;
import fabian.hartman.MinecraftAFKBot.event.custom.BotStartEvent;
import fabian.hartman.MinecraftAFKBot.event.custom.BotStopEvent;
import fabian.hartman.MinecraftAFKBot.io.config.SettingsConfig;
import fabian.hartman.MinecraftAFKBot.io.logging.CustomPrintStream;
import fabian.hartman.MinecraftAFKBot.io.logging.LogFormatter;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinecraftAFKBot {
    public static String PREFIX;
    public static String TITLE;
    @Getter private static MinecraftAFKBot instance;
    @Getter public static Logger log = Logger.getLogger(Bot.class.getSimpleName());
    @Getter private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Getter private SettingsConfig config;
    @Getter private File refreshTokenFile;
    private CommandLine cmdLine;

    @Getter @Setter private Bot currentBot;

    public MinecraftAFKBot(CommandLine cmdLine) {
        instance = this;
        this.cmdLine = cmdLine;

        try {
            final Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("afkbot.properties"));
            PREFIX = properties.getProperty("name") + " v" + properties.getProperty("version") + " - ";
        } catch (Exception ex) {
            PREFIX = "AFKBot - ";
            ex.printStackTrace();
        }
        TITLE = PREFIX.substring(0, PREFIX.length() - 3);

        // initialize Logger
        log.setLevel(Level.ALL);
        ConsoleHandler ch;
        log.addHandler(ch = new ConsoleHandler());
        log.setUseParentHandlers(false);
        LogFormatter formatter = new LogFormatter();
        ch.setFormatter(formatter);
        try {
            ch.setEncoding("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        CustomPrintStream.enableForPackage("fabian.hartman.MinecraftAFKBot", getLog());

        if (cmdLine.hasOption("config"))
            this.config = new SettingsConfig(cmdLine.getOptionValue("config"));
        else
            this.config = new SettingsConfig(new File(MinecraftAFKBot.getExecutionDirectory(), "config.json").getAbsolutePath());

        // refresh token file
        if (cmdLine.hasOption("refreshToken")) {
            this.refreshTokenFile = new File(cmdLine.getOptionValue("refreshToken"));
            File refreshTokenDir = refreshTokenFile.getParentFile();
            if (refreshTokenDir != null && !refreshTokenDir.exists()) {
                refreshTokenDir.mkdirs();
            }
        } else {
            this.refreshTokenFile = new File(MinecraftAFKBot.getExecutionDirectory(), "refreshToken");
        }
    }

    public void startBot() {
        if (getCurrentBot() != null)
            stopBot(true);
        Thread.currentThread().setName("mainThread");
        Bot bot = new Bot(cmdLine);
        bot.getEventManager().callEvent(new BotStartEvent());
        bot.start(cmdLine);
    }

    public void stopBot(boolean preventReconnect) {
        if (getCurrentBot() == null) {
            return;
        }

        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new BotStopEvent());
        getCurrentBot().setPreventReconnect(preventReconnect);
        getCurrentBot().setRunning(false);
        getCurrentBot().setPreventStartup(true);
        MinecraftAFKBot.getInstance().interruptMainThread();
    }

    public void interruptMainThread() {
        Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().equals("mainThread"))
                .forEach(Thread::interrupt);
    }

    public static File getExecutionDirectory() {
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        if (jarFile.getParentFile() == null)
            return new File("");
        return jarFile.getParentFile();
    }
}