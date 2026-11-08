package dev.cbilities.module.lunge;

import dev.cbilities.module.Module;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityExhaustionEvent.ExhaustionReason;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Lunge drains hunger by applying exhaustion through an enchantment effect.
 * We cancel exactly that exhaustion while a Lunge item is held.
 */
public final class LungeNoHungerModule implements Module, Listener {

    private final JavaPlugin plugin;

    public LungeNoHungerModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String id() {
        return "lunge-no-hunger";
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExhaustion(EntityExhaustionEvent event) {
        if (event.getExhaustionReason() != ExhaustionReason.ENCHANTMENT_EFFECT
                || !(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LUNGE)) {
            event.setCancelled(true);
        }
    }
}
