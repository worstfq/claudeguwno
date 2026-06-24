package com.dragonsword.listeners;

import com.dragonsword.DragonSword;
import com.dragonsword.managers.CooldownManager;
import com.dragonsword.managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Listener obsługujący umiejętność Smoczego Miecza.
 * <p>
 * Po kliknięciu prawym przyciskiem myszy z mieczem w głównej ręce:
 * - weryfikuje, czy gracz trzyma oryginalny Smoczy Miecz oraz ma uprawnienia,
 * - sprawdza cooldown gracza,
 * - wyrzuca Perłę Endu (bez zużywania jakiegokolwiek przedmiotu),
 * - odtwarza dźwięk i cząsteczki teleportacji,
 * - nakłada nowy cooldown (w pamięci pluginu oraz widoczny pasek ItemCooldown API).
 */
public class SwordListener implements Listener {

    // Czas trwania cooldownu w tickach (1 sekunda = 20 ticków) - na potrzeby ItemCooldown API
    private static final int ITEM_COOLDOWN_TICKS = CooldownManager.COOLDOWN_SECONDS * 20;

    // Siła rzutu Perły Endu, odpowiadająca sile standardowego rzutu w grze (vanilla)
    private static final double THROW_POWER = 1.5;

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final ItemManager itemManager;
    private final CooldownManager cooldownManager;

    public SwordListener(DragonSword plugin) {
        this.itemManager = plugin.getItemManager();
        this.cooldownManager = plugin.getCooldownManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {

        // Interesuje nas wyłącznie interakcja głównej ręki - zapobiega podwójnemu
        // wywołaniu logiki (np. dla EquipmentSlot.OFF_HAND przy tym samym kliknięciu)
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        // Interesuje nas wyłącznie prawy klik (w powietrze lub w blok)
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        // Weryfikacja: czy gracz trzyma oryginalny Smoczy Miecz (materiał + PersistentDataContainer)
        if (!itemManager.isDragonSword(handItem)) {
            return;
        }

        // Anulujemy domyślną akcję, aby uniknąć niechcianej interakcji z blokami
        // (np. otwarcia skrzynki) podczas korzystania z umiejętności miecza
        event.setCancelled(true);

        // Weryfikacja uprawnień do korzystania z umiejętności
        if (!player.hasPermission("dragonsword.use")) {
            player.sendMessage(LEGACY.deserialize("§cNie masz uprawnień do używania umiejętności tego miecza."));
            return;
        }

        UUID playerId = player.getUniqueId();

        // Sprawdzenie cooldownu przechowywanego w pamięci pluginu
        if (cooldownManager.isOnCooldown(playerId)) {
            long remainingSeconds = cooldownManager.getRemainingSeconds(playerId);
            Component message = LEGACY.deserialize(
                    "§cMusisz poczekać jeszcze §e" + remainingSeconds + "§c sekund."
            );
            player.sendMessage(message);
            return;
        }

        activateDragonSwordAbility(player);
    }

    /**
     * Wykonuje pełną sekwencję umiejętności Smoczego Miecza: rzut Perły Endu
     * (identyczny jak przy standardowym użyciu, ale bez zużywania przedmiotu),
     * efekty dźwiękowe i cząsteczkowe oraz nałożenie cooldownu.
     */
    private void activateDragonSwordAbility(Player player) {
        Location eyeLocation = player.getEyeLocation();

        // Wyrzucenie Perły Endu - gracz nie musi posiadać jej w ekwipunku,
        // żaden przedmiot nie jest zużywany
        EnderPearl pearl = player.launchProjectile(EnderPearl.class);

        // Ustawienie prędkości identycznej jak przy standardowym rzucie Perły Endu
        Vector direction = eyeLocation.getDirection().normalize().multiply(THROW_POWER);
        pearl.setVelocity(direction);

        // Dźwięk teleportacji Endermana
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        // Cząsteczki teleportacji Endu oraz smoczego oddechu
        Location particleLocation = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.PORTAL, particleLocation, 32, 0.5, 1.0, 0.5, 0.5);
        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, particleLocation, 15, 0.4, 0.6, 0.4, 0.02);

        // Ustawienie cooldownu w pamięci pluginu (osobny cooldown dla każdego gracza)
        cooldownManager.setCooldown(player.getUniqueId());

        // Ustawienie widocznego, szarego paska cooldownu na mieczu (ItemCooldown API),
        // identycznego jak przy użyciu tarczy
        player.setCooldown(Material.NETHERITE_SWORD, ITEM_COOLDOWN_TICKS);
    }
}
