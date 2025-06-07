package fabian.hartman.MinecraftAFKBot.modules.ejection;

import lombok.AllArgsConstructor;
import lombok.Data;
import fabian.hartman.MinecraftAFKBot.utils.LocationUtils;

@Data
@AllArgsConstructor
public class ChestEjectFunction {
    private LocationUtils.Direction direction;
    private short slot;
}