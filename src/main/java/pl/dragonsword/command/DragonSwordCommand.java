package pl.dragonsword.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.dragonsword.manager.ItemManager;

import java.util.Collections;
import java.util.List;

/**
 * Obsluga komendy /smoczymiecz.
 * <p>
 * Po wykonaniu komendy gracz z uprawnieniem dragonsword.give
 * otrzymuje Smoczy Miecz. Klasa pelni rowniez role TabCompletera.
 */
public class DragonSwordCommand implements CommandExecutor, TabCompleter {

    private final ItemManager itemManager;

    public DragonSwordCommand(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        // Komenda dostepna tylko dla graczy.
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text(
                    "Ta komenda moze byc uzyta tylko przez gracza.",
                    NamedTextColor.RED));
            return true;
        }

        // Sprawdzenie uprawnienia (dodatkowo do tego z plugin.yml).
        if (!player.hasPermission("dragonsword.give")) {
            player.sendMessage(Component.text(
                    "Nie masz uprawnien do tej komendy.",
                    NamedTextColor.RED));
            return true;
        }

        // Dodanie miecza do ekwipunku gracza.
        player.getInventory().addItem(itemManager.createDragonSword());
        player.sendMessage(Component.text(
                "Otrzymales Smoczy Miecz!",
                NamedTextColor.LIGHT_PURPLE));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      @NotNull String[] args) {
        // Komenda nie przyjmuje argumentow - brak podpowiedzi.
        return Collections.emptyList();
    }
}
