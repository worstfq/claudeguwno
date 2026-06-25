package pl.dragonsword;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pl.dragonsword.command.DragonSwordCommand;
import pl.dragonsword.listener.SwordListener;
import pl.dragonsword.manager.CooldownManager;
import pl.dragonsword.manager.ItemManager;

/**
 * Glowna klasa pluginu DragonSword.
 * <p>
 * Odpowiada za inicjalizacje managerow, rejestracje komendy
 * oraz nasluchiwacza zdarzen przy wlaczaniu serwera.
 */
public final class DragonSword extends JavaPlugin {

    /** Czas trwania cooldownu w sekundach. */
    private static final long COOLDOWN_SECONDS = 60L;

    private ItemManager itemManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        // Inicjalizacja managerow.
        this.itemManager = new ItemManager(this);
        this.cooldownManager = new CooldownManager(COOLDOWN_SECONDS);

        // Rejestracja komendy /smoczymiecz wraz z TabCompleterem.
        registerCommand();

        // Rejestracja nasluchiwacza klikniec.
        getServer().getPluginManager().registerEvents(
                new SwordListener(itemManager, cooldownManager, COOLDOWN_SECONDS), this);

        getLogger().info("DragonSword zostal wlaczony.");
    }

    @Override
    public void onDisable() {
        getLogger().info("DragonSword zostal wylaczony.");
    }

    /**
     * Rejestruje komende /smoczymiecz oraz jej TabCompleter.
     * Sprawdza poprawnosc rejestracji (komenda musi istniec w plugin.yml).
     */
    private void registerCommand() {
        DragonSwordCommand command = new DragonSwordCommand(itemManager);
        PluginCommand pluginCommand = getCommand("smoczymiecz");

        if (pluginCommand == null) {
            getLogger().severe("Nie udalo sie zarejestrowac komendy /smoczymiecz! "
                    + "Sprawdz plugin.yml.");
            return;
        }

        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
