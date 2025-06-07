package fabian.hartman.MinecraftAFKBot.io.config;

import com.google.gson.JsonParseException;
import fabian.hartman.MinecraftAFKBot.MinecraftAFKBot;

import java.io.IOException;
import java.text.MessageFormat;

public interface Config {
    default void init(String dir) {
        try {
            new PropertyProcessor().processAnnotations(this, dir);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            System.out.println(MessageFormat.format("***************************************************************************************\\n\\\n" +
                    "Your configuration could not be parsed because it does not match the json-format:\\n\\\n" +
                    "{0}\\n\\\n" +
                    "***************************************************************************************", e.toString()));
        }
    }
}