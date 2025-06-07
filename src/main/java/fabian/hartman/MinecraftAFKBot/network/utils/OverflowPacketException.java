package fabian.hartman.MinecraftAFKBot.network.utils;

public class OverflowPacketException extends RuntimeException {
    public OverflowPacketException(String message) {
        super(message);
    }
}