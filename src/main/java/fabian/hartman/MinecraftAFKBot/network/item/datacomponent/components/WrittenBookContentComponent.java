package fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components;

import com.google.common.io.ByteArrayDataOutput;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.DataComponent;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.writablebook.FilteredString;
import fabian.hartman.MinecraftAFKBot.network.item.datacomponent.components.parts.writablebook.FilteredTag;
import fabian.hartman.MinecraftAFKBot.network.protocol.Packet;
import fabian.hartman.MinecraftAFKBot.network.utils.ByteArrayDataInputWrapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WrittenBookContentComponent extends DataComponent {
    private FilteredString title;
    private String author;
    private int generation;
    private List<FilteredTag> pages = Collections.emptyList();
    private boolean resolved;

    public WrittenBookContentComponent(int componentTypeId) {
        super(componentTypeId);
    }

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) {
        title.write(out, protocolId);
        Packet.writeString(author, out);
        Packet.writeVarInt(generation, out);
        Packet.writeVarInt(pages.size(), out);
        for (FilteredTag nbt : pages) {
            nbt.write(out, protocolId);
        }
        out.writeBoolean(resolved);
    }

    @Override
    public void read(ByteArrayDataInputWrapper in, int protocolId) {
        FilteredString title = new FilteredString();
        title.read(in, protocolId);
        this.title = title;

        this.author = Packet.readString(in);
        this.generation = Packet.readVarInt(in);
        this.pages = new LinkedList<>();
        int count = Packet.readVarInt(in);
        for (int i = 0; i < count; i++) {
            FilteredTag tag = new FilteredTag();
            tag.read(in, protocolId);
            pages.add(tag);
        }
        this.resolved = in.readBoolean();
    }
}