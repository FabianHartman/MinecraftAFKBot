package fabian.hartman.MinecraftAFKBot.utils.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

public class LongTag extends Tag<Long> {
    @Override
    protected LongTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readLong());
        return this;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }
}