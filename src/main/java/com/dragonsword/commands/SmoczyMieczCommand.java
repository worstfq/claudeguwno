package com.dragonsword.commands;

import com.dragonsword.DragonSword;
import com.dragonsword.managers.ItemManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Executor komendy /smoczymiecz.
 * <p>
 * Domyślne użycie (przez gracza, bez argumentów) wydaje Smoczy Miecz wykonawcy komendy.
 * Dodatkowo, jako rozszerzenie, komenda wspiera opcjonalny argument z nazwą gracza
 * (np. przydatne przy wykonywaniu komendy z konsoli: /smoczymiecz Gracz123).
 */
public class SmoczyMieczCommand implements CommandExecutor {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final ItemManager itemManager;

    public SmoczyMieczCommand(DragonSword plugin) {
        this.itemManager = plugin.getItemManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                              @NotNull String label, @NotNull String[] args) {

        // Weryfikacja uprawnień (dodatkowo wymuszana też przez plugin.yml, ale sprawdzamy
        // jawnie na wypadek wywołania komendy w inny sposób)
        if (!sender.hasPermission("dragonsword.give")) {
            sender.sendMessage(LEGACY.deserialize("§cNie masz uprawnień do użycia tej komendy."));
            return true;
        }

        Player target = resolveTarget(sender, args);
        if (target == null) {
            // Odpowiednia wiadomość o błędzie została już wysłana w resolveTarget()
            return true;
        }

        giveDragonSword(sender, target);
        return true;
    }

    /**
     * Wyznacza gracza, który ma otrzymać Smoczy Miecz, na podstawie argumentów komendy
     * oraz typu wykonawcy (gracz / konsola).
     *
     * @return gracz-cel, lub null, jeśli nie udało się go wyznaczyć (z wysłaną wiadomością o błędzie)
     */
    private Player resolveTarget(CommandSender sender, String[] args) {

        if (args.length > 0) {
            // Podano nazwę gracza jako argument
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(LEGACY.deserialize("§cGracz §e" + args[0] + " §cnie jest online."));
                return null;
            }
            return target;
        }

        // Brak argumentów - komenda musi być wykonana przez gracza, który otrzyma miecz
        if (!(sender instanceof Player player)) {
            sender.sendMessage(LEGACY.deserialize("§cZ konsoli musisz podać nazwę gracza: /smoczymiecz <gracz>"));
            return null;
        }

        return player;
    }

    /**
     * Tworzy Smoczy Miecz i przekazuje go graczowi-celowi, obsługując przypadek
     * pełnego ekwipunku (przedmiot zostaje upuszczony na ziemię pod nogami gracza).
     */
    private void giveDragonSword(CommandSender sender, Player target) {
        ItemStack dragonSword = itemManager.createDragonSword();

        HashMap<Integer, ItemStack> leftover = target.getInventory().addItem(dragonSword);

        if (!leftover.isEmpty()) {
            // Ekwipunek gracza był pełny - upuszczamy miecz na ziemię, aby nic nie zniknęło
            leftover.values().forEach(item ->
                    target.getWorld().dropItemNaturally(target.getLocation(), item));
        }

        target.sendMessage(LEGACY.deserialize("§5§lOtrzymałeś Smoczy Miecz!"));

        if (!sender.equals(target)) {
            sender.sendMessage(LEGACY.deserialize("§aPrzekazano Smoczy Miecz graczowi §e" + target.getName() + "§a."));
        }
    }
}
