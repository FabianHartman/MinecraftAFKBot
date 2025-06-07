package fabian.hartman.MinecraftAFKBot;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.CommandLine;
import fabian.hartman.MinecraftAFKBot.auth.AuthData;
import fabian.hartman.MinecraftAFKBot.auth.Authenticator;
import fabian.hartman.MinecraftAFKBot.bot.Player;
import fabian.hartman.MinecraftAFKBot.event.EventManager;
import fabian.hartman.MinecraftAFKBot.io.config.SettingsConfig;
import fabian.hartman.MinecraftAFKBot.io.logging.LogFormatter;
import fabian.hartman.MinecraftAFKBot.modules.ChatProxyModule;
import fabian.hartman.MinecraftAFKBot.modules.ClientDefaultsModule;
import fabian.hartman.MinecraftAFKBot.modules.HandshakeModule;
import fabian.hartman.MinecraftAFKBot.modules.LoginModule;
import fabian.hartman.MinecraftAFKBot.modules.ModuleManager;
import fabian.hartman.MinecraftAFKBot.modules.command.ChatCommandModule;
import fabian.hartman.MinecraftAFKBot.modules.command.CommandRegistry;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;
import fabian.hartman.MinecraftAFKBot.modules.timer.TimerModule;
import fabian.hartman.MinecraftAFKBot.network.mojangapi.MojangAPI;
import fabian.hartman.MinecraftAFKBot.network.mojangapi.Realm;
import fabian.hartman.MinecraftAFKBot.network.ping.ServerPinger;
import fabian.hartman.MinecraftAFKBot.network.protocol.NetworkHandler;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.utils.MinecraftTranslations;
import fabian.hartman.MinecraftAFKBot.utils.UUIDUtils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;

public class Bot {

    @Getter @Setter private boolean running;
    @Getter @Setter private boolean preventStartup;
    @Getter @Setter private boolean preventReconnect;
    @Getter         private final SettingsConfig config;
    @Getter @Setter private int serverProtocol = ProtocolConstants.MC_1_8; //default 1.8
    @Getter @Setter private String serverHost;
    @Getter @Setter private int serverPort;
    @Getter @Setter private AuthData authData;
    @Getter @Setter private boolean wontConnect = false;
    @Getter         private ExecutorService commandsThread;
    @Getter         private final boolean noGui;

    @Getter         private final EventManager eventManager;
    @Getter         private CommandRegistry commandRegistry;
    @Getter         private final ModuleManager moduleManager;

    @Getter         private Player player;

    @Getter         private Socket socket;
    @Getter         private NetworkHandler net;

    @Getter         private MinecraftTranslations minecraftTranslations;

    @Getter         private File logsFolder = new File(MinecraftAFKBot.getExecutionDirectory(), "logs");

