package com.dragonsword;

import com.dragonsword.commands.SmoczyMieczCommand;
import com.dragonsword.commands.SmoczyMieczTabCompleter;
import com.dragonsword.listeners.SwordListener;
import com.dragonsword.managers.CooldownManager;
import com.dragonsword.managers.ItemManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Główna klasa pluginu DragonSword.
 * <p>
 * Plugin dodaje do gry specjalny netherytowy miecz - "Smoczy Miecz" - który po
 * kliknięciu prawym przyciskiem myszy pozwala graczowi teleportować się tak,
 * jakby użył Perły Endu, bez konieczności posiadania jej w ekwipunku.
 * <p>
 * Odpowiada za:
 * - inicjalizację managerów ({@link ItemManager}, {@link CooldownManager}),
 * - rejestrację komendy /smoczymiecz wraz z TabCompleterem,
 * - rejestrację listenera obsługującego umiejętność miecza.
 */
public final class DragonSword extends JavaPlugin {

    private ItemManager itemManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {

        // Inicjalizacja managera odpowiedzialnego za tworzenie i weryfikację Smoczego Miecza
        this.itemManager = new ItemManager(this);

        // Inicjalizacja managera cooldownów (przechowywanych w pamięci, per gracz)
        this.cooldownManager = new CooldownManager();

        // Rejestracja komendy /smoczymiecz wraz z jej executorem i tab completerem
        registerCommand();

        // Rejestracja listenera obsługującego prawy klik Smoczym Mieczem
        getServer().getPluginManager().registerEvents(new SwordListener(this), this);

        getLogger().info("DragonSword zostal pomyslnie wlaczony!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DragonSword zostal wylaczony!");
    }

    /**
     * Rejestruje komendę /smoczymiecz zdefiniowaną w plugin.yml.
     */
    private void registerCommand() {
        PluginCommand command = getCommand("smoczymiecz");

        if (command == null) {
            // Jeżeli komenda nie została poprawnie wczytana z plugin.yml, informujemy o tym w konsoli
            getLogger().severe("Nie udalo sie zarejestrowac komendy /smoczymiecz - sprawdz plik plugin.yml!");
            return;
        }

        command.setExecutor(new SmoczyMieczCommand(this));
        command.setTabCompleter(new SmoczyMieczTabCompleter());
    }

    /**
     * @return manager odpowiedzialny za tworzenie i weryfikację Smoczego Miecza
     */
    public ItemManager getItemManager() {
        return itemManager;
    }

    /**
     * @return manager odpowiedzialny za przechowywanie cooldownów umiejętności
     */
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
