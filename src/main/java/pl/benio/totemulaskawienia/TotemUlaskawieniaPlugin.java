package pl.benio.totemulaskawienia;

import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Glowna klasa pluginu "Totem Ulaskawienia".
 *
 * <p>Plugin dodaje specjalny przedmiot, ktory po smierci gracza:
 * <ul>
 *     <li>zostaje zuzyty (znika z ekwipunku),</li>
 *     <li>chroni caly ekwipunek, zbroje oraz przedmioty w rekach,</li>
 *     <li>zachowuje poziomy doswiadczenia,</li>
 *     <li>sprawia, ze nic nie wypada na ziemie.</li>
 * </ul>
 * Dziala to podobnie do gamerule {@code keepInventory}, ale wylacznie wtedy,
 * gdy gracz w chwili smierci posiada Totem Ulaskawienia.
 */
public final class TotemUlaskawieniaPlugin extends JavaPlugin {

    /** Fabryka tworzaca i rozpoznajaca nasz przedmiot. */
    private TotemItem totemItem;

    @Override
    public void onEnable() {
        // Zapis domyslnego config.yml, jesli jeszcze nie istnieje
        saveDefaultConfig();

        // Wczytanie ustawien z konfiguracji
        int customModelData = getConfig().getInt("custom-model-data", 100100);
        boolean glow = getConfig().getBoolean("glow", true);

        // Unikalny klucz sluzacy do oznaczania przedmiotu w PersistentDataContainer
        NamespacedKey totemKey = new NamespacedKey(this, "totem_ulaskawienia");
        this.totemItem = new TotemItem(totemKey, customModelData, glow);

        // Rejestracja listenera odpowiedzialnego za obsluge smierci
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        // Rejestracja komendy /totemulaskawienia
        PluginCommand command = getCommand("totemulaskawienia");
        if (command != null) {
            TotemCommand executor = new TotemCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        } else {
            getLogger().warning("Nie udalo sie zarejestrowac komendy 'totemulaskawienia' "
                    + "(sprawdz plugin.yml).");
        }

        getLogger().info("Totem Ulaskawienia zostal wlaczony.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Totem Ulaskawienia zostal wylaczony.");
    }

    /** Zwraca fabryke przedmiotu Totem Ulaskawienia. */
    public TotemItem getTotemItem() {
        return totemItem;
    }
}
