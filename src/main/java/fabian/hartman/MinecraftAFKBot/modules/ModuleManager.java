package fabian.hartman.MinecraftAFKBot.modules;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {
    @Getter private final List<Module> loadedModules = new CopyOnWriteArrayList<>();

    public void disableAll() {
        getLoadedModules().stream()
                .filter(module -> !module.getClass().equals(HandshakeModule.class))
                .forEach(Module::disable);
        getLoadedModules().clear();
    }

    public void enableModule(Module module) {
        if (isLoaded(module))
            return;
        getLoadedModules().add(module);
        module.enable();
    }

    public boolean isLoaded(Module module) {
        return getLoadedModules().stream().anyMatch(m -> m.getClass().getName().equals(module.getClass().getName()));
    }
}