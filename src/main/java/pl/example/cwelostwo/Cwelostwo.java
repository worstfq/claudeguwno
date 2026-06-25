package pl.example.cwelostwo;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Cwelostwo extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Cwelostwo wlaczone.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cwelostwo wylaczone.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("cwelostwo")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Te komende moze wykonac tylko gracz w grze.");
            return true;
        }

        final Player player = (Player) sender;
        player.sendMessage(ChatColor.RED + "Plomienie cie pochlaniaja...");

        // Co sekunde podtrzymujemy ogien, az gracz zginie lub wyjdzie z serwera.
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    cancel();
                    return;
                }
                // 160 tickow = 8 sekund ognia; odnawiane co sekunde, wiec gracz plonie ciagle.
                player.setFireTicks(160);
            }
        }.runTaskTimer(this, 0L, 20L);

        return true;
    }
}
