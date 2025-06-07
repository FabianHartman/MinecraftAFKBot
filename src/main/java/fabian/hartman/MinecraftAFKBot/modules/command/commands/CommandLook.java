package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.text.MessageFormat;

public class CommandLook extends BrigardierCommand {
    public CommandLook() {
        super("look", "Makes the bot look elsewhere");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(argument("yaw", FloatArgumentType.floatArg(-180.0f, 180.0f))
                        .then(argument("pitch", FloatArgumentType.floatArg(-180.0f, 180.0f))
                                .then(argument("speed", IntegerArgumentType.integer(1))
                                        .executes(getExecutor()))
                                .executes(getExecutor()))
                        .executes(context -> {
                            context.getSource().sendMessage(getSyntax(context));
                            return 0;
                        }))
                .executes(context -> {
                    context.getSource().sendMessage(getSyntax(context));
                    return 0;
                });
    }

    private Command<CommandExecutor> getExecutor() {
        return context -> {
            CommandExecutor source = context.getSource();
            float yaw = context.getArgument("yaw", Float.class);
            float pitch = context.getArgument("pitch", Float.class);
            int speed;
            try {
                speed = context.getArgument("speed", Integer.class);
            } catch (IllegalArgumentException ex) {
                speed = MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getLookSpeed();
            }

            MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().look(yaw, pitch, speed, finished -> {
                source.sendTranslatedMessages("command-look-executed", yaw, pitch);
            });
            MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().setOriginYaw(yaw);
            MinecraftAFKBot.getInstance().getCurrentBot().getPlayer().setOriginPitch(pitch);
            return 0;
        };
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/{0} <Yaw> <Pitch> [Speed]", label);
    }
}
