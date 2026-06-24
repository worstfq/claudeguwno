package com.dragonsword.managers;

import com.dragonsword.DragonSword;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa odpowiedzialna za tworzenie oraz bezpieczną weryfikację Smoczego Miecza.
 * <p>
 * Cała logika budowy przedmiotu (nazwa, lore, atrybuty, enchanty, flagi, znacznik PDC)
 * znajduje się w jednym miejscu, dzięki czemu łatwo go modyfikować bez ingerencji
 * w logikę listenera czy komendy.
 */
public class ItemManager {

    // Serializer konwertujący stary format kolorów (np. "§5§l") na komponenty Adventure
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    // Wartość dodatkowych obrażeń ukrytych w atrybucie miecza
    private static final double BONUS_ATTACK_DAMAGE = 3.0;

    private final DragonSword plugin;

    // Unikalny klucz NBT/PersistentDataContainer oznaczający oryginalny Smoczy Miecz
    private final NamespacedKey dragonSwordKey;

    public ItemManager(DragonSword plugin) {
        this.plugin = plugin;
        this.dragonSwordKey = new NamespacedKey(plugin, "dragon_sword");
    }

    /**
     * Tworzy nowy egzemplarz Smoczego Miecza ze wszystkimi wymaganymi właściwościami:
     * netherytowy miecz, specjalna nazwa i lore, efekt świecenia, ukryte enchanty
     * i atrybuty, niezniszczalność oraz znacznik identyfikujący przedmiot.
     *
     * @return gotowy do wydania ItemStack reprezentujący Smoczy Miecz
     */
    public ItemStack createDragonSword() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            // Teoretycznie niemożliwe dla NETHERITE_SWORD, ale zabezpieczamy się na wszelki wypadek
            throw new IllegalStateException("Nie udalo sie utworzyc ItemMeta dla NETHERITE_SWORD.");
        }

        // ---- Nazwa przedmiotu ----
        meta.displayName(LEGACY.deserialize("§5§lSmoczy Miecz"));

        // ---- Lore przedmiotu ----
        meta.lore(buildLore());

        // ---- Niezniszczalność ----
        meta.setUnbreakable(true);

        // ---- Ukryty enchant - czysto kosmetyczny, niewidoczny dzięki ItemFlag.HIDE_ENCHANTS ----
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);

        // ---- Wymuszony efekt świecenia (glint) niezależnie od widoczności enchantów ----
        meta.setEnchantmentGlintOverride(true);

        // ---- Ukryty atrybut: dodatkowe obrażenia aktywne tylko gdy miecz jest w głównej ręce ----
        AttributeModifier damageModifier = new AttributeModifier(
                new NamespacedKey(plugin, "dragonsword_attack_damage"),
                BONUS_ATTACK_DAMAGE,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        );
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, damageModifier);

        // ---- Ukrycie WSZYSTKICH zbędnych informacji (enchanty, atrybuty, unbreakable itd.) ----
        meta.addItemFlags(ItemFlag.values());

        // ---- Znacznik PersistentDataContainer identyfikujący oryginalny Smoczy Miecz ----
        meta.getPersistentDataContainer().set(dragonSwordKey, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Buduje listę linii lore w formie komponentów Adventure, na podstawie
     * specyfikacji przekazanej przy projektowaniu Smoczego Miecza.
     */
    private List<Component> buildLore() {
        String[] rawLore = {
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§7Legendarna broń smoków.",
                "§7",
                "§dUmiejętność:",
                "§f► Prawy klik wyrzuca Perłę Endu",
                "§f► Teleportuje gracza",
                "§f► Cooldown: 60 sekund",
                "§7",
                "§eKliknij PPM aby użyć.",
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        };

        List<Component> lore = new ArrayList<>(rawLore.length);
        for (String line : rawLore) {
            lore.add(LEGACY.deserialize(line));
        }
        return lore;
    }

    /**
     * Sprawdza, czy podany ItemStack jest oryginalnym Smoczym Mieczem wydanym przez plugin.
     * <p>
     * Weryfikacja opiera się WYŁĄCZNIE na materiale (NETHERITE_SWORD) oraz znaczniku
     * PersistentDataContainer - nigdy na samej nazwie przedmiotu, którą gracz mógłby
     * łatwo podrobić np. za pomocą kowadła i własnego netherytowego miecza.
     *
     * @param item przedmiot do sprawdzenia (może być null)
     * @return true, jeśli przedmiot jest oryginalnym Smoczym Mieczem
     */
    public boolean isDragonSword(ItemStack item) {
        if (item == null) {
            return false;
        }

        if (item.getType() != Material.NETHERITE_SWORD) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        Byte marker = meta.getPersistentDataContainer().get(dragonSwordKey, PersistentDataType.BYTE);
        return marker != null && marker == (byte) 1;
    }

    /**
     * @return klucz PersistentDataContainer używany do oznaczania Smoczego Miecza
     */
    public NamespacedKey getDragonSwordKey() {
        return dragonSwordKey;
    }
}
