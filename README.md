# DragonSword

Plugin Minecraft (Paper/Spigot 1.21.4+) dodajacy **Smoczy Miecz** — netherytowy miecz,
ktory po kliknieciu PPM wyrzuca Perle Endu i teleportuje gracza. Cooldown: 60 sekund.

## Komendy i uprawnienia

| Element | Wartosc |
|---|---|
| Komenda | `/smoczymiecz` |
| Uprawnienie do otrzymania | `dragonsword.give` (domyslnie: OP) |
| Uprawnienie do uzywania | `dragonsword.use` (domyslnie: wszyscy) |

## Budowanie lokalne

Wymagania: Java 21 + Maven.

```bash
mvn clean package
```

Gotowy plik powstanie w `target/DragonSword.jar`.

## Auto-build na GitHub (GitHub Actions)

Repozytorium zawiera workflow `.github/workflows/build.yml`, ktory buduje plugin automatycznie:

1. Wrzuc projekt na GitHub (push na galaz `main` lub `master`).
2. Wejdz w zakladke **Actions** — build uruchomi sie sam.
3. Po zakonczeniu pobierz `DragonSword.jar` z sekcji **Artifacts** danego buildu.

### Wydanie (Release) z plikiem .jar

Aby utworzyc Release z gotowym plikiem `.jar`, wypchnij tag wersji:

```bash
git tag v1.0.0
git push origin v1.0.0
```

Workflow automatycznie zbuduje plugin i dolaczy `DragonSword.jar` do nowego Release.

## Instalacja na serwerze

Skopiuj `DragonSword.jar` do folderu `plugins/` serwera Paper/Spigot 1.21.4+ i uruchom serwer.
