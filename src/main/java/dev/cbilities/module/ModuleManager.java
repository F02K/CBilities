package dev.cbilities.module;

import dev.cbilities.config.ConfigManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/** Enables and disables modules according to the configuration. */
public final class ModuleManager {

    private final ConfigManager config;
    private final Logger logger;
    private final List<Module> registered = new ArrayList<>();
    private final List<Module> active = new ArrayList<>();

    public ModuleManager(ConfigManager config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public void register(Module module) {
        registered.add(module);
    }

    /** (Re)starts every registered module whose config entry is enabled. */
    public void enableAll() {
        disableAll();
        for (Module module : registered) {
            if (!config.isModuleEnabled(module.id())) {
                continue;
            }
            try {
                module.enable();
                active.add(module);
                logger.info("Module enabled: " + module.id());
            } catch (RuntimeException e) {
                logger.severe("Module " + module.id() + " failed to enable: " + e);
            }
        }
    }

    public void disableAll() {
        for (Module module : active) {
            try {
                module.disable();
            } catch (RuntimeException e) {
                logger.severe("Module " + module.id() + " failed to disable: " + e);
            }
        }
        active.clear();
    }
}
