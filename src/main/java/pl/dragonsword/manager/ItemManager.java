package pl.dragonsword.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager odpowiedzialny za tworzenie Smoczego Miecza
 * oraz jego jednoznaczne rozpoznawanie (po PersistentDataContainer,
 * a nie tylko po nazwie).
 */
public class ItemManager {

    /** Serializer pozwalajacy uzywac kolorow w stylu legacy (np. §5§l). */
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacySection();

    /** Wyswietlana nazwa miecza. */
    private static final String DISPLAY_NAME = "§5§lSmoczy Miecz";

    /** Unikalny klucz zapisywany w PersistentDataContainer miecza. */
    private final NamespacedKey swordKey;

    public ItemManager(JavaPlugin plugin) {
        this.swordKey = new NamespacedKey(plugin, "dragon_sword");
    }

    public NamespacedKey getSwordKey() {
        return swordKey;
    }

    /**
     * Tworzy gotowy egzemplarz Smoczego Miecza.
     *
     * @return ItemStack reprezentujacy Smoczy Miecz
     */
    public ItemStack createDragonSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();

        // Nazwa (z wylaczona kursywa, ktora Minecraft dodaje domyslnie).
        meta.displayName(toComponent(DISPLAY_NAME));

        // Lore.
        meta.lore(buildLore());

        // Niezniszczalnosc.
        meta.setUnbreakable(true);

        // Efekt swiecenia (glint) bez koniecznosci dodawania prawdziwego enchantu.
        meta.setEnchantmentGlintOverride(true);

        // Ukrycie wszystkich zbednych informacji (enchanty, atrybuty,
        // niezniszczalnosc oraz dodatkowe tooltipy).
        meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_ARMOR_TRIM
        );

        // Zapis znacznika w PersistentDataContainer - to po nim rozpoznajemy miecz.
        meta.getPersistentDataContainer().set(swordKey, PersistentDataType.BYTE, (byte) 1);

        sword.setItemMeta(meta);
        return sword;
    }

    /**
     * Sprawdza, czy podany przedmiot jest Smoczym Mieczem.
     * Weryfikacja opiera sie na materiale ORAZ PersistentDataContainer,
     * a nie wylacznie na nazwie.
     *
     * @param item przedmiot do sprawdzenia (moze byc null)
     * @return true jesli przedmiot jest Smoczym Mieczem
     */
    public boolean isDragonSword(ItemStack item) {
        // Podstawowe sprawdzenia.
        if (item == null || item.getType() != Material.NETHERITE_SWORD) {
            return false;
        }
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        // Glowna weryfikacja - obecnosc naszego znacznika w PDC.
        Byte marker = meta.getPersistentDataContainer()
                .get(swordKey, PersistentDataType.BYTE);

        return marker != null && marker == (byte) 1;
    }

    /**
     * Buduje liste linijek lore z wlaczonymi kolorami i wylaczona kursywa.
     */
    private List<Component> buildLore() {
        List<String> lines = List.of(
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "§7Legendarna broń smoków.",
                "§7 ",
                "§dUmiejętność:",
                "§f► Prawy klik wyrzuca Perłę Endu",
                "§f► Teleportuje gracza",
                "§f► Cooldown: 60 sekund",
                "§7 ",
                "§eKliknij PPM aby użyć.",
                "§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
        );

        List<Component> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(toComponent(line));
        }
        return lore;
    }

    /**
     * Konwertuje tekst legacy (z kolorami §) na Component Adventure
     * z wylaczona domyslna kursywa.
     */
    private Component toComponent(String legacyText) {
        return LEGACY.deserialize(legacyText)
                .decoration(TextDecoration.ITALIC, false);
    }
}
