package pl.dragonsword.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import pl.dragonsword.manager.CooldownManager;
import pl.dragonsword.manager.ItemManager;

import java.util.UUID;

/**
 * Nasluchiwacz obslugujacy umiejetnosc Smoczego Miecza.
 * <p>
 * Po kliknieciu PPM Smoczym Mieczem gracz wyrzuca Perle Endu
 * (bez zuzywania jakiegokolwiek przedmiotu), uruchamiane sa efekty
 * dzwiekowe i czasteczkowe oraz rozpoczyna sie cooldown.
 */
public class SwordListener implements Listener {

    /** 20 tickow = 1 sekunda. */
    private static final int TICKS_PER_SECOND = 20;

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;
    private final long cooldownSeconds;

    public SwordListener(ItemManager itemManager,
                         CooldownManager cooldownManager,
                         long cooldownSeconds) {
        this.itemManager = itemManager;
        this.cooldownManager = cooldownManager;
        this.cooldownSeconds = cooldownSeconds;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Reagujemy tylko na glowna reke, by uniknac podwojnego wywolania.
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        // Reagujemy tylko na klikniecie prawym przyciskiem (w powietrze lub blok).
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Sprawdzenie, czy gracz trzyma Smoczy Miecz (material + PDC).
        if (!itemManager.isDragonSword(itemInHand)) {
            return;
        }

        // Od tego momentu blokujemy domyslna akcje przedmiotu.
        event.setCancelled(true);

        // Sprawdzenie uprawnienia do uzywania umiejetnosci.
        if (!player.hasPermission("dragonsword.use")) {
            player.sendMessage(Component.text(
                    "Nie masz uprawnien do uzycia tej umiejetnosci.",
                    NamedTextColor.RED));
            return;
        }

        UUID uuid = player.getUniqueId();

        // Sprawdzenie cooldownu.
        if (cooldownManager.isOnCooldown(uuid)) {
            long remaining = cooldownManager.getRemainingSeconds(uuid);
            sendCooldownMessage(player, remaining);
            return;
        }

        // Wykonanie umiejetnosci.
        activateAbility(player);
    }

    /**
     * Aktywuje umiejetnosc miecza: wyrzucenie Perly Endu,
     * efekty oraz rozpoczecie cooldownu.
     */
    private void activateAbility(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();

        // Wyrzucenie Perly Endu dokladnie jak po uzyciu zwyklej perly.
        // Gracz jest automatycznie ustawiony jako rzucajacy, wiec po wyladowaniu
        // zostanie przeteleportowany. Nie zuzywa to zadnego przedmiotu z ekwipunku.
        player.launchProjectile(EnderPearl.class);

        // Efekt dzwiekowy teleportacji Endermana.
        world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        // Efekty czasteczkowe Endu wokol gracza (na wysokosci tulowia).
        Location effectLocation = location.clone().add(0, 1, 0);
        world.spawnParticle(Particle.PORTAL, effectLocation, 40, 0.5, 1.0, 0.5, 0.1);
        world.spawnParticle(Particle.DRAGON_BREATH, effectLocation, 20, 0.5, 1.0, 0.5, 0.05);

        // Rozpoczecie cooldownu w pamieci pluginu.
        cooldownManager.startCooldown(player.getUniqueId());

        // Ustawienie wizualnego cooldownu przedmiotu (szary pasek jak przy tarczy).
        player.setCooldown(Material.NETHERITE_SWORD, (int) cooldownSeconds * TICKS_PER_SECOND);
    }

    /**
     * Wysyla wiadomosc o pozostalym cooldownie w formacie:
     * "§cMusisz poczekać jeszcze §eX§c sekund."
     */
    private void sendCooldownMessage(Player player, long remainingSeconds) {
        Component message = Component.text("Musisz poczekać jeszcze ", NamedTextColor.RED)
                .append(Component.text(remainingSeconds, NamedTextColor.YELLOW))
                .append(Component.text(" sekund.", NamedTextColor.RED));
        player.sendMessage(message);
    }
}