    public Bot(CommandLine cmdLine) {
        MinecraftAFKBot.getInstance().setCurrentBot(this);
        this.eventManager = new EventManager();
        this.moduleManager = new ModuleManager();
        this.noGui = cmdLine.hasOption("nogui");

        if (cmdLine.hasOption("config"))
            this.config = new SettingsConfig(cmdLine.getOptionValue("config"));
        else
            this.config = new SettingsConfig(new File(MinecraftAFKBot.getExecutionDirectory(), "config.json").getAbsolutePath());

        // use command line arguments
        if (cmdLine.hasOption("logsdir")) {
            this.logsFolder = new File(cmdLine.getOptionValue("logsdir"));
            if (!logsFolder.exists()) {
                boolean success = logsFolder.mkdirs();
                if (!success) {
                    MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
                    MinecraftAFKBot.getInstance().getCurrentBot().setWontConnect(true);
                    MinecraftAFKBot.getInstance().getCurrentBot().setPreventStartup(true);
                    return;
                }
            }
        }

        // set logger file handler
        try {
            FileHandler fh;
            if(!logsFolder.exists() && !logsFolder.mkdir() && logsFolder.isDirectory())
                throw new IOException("Failed creating logs folder. Exiting.");
            MinecraftAFKBot.getLog().removeHandler(Arrays.stream(MinecraftAFKBot.getLog().getHandlers()).filter(handler -> handler instanceof FileHandler).findAny().orElse(null));
            MinecraftAFKBot.getLog().addHandler(fh = new FileHandler(logsFolder.getPath() + "/log%g.log", 0, Math.max(1, getConfig().getLogCount())));
            fh.setFormatter(new LogFormatter());
            fh.setEncoding("UTF-8");
        } catch (IOException e) {
            MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
            MinecraftAFKBot.getInstance().getCurrentBot().setWontConnect(true);
            return;
        }

        // start message
        MinecraftAFKBot.getLog().info("Using " + MinecraftAFKBot.TITLE);

        // init MinecraftTranslations
        this.minecraftTranslations = new MinecraftTranslations();

        // authenticate player if online-mode is set
        if (getConfig().isOnlineMode()) {
            boolean authSuccessful = authenticate();

            if (!authSuccessful) {
                setPreventStartup(true);
                return;
            }
        } else {
            this.authData = new AuthData(null, UUIDUtils.createOfflineUUIDString(getConfig().getUserName()), getConfig().getUserName());
        }

        String ip = getConfig().getServerIP();
        int port = getConfig().getServerPort();

        int assumedProtocolIdForMJAPI = ProtocolConstants.getProtocolId(getConfig().getDefaultProtocol());
        if (assumedProtocolIdForMJAPI == ProtocolConstants.AUTOMATIC)
            assumedProtocolIdForMJAPI = ProtocolConstants.getLatest();
        MojangAPI mojangAPI = null;
        if (getConfig().isOnlineMode())
            mojangAPI = new MojangAPI(getAuthData(), assumedProtocolIdForMJAPI);

        // Check rather to connect to realm
        if (getConfig().getRealmId() != -1 && mojangAPI != null) {
            if (getConfig().getRealmId() == 0) {
                List<Realm> possibleRealms = mojangAPI.getPossibleWorlds();
                mojangAPI.printRealms(possibleRealms);
                if (getConfig().getRealmId() == 0) {
                    setPreventStartup(true);
                    return;
                }
            }
            if (getConfig().isRealmAcceptTos())
                mojangAPI.agreeTos();
            else {
                if (!getConfig().isRealmAcceptTos()) {
                    System.out.println("************************************************************************************************\\n\\\n" +
                            "If you want to use realms you have to accept the tos in the Settings (realm-accept-tos)\\n\\\n" +
                            "************************************************************************************************");
                    setPreventStartup(true);
                    return;
                }
            }

            String ipAndPort = null;
            for (int i = 0; i < 5; i++) {
                ipAndPort = mojangAPI.getServerIP(getConfig().getRealmId());
                if (ipAndPort == null) {
                    System.out.println("Trying to resolve the server address... (Try "+ (i + 1) +")");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignore) { }
                } else
                    break;
            }
            if (ipAndPort == null) {
                setWontConnect(true);
                setRunning(false);
                setPreventReconnect(true);
                return;
            }
            ip = ipAndPort.split(":")[0];
            port = Integer.parseInt(ipAndPort.split(":")[1]);
        }

        // Ping server
        System.out.println(MessageFormat.format("Pinging {0}:{1} with protocol of MC-{2}", ip, String.valueOf(port), getConfig().getDefaultProtocol()));
        ServerPinger sp = new ServerPinger(ip, port);
        sp.ping();

