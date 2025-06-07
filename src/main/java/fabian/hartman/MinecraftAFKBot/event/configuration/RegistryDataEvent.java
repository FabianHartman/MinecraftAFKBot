package fabian.hartman.MinecraftAFKBot.event.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import fabian.hartman.MinecraftAFKBot.event.Event;
import fabian.hartman.MinecraftAFKBot.utils.nbt.NBTTag;

import java.util.SortedMap;

@Getter
@RequiredArgsConstructor
public class RegistryDataEvent extends Event {
    private final String registryId;
    private final SortedMap<String, @Nullable NBTTag> registryData;
}