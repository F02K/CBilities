package dev.cbilities;

import dev.cbilities.config.ConfigManager;
import dev.cbilities.module.ModuleManager;
import dev.cbilities.module.lunge.LungeNoHungerModule;
import org.bukkit.plugin.java.JavaPlugin;

public final class CBilitiesPlugin extends JavaPlugin {

    private ModuleManager modules;

    @Override
    public void onEnable() {
        ConfigManager config = new ConfigManager(this);
        config.load();

        modules = new ModuleManager(config, getLogger());
        // Register new modules here.
        modules.register(new LungeNoHungerModule(this));
        modules.enableAll();
    }

    @Override
    public void onDisable() {
        if (modules != null) {
            modules.disableAll();
        }
    }
}
