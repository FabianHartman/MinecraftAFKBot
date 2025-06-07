package fabian.hartman.MinecraftAFKBot.network.utils;

public class InvalidPacketException extends RuntimeException {

    public InvalidPacketException(String msg) {
        super(msg);
    }
}