package fabian.hartman.MinecraftAFKBot.modules;

import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.event.EventHandler;
import fabian.hartman.MinecraftAFKBot.event.Listener;
import fabian.hartman.MinecraftAFKBot.event.play.ChatEvent;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.ConsoleCommandExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

public class ChatProxyModule extends Module implements Listener {
    private Thread chatThread;
    private BufferedReader scanner;

    @Override
    public void onEnable() {
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().registerListener(this);
        chatThread = new Thread(() -> {
            scanner = new BufferedReader(new InputStreamReader(System.in));
            String line;
            try {
                while (!chatThread.isInterrupted()) {
                    while (!scanner.ready() && !chatThread.isInterrupted()) {
                        Thread.sleep(100);
                    }
                    line = scanner.readLine();
                    if (MinecraftAFKBot.getInstance().getCurrentBot() == null) return;
                    MinecraftAFKBot.getInstance().getCurrentBot().runCommand(line, true, new ConsoleCommandExecutor());
                }
            } catch (IOException | InterruptedException ignored) { }
        });
        chatThread.setName("chatThread");
        chatThread.start();
    }

    @Override
    public void onDisable() {
        chatThread.interrupt();
        MinecraftAFKBot.getInstance().getCurrentBot().getEventManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (isEnabled() && !"".equals(event.getText())) {
            System.out.println(MessageFormat.format("[CHAT] {0}", event.getText()));
        }
    }
}
