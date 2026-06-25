package pl.benio.totemulaskawienia;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Listener obslugujacy smierc gracza.
 *
 * <p>Jesli w chwili smierci gracz posiada Totem Ulaskawienia (w glownej rece,
 * drugiej rece, zbroi lub gdziekolwiek w ekwipunku), to:
 * <ul>
 *     <li>jeden totem zostaje zuzyty,</li>
 *     <li>ekwipunek oraz poziomy doswiadczenia zostaja zachowane,</li>
 *     <li>nic nie wypada na ziemie (brak dropu przedmiotow i doswiadczenia).</li>
 * </ul>
 * Gracz mimo to umiera normalnie (pojawia sie ekran smierci) - chroniony jest
 * jedynie jego ekwipunek.
 */
public final class DeathListener implements Listener {

    private final TotemUlaskawieniaPlugin plugin;

    public DeathListener(TotemUlaskawieniaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerInventory inventory = player.getInventory();

        // Szukamy totemu w calym ekwipunku (sloty 0-40: hotbar, plecak, zbroja, druga reka)
        int slot = findTotemSlot(inventory);
        if (slot == -1) {
            // Brak totemu - normalna smierc z pelnym dropem przedmiotow
            return;
        }

        // 1) Zuzycie jednego totemu
        consumeOne(inventory, slot);

        // 2) Zachowanie ekwipunku oraz poziomow doswiadczenia
        event.setKeepInventory(true);
        event.setKeepLevel(true);

        // 3) Nic nie wypada na ziemie
        event.getDrops().clear();
        event.setDroppedExp(0);

        // 4) Informacja dla gracza
        player.sendMessage("§6§lTotem Ułaskawienia §7uratował Twój ekwipunek.");
    }

    /**
     * Wyszukuje pierwszy slot zawierajacy Totem Ulaskawienia.
     *
     * @return indeks slotu lub {@code -1}, jesli totemu nie znaleziono
     */
    private int findTotemSlot(PlayerInventory inventory) {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (plugin.getTotemItem().isTotem(contents[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Zmniejsza o 1 liczbe totemow w podanym slocie (lub czysci slot, gdy zostaje 0).
     */
    private void consumeOne(PlayerInventory inventory, int slot) {
        ItemStack item = inventory.getItem(slot);
        if (item == null) {
            return;
        }
        int amount = item.getAmount();
        if (amount <= 1) {
            inventory.setItem(slot, null);
        } else {
            item.setAmount(amount - 1);
            inventory.setItem(slot, item);
        }
    }
}
