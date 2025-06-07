package fabian.hartman.MinecraftAFKBot.utils.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.nio.charset.StandardCharsets;

public class StringTag extends Tag<String> {
    @Override
    protected StringTag read(ByteArrayDataInputWrapper in) {
        int stringLength = in.readUnsignedShort();
        byte[] stringData = new byte[stringLength];
        in.readBytes(stringData);
        setValue(new String(stringData, StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }
}