package fabian.hartman.MinecraftAFKBot.modules;

import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;

import java.text.MessageFormat;

public abstract class Module {

    private boolean enabled = false;

    public void enable() {
        this.enabled = true;
        onEnable();

        System.out.println(MessageFormat.format("Module \"{0}\" enabled!", getClass().getSimpleName()));
    }

    public void disable() {
        this.enabled = false;
        onDisable();

        System.out.println(MessageFormat.format("Module \"{0}\" disabled!", getClass().getSimpleName()));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public abstract void onEnable();

    public abstract void onDisable();
}