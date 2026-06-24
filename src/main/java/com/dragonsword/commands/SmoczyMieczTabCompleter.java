package com.dragonsword.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TabCompleter dla komendy /smoczymiecz.
 * <p>
 * Podpowiada nazwy aktualnie zalogowanych graczy dla pierwszego argumentu,
 * co przydaje się np. przy wydawaniu miecza innemu graczowi z konsoli
 * (/smoczymiecz <Tab>).
 */
public class SmoczyMieczTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                  @NotNull String alias, @NotNull String[] args) {

        if (args.length != 1) {
            // Komenda obsługuje tylko jeden opcjonalny argument - dla innych pozycji nie podpowiadamy nic
            return new ArrayList<>();
        }

        String partialName = args[0].toLowerCase();

        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(partialName))
                .collect(Collectors.toList());
    }
}
