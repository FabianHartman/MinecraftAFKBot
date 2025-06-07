package fabian.hartman.MinecraftAFKBot.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import fabian.hartman.MinecraftAFKBot.network.protocol.play.PacketOutBlockPlace;

@Data
@AllArgsConstructor
public class MovingObjectPositionBlock {
    private long blockPos;
    private PacketOutBlockPlace.BlockFace direction;
    private float dx;
    private float dy;
    private float dz;
    private boolean inside;
    private boolean worldBorderHit;
}