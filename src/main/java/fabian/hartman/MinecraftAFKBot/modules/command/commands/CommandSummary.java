package fabian.hartman.MinecraftAFKBot.modules.command.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.bot.loot.LootHistory;
import fabian.hartman.MinecraftAFKBot.bot.loot.LootItem;
import fabian.hartman.MinecraftAFKBot.modules.command.BrigardierCommand;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.CommandExecutor;

import java.text.MessageFormat;
import java.util.Comparator;

public class CommandSummary extends BrigardierCommand {
    public CommandSummary() {
        super("summary", "Prints a summary of the caught items", "summarize", "stats", "statistics", "caught", "loot");
    }

    @Override
    public void register(LiteralArgumentBuilder<CommandExecutor> builder) {
        builder.then(literal("clear")
                        .executes(getExecutor(true)))
                .executes(getExecutor(false));
    }

    private Command<CommandExecutor> getExecutor(boolean clearAfterwards) {
        return context -> {
            CommandExecutor source = context.getSource();
            if (MinecraftAFKBot.getInstance().getCurrentBot() == null)
                return 0;
            if (MinecraftAFKBot.getInstance().getCurrentBot().getFishingModule() == null)
                return 0;

            LootHistory lootHistory = MinecraftAFKBot.getInstance().getCurrentBot().getFishingModule().getLootHistory();
            if (lootHistory.getItems().isEmpty()) {
                source.sendTranslatedMessages("command-summary-empty");
                return 0;
            }
            source.sendTranslatedMessages("ui-tabs-loot", lootHistory.getItems().stream().mapToInt(LootItem::getCount).sum());
            lootHistory.getItems().stream().sorted(Comparator.comparingInt(LootItem::getCount).reversed()).forEach(lootItem -> {
                source.sendMessage(lootItem.getCount() + "x " + lootItem.getDisplayName());
            });

            if (MinecraftAFKBot.getInstance().getCurrentBot().getDiscordModule() != null)
                MinecraftAFKBot.getInstance().getCurrentBot().getDiscordModule().sendSummary(lootHistory);

            if (clearAfterwards)
                lootHistory.getItems().clear();
            return 0;
        };
    }

    @Override
    public String getSyntax(String label) {
        return MessageFormat.format("/{0} [clear]", label);
    }
}