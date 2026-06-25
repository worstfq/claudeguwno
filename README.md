# Totem Ułaskawienia

Plugin do Paper **1.21.4+** (Java 21). Dodaje przedmiot **Totem Ułaskawienia**, który
w chwili śmierci gracza zostaje zużyty i chroni cały ekwipunek.

## Działanie
Gdy gracz umiera, mając totem w ekwipunku (ręka główna, druga ręka, zbroja lub plecak):
- gracz umiera normalnie (pojawia się ekran śmierci),
- **jeden** totem znika z ekwipunku,
- ekwipunek, zbroja i przedmioty w rękach zostają zachowane,
- poziomy doświadczenia zostają zachowane,
- nic nie wypada na ziemię (brak dropu przedmiotów i XP).

Bez totemu śmierć przebiega standardowo.

## Komenda
```
/totemulaskawienia give [ilość]
```
Aliasy: `/totemu`, `/tu`

## Permisja
```
totemulaskawienia.give   (domyślnie: op)
```

## Konfiguracja (`config.yml`)
- `custom-model-data` – wartość CustomModelData pod własny model w resource packu.
- `glow` – czy przedmiot ma świecić jak zaklęty.

## Budowanie
W katalogu projektu:
```
mvn clean package
```
Gotowy plik powstanie w `target/TotemUlaskawienia-1.0.0.jar`.
W IntelliJ IDEA: *Open* → wskaż `pom.xml` → uruchom *Maven → package*.
