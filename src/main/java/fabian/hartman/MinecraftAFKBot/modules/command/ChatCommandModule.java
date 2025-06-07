package fabian.hartman.MinecraftAFKBot.modules.command;

import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.EventHandler;
import fabian.hartman.MinecraftAFKBot.event.Listener;
import fabian.hartman.MinecraftAFKBot.event.play.ChatEvent;
import fabian.hartman.MinecraftAFKBot.modules.Module;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.PlayerCommandExecutor;

public class ChatCommandModule extends Module implements Listener {
    @Override
    public void onEnable() {
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
    }

    @Override
    public void onDisable() {
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!isEnabled())
            return;
        String userName = MinecraftAFKBot.getInstance().getCurrentBot().getAuthData().getUsername();
        if (event.getText().contains(userName) && event.getText().contains(",")) {
            String[] parts = event.getText().split(",");
            if (parts.length <= 1)
                return;

            StringBuilder cmdBuilder = new StringBuilder(parts[1]);
            for (int i = 2; i < parts.length; i++)
                cmdBuilder.append(parts[i]);

            String fullCommand = cmdBuilder.toString().trim();
            if (Character.isWhitespace(fullCommand.charAt(0)))
                fullCommand = fullCommand.substring(1);

            MinecraftAFKBot.getInstance().getCurrentBot().getCommandRegistry().dispatchCommand(fullCommand, new PlayerCommandExecutor());
        }
    }
}