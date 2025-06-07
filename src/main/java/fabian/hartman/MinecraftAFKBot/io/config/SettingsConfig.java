package fabian.hartman.MinecraftAFKBot.io.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import fabian.hartman.MinecraftAFKBot.modules.timer.Timer;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.utils.LocationUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@ToString
public class SettingsConfig implements Config {
    @Property(key = "server.ip", description = "config-server-ip") private String serverIP = "127.0.0.1";
    @Property(key = "server.port", description = "config-server-port") private int serverPort = 25565;
    @Property(key = "server.realm-id", description = "config-server-realm-id") @Setter private long realmId = -1;
    @Property(key = "server.realm-accept-tos", description = "config-server-realm-accept-tos") @Setter private boolean realmAcceptTos = false;
    @Property(key = "server.online-mode", description = "config-server-online-mode") private final boolean onlineMode = true;
    @Property(key = "server.default-protocol", description = "config-server-default-protocol") private final String defaultProtocol = ProtocolConstants.getVersionString(ProtocolConstants.AUTOMATIC);
    @Property(key = "server.spoof-forge", description = "config-server-spoof-forge") private final boolean spoofForge = false;

    @Property(key = "auto.auto-reconnect", description = "config-auto-auto-reconnect") private final boolean autoReconnect = true;
    @Property(key = "auto.auto-reconnect-time", description = "config-auto-auto-reconnect-time") private final int autoReconnectTime = 5;
    @Property(key = "auto.auto-disconnect", description = "config-auto-auto-disconnect") private final boolean autoDisconnect = false;
    @Property(key = "auto.auto-sneak", description = "config-auto-auto-sneak") private final boolean autoSneak = false;
    @Property(key = "auto.auto-disconnect-players-threshold", description = "config-auto-auto-disconnect-players-threshold") private final int autoDisconnectPlayersThreshold = 5;

    @Property(key = "auto.auto-command-on-respawn.enabled", description = "config-auto-auto-command-on-respawn") private final boolean autoCommandOnRespawnEnabled = false;
    @Property(key = "auto.auto-command-on-respawn.delay", description = "config-auto-auto-command-on-respawn-delay") private final long autoCommandOnRespawnDelay = 1000;
    @Property(key = "auto.auto-command-on-respawn.commands", description = "config-auto-auto-command-on-respawn-commands") private final List<String> autoCommandOnRespawn = Arrays.asList("%prefix%I respawned", "/home fishing");

    @Property(key = "auto.auto-command-before-death.enabled", description = "config-auto-auto-command-before-death") private final boolean autoCommandBeforeDeathEnabled = false;
    @Property(key = "auto.auto-command-before-death.commands", description = "config-auto-auto-command-before-death-commands") private final List<String> autoCommandBeforeDeath = Arrays.asList("%prefix%I am about to die", "/home");
    @Property(key = "auto.auto-command-before-death.min-health-before-death", description = "config-auto-auto-command-before-death-min-health-before-death") private final float minHealthBeforeDeath = 6.0F;

    @Property(key = "auto.auto-quit-before-death.enabled", description = "config-auto-auto-quit-before-death") private final boolean autoQuitBeforeDeathEnabled = false;
    @Property(key = "auto.auto-quit-before-death.min-health-before-quit", description = "config-auto-auto-quit-before-death-min-health-before-quit") private final float minHealthBeforeQuit = 6.0F;
    @Property(key = "auto.auto-eject.enabled", description = "config-auto-auto-eject") private final boolean autoLootEjectionEnabled = false;

    @Property(key = "auto.timer.enabled", description = "config-auto-timer") private final boolean timerEnabled = false;
    @Property(key = "auto.timer.timers", description = "config-auto-timers") private final List<Timer> timers = Collections.singletonList(new Timer("test", 5, TimeUnit.MINUTES, Collections.singletonList("Every five minutes")));

    @Property(key = "account.mail", description = "config-account-mail") private final String userName = "AFKBot";

    @Property(key = "logs.log-count", description = "config-logs-log-count") private final int logCount = 15;
    @Property(key = "logs.log-packets", description = "config-logs-log-packets") private final boolean logPackets = false;
    @Property(key = "logs.log-entity-data", description = "config-logs-log-entity-data") private final boolean logEntityData = false;
    @Property(key = "logs.log-item-data", description = "config-logs-log-item-data") private final boolean logItemData = false;

    @Property(key = "announces.discord.enabled", description = "config-announces-discord") private final boolean webHookEnabled = false;
    @Property(key = "announces.discord.web-hook", description = "config-announces-discord-web-hook") private final String webHook = "YOURWEBHOOK";
    @Property(key = "announces.discord.alert-on-attack", description = "config-announces-discord-alert-on-attack") private final boolean alertOnAttack = true;
    @Property(key = "announces.discord.alert-on-respawn", description = "config-announces-discord-alert-on-respawn") private final boolean alertOnRespawn = true;
    @Property(key = "announces.discord.alert-on-level-update", description = "config-announces-discord-alert-on-level-update") private final boolean alertOnLevelUpdate = true;
    @Property(key = "announces.discord.ping-on-enchantment.enabled", description = "config-announces-discord-ping-on-enchantment") private final boolean pingOnEnchantmentEnabled = false;
    @Property(key = "announces.discord.ping-on-enchantment.mention", description = "config-announces-discord-ping-on-enchantment-mention") private final String pingOnEnchantmentMention = "<@USER_ID>";
    @Property(key = "announces.discord.ping-on-enchantment.items", description = "config-announces-discord-ping-on-enchantment-items") private final List<String> pingOnEnchantmentItems = Collections.singletonList("enchanted_book");
    @Property(key = "announces.discord.ping-on-enchantment.enchantments", description = "config-announces-discord-ping-on-enchantment-enchantments") private final List<String> pingOnEnchantmentEnchantments = Arrays.asList("mending","unbreaking");

    @Property(key = "announces.announce-lvl-up", description = "config-announces-announce-lvl-up") private final boolean announceLvlUp = true;
    @Property(key = "announces.announce-lvl-up-text", description = "config-announces-announce-lvl-up-text") private final String announceLvlUpText = "I am level %lvl% now";

    @Property(key = "start-text.enabled", description = "config-start-text-enabled") private final boolean startTextEnabled = true;
    @Property(key = "start-text.text", description = "config-start-text-text") private final List<String> startText = Arrays.asList("%prefix%Starting fishing", "/trigger Bot");

    @Property(key = "misc.stucking-fix-enabled", description = "config-misc-stucking-fix-enabled") private final boolean stuckingFixEnabled = true;
    @Property(key = "misc.prevent-rod-breaking", description = "config-misc-prevent-rod-breaking") private final boolean preventRodBreaking = true;
    @Property(key = "misc.disable-rod-checking", description = "config-misc-disable-rod-checking") private final boolean disableRodChecking = false;
    @Property(key = "misc.gui-console-max-lines", description = "config-misc-gui-console-max-lines") private final int guiConsoleMaxLines = 1000;
    @Property(key = "misc.look-speed", description = "config-misc-look-speed") private final int lookSpeed = 16;
    @Property(key = "misc.wiki", description = "") private final String readme = "https://github.com/MrKinau/FishingBot/wiki/config";

    @Getter private final String path;

    public SettingsConfig(String path) {
        this.path = path;
        init(path);

        if (serverIP.contains(":")) {
            serverPort = Integer.parseInt(serverIP.split(":")[1]);
            serverIP = serverIP.split(":")[0];
        }
    }
}