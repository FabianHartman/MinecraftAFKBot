package fabian.hartman.MinecraftAFKBot.bot;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import fabian.hartman.MinecraftAFKBot.event.play.*;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.*;
import fabian.hartman.MinecraftAFKBot.utils.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.EventHandler;
import fabian.hartman.MinecraftAFKBot.event.Listener;
import fabian.hartman.MinecraftAFKBot.event.custom.RespawnEvent;
import fabian.hartman.MinecraftAFKBot.modules.command.brigardier.argument.MessageArgumentType;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.ConsoleCommandExecutor;
import fabian.hartman.MinecraftAFKBot.network.protocol.ProtocolConstants;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutEntityAction.EntityAction;
import fabian.hartman.MinecraftAFKBot.network.utils.CryptManager;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
@Setter
public class Player implements Listener {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private float originYaw = -255;
    private float originPitch = -255;

    private int experience;
    private int levels;
    private float health = -1;
    private boolean sentLowHealth;
    private boolean respawning;
    private boolean sneaking;

    private int heldSlot;
    @Setter(AccessLevel.NONE)
    private Optional<CryptManager.MessageSignature> lastUsedSignature = Optional.empty();
    private int chatSessionIndex = 0;
    private CommandDispatcher<CommandExecutor> mcCommandDispatcher;

    private UUID uuid;

    private int entityID = -1;

    private Thread lookThread;

    public Player() {
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
    }

    @EventHandler
    public void onJoinGame(JoinGameEvent event) {
        setEntityID(event.getEid());
        respawn();
    }

    @EventHandler
    public void onUpdateHealth(UpdateHealthEvent event) {
        if (event.getEid() != getEntityID())
            return;

        if (getHealth() != -1 && event.getHealth() <= 0 && getEntityID() != -1 && !isRespawning()) {
            setRespawning(true);
            MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().callEvent(new RespawnEvent());
            this.sneaking = false;
            respawn();
        } else if (event.getHealth() > 0 && isRespawning())
            setRespawning(false);

        if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isAutoCommandBeforeDeathEnabled()) {
            if (event.getHealth() < getHealth() && event.getHealth() <= MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getMinHealthBeforeDeath() && !isSentLowHealth()) {
                for (String command : MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getAutoCommandBeforeDeath()) {
                    MinecraftAFKBot.getInstance().getCurrentBot().runCommand(command, true, new ConsoleCommandExecutor());
                }
                setSentLowHealth(true);
            } else if (isSentLowHealth() && event.getHealth() > MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getMinHealthBeforeDeath())
                setSentLowHealth(false);
        }

        if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isAutoQuitBeforeDeathEnabled() && event.getHealth() < getHealth()
                && event.getHealth() <= MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getMinHealthBeforeQuit() && event.getHealth() != 0.0) {
            System.out.println("Health threshold before quit reached. Bot will be stopped.");
            MinecraftAFKBot.getInstance().getCurrentBot().setPreventReconnect(true);
            MinecraftAFKBot.getInstance().getCurrentBot().setRunning(false);
        }

        this.health = event.getHealth();
    }

    @EventHandler
    public void onRespawn(RespawnEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getAutoCommandOnRespawnDelay());
            } catch (InterruptedException ignore) { }
            if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isAutoCommandOnRespawnEnabled()) {
                for (String command : MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getAutoCommandOnRespawn()) {
                    MinecraftAFKBot.getInstance().getCurrentBot().runCommand(command, true, new ConsoleCommandExecutor());
                }
            }
        }).start();
    }

    @EventHandler
    public void onCommandsRegistered(CommandsRegisteredEvent event) {
        setMcCommandDispatcher(event.getCommandDispatcher());
    }

    public void respawn() {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutClientStatus(PacketOutClientStatus.Action.PERFORM_RESPAWN));

        if (MinecraftAFKBot.getInstance().getCurrentBot().getConfig().isAutoSneak()) {
            MinecraftAFKBot.getScheduler().schedule(() -> {
                MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutEntityAction(EntityAction.START_SNEAKING));
                this.sneaking = true;
            }, 250, TimeUnit.MILLISECONDS);
        }
    }

    public void sendMessage(String message, CommandExecutor commandExecutor) {
        message = message.replace("%prefix%", MinecraftAFKBot.PREFIX);
        for (String line : message.split("\n")) {
            if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() == ProtocolConstants.MC_1_8) {
                for (String split : StringUtils.splitDescription(line)) {
                    MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(split));
                }
            } else if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() < ProtocolConstants.MC_1_19) {
                MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(line));
            } else {
                if (line.startsWith("/"))
                    executeChatCommand(line.substring(1), commandExecutor);
                else
                    MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatMessage(line));
            }
        }
    }

    private void executeChatCommand(String command, CommandExecutor commandExecutor) {
        if (mcCommandDispatcher == null) {
            if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MC_1_20_5)
                MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUnsignedChatCommand(command));
            else
                MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatCommand(command));
            return;
        }

        CommandContextBuilder<CommandExecutor> context = mcCommandDispatcher.parse(command, commandExecutor).getContext();
        Map<String, Pair<ArgumentType<?>, ParsedArgument<CommandExecutor, ?>>> arguments = CommandUtils.getArguments(context);
        boolean containsSignableArguments = arguments.values().stream().anyMatch(argument -> argument.getKey() instanceof MessageArgumentType);
        if (!containsSignableArguments) {
            if (MinecraftAFKBot.getInstance().getCurrentBot().getServerProtocol() >= ProtocolConstants.MC_1_20_5)
                MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUnsignedChatCommand(command));
            else
                MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatCommand(command));
            return;
        }
        List<CryptManager.SignableArgument> signableArguments = arguments.entrySet().stream()
                .filter(entry -> entry.getValue().getKey() instanceof MessageArgumentType)
                .map(entry -> new CryptManager.SignableArgument(entry.getKey(), entry.getValue().getValue().getResult().toString()))
                .collect(Collectors.toList());
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutChatCommand(command, signableArguments));
    }

    public void use() {
        MinecraftAFKBot.getInstance().getCurrentBot().getNet().sendPacket(new PacketOutUseItem(this));
    }

    public int incrementChatSessionIndex() {
        return this.chatSessionIndex++;
    }
}