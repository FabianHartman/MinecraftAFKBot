package fabian.hartman.MinecraftAFKBot.utils;

import lombok.Getter;

public class LocationUtils {
    @Getter
    public enum Direction {
        NORTH(180.0F, "North"),
        EAST(-90.0F, "East"),
        SOUTH(0.0F, "South"),
        WEST(90.0F, "West"),
        DOWN(Float.MIN_VALUE, 90.0F, "Down");

        private float yaw = Float.MIN_VALUE;
        private float pitch = Float.MIN_VALUE;
        private final String displayName;

        Direction(float yaw, float pitch, String displayName) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.displayName = displayName;
        }

        Direction(float yaw, String displayName) {
            this.yaw = yaw;
            this.displayName = displayName;
        }
    }
}