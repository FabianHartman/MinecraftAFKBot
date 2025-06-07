package net.minecraft.client.util;

public class Session {
    public enum AccountType {
        LEGACY("legacy"),
        MOJANG("mojang"),
        MSA("msa");

        private final String name;

        AccountType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}