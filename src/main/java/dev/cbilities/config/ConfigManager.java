package dev.cbilities.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Loads config.yml, adds options missing from older files and keeps
 * timestamped backups of the file before it is ever rewritten.
 */
public final class ConfigManager {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final JavaPlugin plugin;
    private final Logger logger;
    private final Path file;
    private final Path backupDir;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.file = plugin.getDataFolder().toPath().resolve("config.yml");
        this.backupDir = plugin.getDataFolder().toPath().resolve("backups");
    }

    /** Loads the config, merging in new defaults (after a backup) if needed. */
    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();
        boolean missing = config.getDefaults() != null
                && config.getDefaults().getKeys(true).stream().anyMatch(key -> !config.isSet(key));
        if (missing) {
            backup();
            config.options().copyDefaults(true);
            plugin.saveConfig();
            logger.info("Added new config options; previous file saved to backups.");
        }
    }

    public boolean isModuleEnabled(String id) {
        return plugin.getConfig().getBoolean("modules." + id + ".enabled", false);
    }

    /** Copies the current config.yml into backups/ and prunes old backups. */
    public void backup() {
        if (!Files.exists(file)) {
            return;
        }
        try {
            Files.createDirectories(backupDir);
            Path target = backupDir.resolve("config-" + LocalDateTime.now().format(STAMP) + ".yml");
            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
            prune();
        } catch (IOException e) {
            logger.warning("Could not back up config: " + e.getMessage());
        }
    }

    private void prune() throws IOException {
        int keep = Math.max(1, plugin.getConfig().getInt("max-backups", 10));
        List<Path> backups;
        try (Stream<Path> files = Files.list(backupDir)) {
            backups = files.filter(p -> p.getFileName().toString().startsWith("config-")).sorted().toList();
        }
        for (Path old : backups.subList(0, Math.max(0, backups.size() - keep))) {
            Files.deleteIfExists(old);
        }
    }
}
