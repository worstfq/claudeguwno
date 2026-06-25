package pl.benio.totemulaskawienia;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Fabryka odpowiedzialna za tworzenie oraz rozpoznawanie przedmiotu
 * "Totem Ulaskawienia".
 *
 * <p>Identyfikacja przedmiotu opiera sie o {@link org.bukkit.persistence.PersistentDataContainer},
 * dzieki czemu totem jest rozpoznawany niezaleznie od nazwy czy lore
 * (np. po zmianie nazwy w kowadle). Jest to znacznie bezpieczniejsze niz
 * porownywanie nazwy wyswietlanej.
 */
public final class TotemItem {

    /** Klucz znacznika w PersistentDataContainer. */
    private final NamespacedKey key;

    /** Wartosc CustomModelData (pod wlasny model w resource packu). */
    private final int customModelData;

    /** Czy przedmiot ma swiecic jak zaklety. */
    private final boolean glow;

    public TotemItem(NamespacedKey key, int customModelData, boolean glow) {
        this.key = key;
        this.customModelData = customModelData;
        this.glow = glow;
    }

    /**
     * Tworzy przedmiot Totem Ulaskawienia.
     *
     * @param amount liczba sztuk (minimum 1)
     * @return gotowy {@link ItemStack}
     */
    public ItemStack create(int amount) {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();

        // Nazwa przedmiotu (wylaczamy domyslna kursywe nazw/lore)
        meta.displayName(text("§6§lTotem Ułaskawienia"));

        // Opis (lore)
        meta.lore(List.of(
                text("§7Artefakt przebaczenia."),
                text("§7Śmierć zabierze tylko totem."),
                text(" "),
                text("§8Po śmierci:"),
                text("§8• Zachowujesz cały ekwipunek"),
                text("§8• Zachowujesz doświadczenie"),
                text("§8• Nic nie wypada na ziemię"),
                text("§8• Totem zostaje zniszczony")
        ));

        // Wlasny model przez resource pack (latwy do zmiany w config.yml)
        meta.setCustomModelData(customModelData);

        // Efekt poswiaty bez realnego zaklecia
        if (glow) {
            meta.setEnchantmentGlintOverride(true);
        }

        // Maksymalnie 1 sztuka w jednym stacku
        meta.setMaxStackSize(1);

        // Znacznik pozwalajacy jednoznacznie rozpoznac nasz przedmiot
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Sprawdza, czy podany przedmiot to Totem Ulaskawienia.
     *
     * @param item przedmiot do sprawdzenia (moze byc null)
     * @return {@code true}, jesli to nasz totem
     */
    public boolean isTotem(ItemStack item) {
        if (item == null || item.getType() != Material.TOTEM_OF_UNDYING) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    /**
     * Tworzy komponent tekstowy z kodow kolorow (&sect;) i wylacza domyslna kursywe.
     */
    private static Component text(String legacy) {
        return LegacyComponentSerializer.legacySection()
                .deserialize(legacy)
                .decoration(TextDecoration.ITALIC, false);
    }
}
