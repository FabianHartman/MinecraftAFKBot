package fabian.hartman.MinecraftAFKBot.modules.command.brigardier.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.Getter;

import java.util.function.Consumer;

public class StubArgumentType implements ArgumentType<Object> {
    @Getter
    private final int id;
    private final Consumer<StringReader> reader;

    public StubArgumentType(int id) {
        this.id = id;
        this.reader = stringReader -> {
            while (stringReader.canRead() && !Character.isWhitespace(stringReader.peek()))
                stringReader.skip();
        };
    }

    @Override
    public Object parse(StringReader reader) throws CommandSyntaxException {
        this.reader.accept(reader);
        return null;
    }
}