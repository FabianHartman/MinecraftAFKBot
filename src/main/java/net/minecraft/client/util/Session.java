package net.minecraft.client.util;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Session {
    public enum AccountType {
        LEGACY("legacy"),
        MOJANG("mojang"),
        MSA("msa");

        private static final Map<String, AccountType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap((type) -> type.name, Function.identity()));
        private final String name;

        AccountType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}