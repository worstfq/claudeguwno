package pl.dragonsword.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manager cooldownow przechowywany w pamieci pluginu.
 * Kazdy gracz ma osobny, niezalezny cooldown.
 */
public class CooldownManager {

    /** Mapa: UUID gracza -> czas (epoch millis), w ktorym cooldown sie konczy. */
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    /** Dlugosc cooldownu w milisekundach. */
    private final long cooldownMillis;

    /**
     * @param cooldownSeconds dlugosc cooldownu w sekundach
     */
    public CooldownManager(long cooldownSeconds) {
        this.cooldownMillis = cooldownSeconds * 1000L;
    }

    /**
     * Sprawdza, czy gracz jest aktualnie na cooldownie.
     * Jesli cooldown juz minal, wpis jest automatycznie czyszczony.
     *
     * @param uuid UUID gracza
     * @return true jesli gracz musi jeszcze poczekac
     */
    public boolean isOnCooldown(UUID uuid) {
        Long expiry = cooldowns.get(uuid);
        if (expiry == null) {
            return false;
        }
        if (System.currentTimeMillis() >= expiry) {
            cooldowns.remove(uuid);
            return false;
        }
        return true;
    }

    /**
     * Ustawia (rozpoczyna) cooldown dla gracza.
     *
     * @param uuid UUID gracza
     */
    public void startCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis() + cooldownMillis);
    }

    /**
     * Zwraca liczbe pozostalych sekund cooldownu, zaokraglona w gore.
     *
     * @param uuid UUID gracza
     * @return liczba pozostalych pelnych sekund (0 jesli brak cooldownu)
     */
    public long getRemainingSeconds(UUID uuid) {
        Long expiry = cooldowns.get(uuid);
        if (expiry == null) {
            return 0L;
        }
        long remainingMillis = expiry - System.currentTimeMillis();
        if (remainingMillis <= 0L) {
            return 0L;
        }
        // Zaokraglenie w gore do pelnych sekund.
        return (long) Math.ceil(remainingMillis / 1000.0);
    }

    /**
     * Usuwa cooldown gracza (np. przy wylogowaniu lub restarcie umiejetnosci).
     *
     * @param uuid UUID gracza
     */
    public void clearCooldown(UUID uuid) {
        cooldowns.remove(uuid);
    }
}
