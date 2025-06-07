package fabian.hartman.MinecraftAFKBot.utils.reflect;

public interface MethodAccessor {
    <T> T invoke(Object instance, Object... args);
}