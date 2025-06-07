package fabian.hartman.MinecraftAFKBot.utils.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

public class ByteTag extends Tag<Byte> {
    @Override
    protected ByteTag read(ByteArrayDataInputWrapper in) {
        setValue(in.readByte());
        return this;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }
}