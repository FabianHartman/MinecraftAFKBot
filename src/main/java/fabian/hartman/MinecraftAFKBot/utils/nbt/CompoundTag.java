package fabian.hartman.MinecraftAFKBot.utils.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class CompoundTag extends Tag<Map<String, ? extends Tag<?>>> {
    @Override
    protected CompoundTag read(ByteArrayDataInputWrapper in) {
        Map<String, Tag<?>> value = new HashMap<>();
        try {
            while (true) {
                byte type = in.readByte();
                if (type == TagRegistry.TAG_END_ID) {
                    break;
                } else {
                    Tag<?> tag = readNextNamedTag(in, type);
                    value.put(tag.getName(), tag);
                }
            }
        } finally {
            setValue(value);
        }
        return this;
    }

    @Override
    public String toString(int tabs) {
        StringBuilder sb = new StringBuilder(addTabs(tabs) + getClass().getSimpleName() + " (" + Optional.ofNullable(getName()).orElse("") + "): ");
        for (Tag<?> value : getValue().values()) {
            sb.append("\n").append(value.toString(tabs + 1));
        }
        sb.append("\n").append(TagRegistry.createTag(EndTag.class).toString(tabs));
        return sb.toString();
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        getValue().forEach((name, tag) -> jsonObject.add(name, tag.toJson()));
        return jsonObject;
    }
}