        // Obtain keys for chat signing
        if (mojangAPI != null) {
            mojangAPI.obtainCertificates();
        }
    }

    public void start(CommandLine cmdLine) {
        if (isRunning() || isPreventStartup()) {
            MinecraftAFKBot.getInstance().setCurrentBot(null);
            return;
        }
        connect();
    }

    public void runCommand(String command, boolean executeBotCommand, CommandExecutor commandExecutor) {
        commandsThread.execute(() -> {
            if (getNet() == null)
                return;
            if (executeBotCommand && command.startsWith("/")) {
                boolean executed = MinecraftAFKBot.getInstance().getCurrentBot().getCommandRegistry().dispatchCommand(command, commandExecutor);
                if (executed)
                    return;
            }

            getPlayer().sendMessage(command, commandExecutor);
        });
    }

    private boolean authenticate() {
        Authenticator authenticator = new Authenticator();
        Optional<AuthData> authData = authenticator.authenticate();

        if (!authData.isPresent()) {
            setAuthData(new AuthData(null, UUIDUtils.createOfflineUUIDString(getConfig().getUserName()), getConfig().getUserName()));
            return false;
        }

        setAuthData(authData.get());

        return true;
    }

    private void registerCommands() {
        this.commandRegistry = new CommandRegistry();
        commandRegistry.registerBotCommands();
    }

    private void connect() {
        String serverName = getServerHost();
        int port = getServerPort();

        do {
            try {
                setRunning(true);
                if (isWontConnect()) {
                    setWontConnect(false);
                    ServerPinger sp = new ServerPinger(getServerHost(), getServerPort());
                    sp.ping();
                    if (isWontConnect()) {
                        if (!getConfig().isAutoReconnect())
                            return;
                        try {
                            Thread.sleep(getConfig().getAutoReconnectTime() * 1000L);
                        } catch (InterruptedException ignore) { }
                        continue;
                    }
                }
                this.socket = new Socket(serverName, port);

                this.net = new NetworkHandler();
                this.commandsThread = Executors.newSingleThreadExecutor(
                        new ThreadFactoryBuilder().setNameFormat("command-executor-thread-%d").build());

                registerCommands();

                // enable required modules
                getModuleManager().enableModule(new HandshakeModule(serverName, port));
                getModuleManager().enableModule(new LoginModule(getAuthData().getUsername()));
                getModuleManager().enableModule(new ClientDefaultsModule());
                getModuleManager().enableModule(new ChatProxyModule());

                if (getConfig().isStartTextEnabled())
                    getModuleManager().enableModule(new ChatCommandModule());

                if (getConfig().isTimerEnabled())
                    getModuleManager().enableModule(new TimerModule());

                // init player

                this.player = new Player();

                // add shutdown hook

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        if (socket != null && !socket.isClosed())
                            socket.close();
                        if (commandsThread != null && !commandsThread.isTerminated())
                            commandsThread.shutdownNow();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

                // game loop (for receiving packets)

                while (running) {
                    try {
                        net.readData();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        System.out.println("Could not receive packet! Shutting down...");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(MessageFormat.format("An unexpected error occured: {0}\\n\\\n" +
                        "The bot will be stopped...", e.getMessage()));
            } finally {
                try {
                    if (socket != null)
                        this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (getPlayer() != null)
                    getEventManager().unregisterListener(getPlayer());
                if (commandsThread != null && !commandsThread.isShutdown())
                    commandsThread.shutdownNow();
                getEventManager().getRegisteredListener().clear();
                getEventManager().getClassToInstanceMapping().clear();
                getModuleManager().disableAll();
                this.socket = null;
                this.net = null;
                this.player = null;
            }
            if (getConfig().isAutoReconnect() && !isPreventReconnect()) {
                System.out.println(MessageFormat.format("AFKBot will reconnect in {0} seconds...", String.valueOf(getConfig().getAutoReconnectTime())));

                try {
                    Thread.sleep(getConfig().getAutoReconnectTime() * 1000L);
                } catch (InterruptedException ignore) { }

                if (getAuthData() == null) {
                    if (getConfig().isOnlineMode())
                        authenticate();
                    else {
                        System.out.println(MessageFormat.format("Starting in offline-mode with user name: {0}", getConfig().getUserName()));
                        authData = new AuthData(null, UUIDUtils.createOfflineUUIDString(getConfig().getUserName()), getConfig().getUserName());
                    }
                }
            }
        } while (getConfig().isAutoReconnect() && !isPreventReconnect());
        MinecraftAFKBot.getInstance().setCurrentBot(null);
    }
}