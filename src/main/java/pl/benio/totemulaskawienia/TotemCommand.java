package pl.benio.totemulaskawienia;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Obsluga komendy {@code /totemulaskawienia give [ilosc]}.
 *
 * <p>Wymaga permisji {@code totemulaskawienia.give}. Przedmiot wydawany jest
 * graczowi wykonujacemu komende. Poniewaz totem nie stackuje sie powyzej 1 sztuki,
 * podana ilosc rozdzielana jest na pojedyncze przedmioty.
 */
public final class TotemCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = "totemulaskawienia.give";
    private static final int MAX_AMOUNT = 64;

    private final TotemUlaskawieniaPlugin plugin;

    public TotemCommand(TotemUlaskawieniaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Skladnia: /totemulaskawienia give [ilosc]
        if (args.length == 0 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage("§7Użycie: §e/" + label + " give [ilość]");
            return true;
        }

        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage("§cNie masz uprawnień do tej komendy.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTę komendę może wykonać tylko gracz.");
            return true;
        }

        // Opcjonalna ilosc (domyslnie 1)
        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cPodana ilość nie jest liczbą.");
                return true;
            }
            // Ograniczenie do rozsadnego zakresu
            amount = Math.max(1, Math.min(amount, MAX_AMOUNT));
        }

        // Wydanie pojedynczych totemow; nadmiar (gdy ekwipunek pelny) laduje obok gracza
        for (int i = 0; i < amount; i++) {
            ItemStack totem = plugin.getTotemItem().create(1);
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(totem);
            leftover.values().forEach(item ->
                    player.getWorld().dropItemNaturally(player.getLocation(), item));
        }

        player.sendMessage("§aOtrzymano §6Totem Ułaskawienia §7x" + amount + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Pierwszy argument: podpowiedz "give"
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            if ("give".startsWith(args[0].toLowerCase())) {
                suggestions.add("give");
            }
            return suggestions;
        }
        // Drugi argument: kilka przykladowych ilosci
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return List.of("1", "8", "16", "64");
        }
        return Collections.emptyList();
    }
}
