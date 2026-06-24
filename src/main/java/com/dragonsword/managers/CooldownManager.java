package com.dragonsword.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manager odpowiedzialny za przechowywanie i obsługę cooldownu umiejętności
 * Smoczego Miecza w pamięci pluginu (osobny cooldown dla każdego gracza).
 * <p>
 * Dane przechowywane są w prostej mapie UUID -> czas zakończenia cooldownu
 * (w milisekundach systemowych). Mapa istnieje wyłącznie w pamięci RAM,
 * dlatego cooldown resetuje się po restarcie/reloadzie serwera.
 */
public class CooldownManager {

    /** Długość cooldownu umiejętności w sekundach. */
    public static final int COOLDOWN_SECONDS = 60;

    // Mapa: UUID gracza -> znacznik czasu (System.currentTimeMillis()) zakończenia cooldownu
    private final Map<UUID, Long> activeCooldowns = new HashMap<>();

    /**
     * Sprawdza, czy gracz jest aktualnie na cooldownie.
     * Jeżeli cooldown już minął, wpis zostaje automatycznie usunięty z pamięci.
     *
     * @param playerId UUID gracza
     * @return true, jeśli gracz nie może jeszcze ponownie użyć umiejętności
     */
    public boolean isOnCooldown(UUID playerId) {
        Long expirationTime = activeCooldowns.get(playerId);

        if (expirationTime == null) {
            return false;
        }

        if (System.currentTimeMillis() >= expirationTime) {
            // Cooldown wygasł - czyścimy wpis, aby mapa nie rosła w nieskończoność
            activeCooldowns.remove(playerId);
            return false;
        }

        return true;
    }

    /**
     * Zwraca liczbę sekund pozostałych do końca cooldownu, zaokrągloną w górę.
     *
     * @param playerId UUID gracza
     * @return liczba pozostałych sekund (0, jeśli cooldown nie jest aktywny)
     */
    public long getRemainingSeconds(UUID playerId) {
        Long expirationTime = activeCooldowns.get(playerId);

        if (expirationTime == null) {
            return 0;
        }

        long remainingMillis = expirationTime - System.currentTimeMillis();

        if (remainingMillis <= 0) {
            return 0;
        }

        // Zaokrąglenie w górę, aby gracz widział np. "1 sekunda" a nie "0 sekund" przy 0.3s
        return (long) Math.ceil(remainingMillis / 1000.0);
    }

    /**
     * Nakłada nowy cooldown na gracza, liczony od aktualnego momentu.
     *
     * @param playerId UUID gracza
     */
    public void setCooldown(UUID playerId) {
        long expirationTime = System.currentTimeMillis() + (COOLDOWN_SECONDS * 1000L);
        activeCooldowns.put(playerId, expirationTime);
    }

    /**
     * Usuwa cooldown gracza (np. przydatne dla komend administracyjnych w przyszłości).
     *
     * @param playerId UUID gracza
     */
    public void clearCooldown(UUID playerId) {
        activeCooldowns.remove(playerId);
    }
}
