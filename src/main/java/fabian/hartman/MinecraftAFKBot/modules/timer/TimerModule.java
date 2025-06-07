package fabian.hartman.MinecraftAFKBot.modules.timer;

import fabian.hartman.MinecraftAFKBot.Bot;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;
import fabian.hartman.MinecraftAFKBot.modules.Module;
import fabian.hartman.MinecraftAFKBot.modules.command.executor.ConsoleCommandExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class TimerModule extends Module {
    private List<Timer> enabledTimers;
    private List<ScheduledFuture<?>> runningTimers;

    @Override
    public void onEnable() {
        this.enabledTimers = MinecraftAFKBot.getInstance().getCurrentBot().getConfig().getTimers();
        this.runningTimers = new ArrayList<>();
        startTimers();
    }

    @Override
    public void onDisable() {
        runningTimers.forEach(scheduledFuture -> scheduledFuture.cancel(true));
    }

    private void startTimers() {
        enabledTimers.forEach(timer -> {
            runningTimers.add(MinecraftAFKBot.getScheduler().scheduleAtFixedRate(() -> {
                for (String command : timer.getCommands()) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Bot bot = MinecraftAFKBot.getInstance().getCurrentBot();
                    if (bot == null)
                        continue;
                    bot.runCommand(command, true, new ConsoleCommandExecutor());
                }
            }, timer.getUnits(), timer.getUnits(), timer.getTimeUnit()));
        });
    }
}