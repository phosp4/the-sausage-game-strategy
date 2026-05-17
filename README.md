# Stratégia hry klobásky (The Sausage Game Strategy)

Tento repozitár obsahuje praktickú časť bakalárskej práce s názvom _Stratégia hry klobásky_, realizovanej na Prírodovedeckej fakulte UPJŠ v Košiciach. Cieľom tejto práce bolo nájsť vyhrávajúcu stratégiu pre túto hru, teda spôsob, ako vyhrať, bez ohľadu na silu a schopnosti protihráča.

## O hre
Klobásky sú abstraktná matematická hra pre dvoch hráčov, hraná na ploche pripomínajúcej trojuholníkovú mriežku. Hráči striedavo umiestňujú tvary (tzv. klobásky) označením troch susedných, zatiaľ neobsadených bodov. Hra je deterministická, bez náhodných prvkov a patrí do kategórie nestranných hier. Implementácia zahŕňa grafické rozhranie pre hru dvoch hráčov, ako aj hru proti autonómnemu počítačovému oponentovi využívajúcemu nájdené stratégie.

## Štruktúra repozitára
* **the-sausage-game-v2** – Hlavný projekt. Aktuálna verzia aplikácie s grafickým používateľským rozhraním (LibGDX) a optimalizovaným jadrom pre výpočet stratégií.
* **the-sausage-game** – Staršia verzia projektu slúžiaca ako archív vývoja.
* **cli-java-prototype** – Pôvodný funkčný kód zameraný na textové rozhranie (príkazový riadok).

## Technická realizácia
Praktická časť je postavená na jazyku Java s využitím frameworku LibGDX. Na hľadanie výherných stratégií bol použitý algoritmus minimax s alfa-beta orezávaním a ďalšími optimalizáciami.

## Inštalácia a spustenie
Aplikácia je distribuovaná ako samostatný program, ktorý nevyžaduje žiadnu dodatočnú inštaláciu ani nastavovanie závislostí. Podporované sú systémy Windows, macOS (Intel aj Apple Silicon) a Linux (64-bitové verzie).

1. Prejdite do sekcie **Releases** v tomto repozitári.
2. Stiahnite si archív (.zip) pre váš operačný systém.
3. Extrahujte stiahnutý súbor.
4. Hru spustíte dvojitým kliknutím na spustiteľný súbor v extrahovanom adresári.
