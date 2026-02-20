
# TP Réseaux — Licence 3 Informatique

Ce dépôt regroupe les travaux pratiques réalisés dans le cadre du cours de Réseaux Informatiques (année 2025-2026).

## Sommaire
- [Présentation](#présentation)
- [Structure du projet](#structure-du-projet)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Auteurs](#auteurs)

## Présentation

Ce projet contient plusieurs TPs illustrant des concepts fondamentaux des réseaux informatiques :

- **TP1 : Traitement et visualisation de signaux**
   - Génération, affichage et manipulation de signaux numériques.
   - Visualisation graphique avec Java (Swing).

- **TP2 : Code de Redondance Cyclique (CRC)**
   - Implémentation d’un algorithme CRC pour la détection d’erreurs dans les transmissions.
   - Interface graphique Java (Swing) pour tester le calcul et la vérification de CRC.

- **TP3 : Code de Hamming**
   - Encodage et décodage de messages binaires avec correction automatique d’erreur (1 bit).
   - Interface graphique Java (Swing) pour manipuler le code de Hamming.

- **TP4 : Routage et Réseaux Logiques**
   - Simulation de réseaux, calcul de plus courts chemins, table de routage, etc.

Chaque TP est accompagné d’un rapport détaillé (LaTeX).

## Structure du projet

```
src/
   main/java/TP1/   # Traitement et visualisation de signaux
   main/java/TP2/   # CRC et interface graphique
   main/java/TP3/   # Hamming et interface graphique
   main/java/TP4/   # Routage logique
   ressources/      # Fichiers de test (ex: test.dgs)
   test/java/TP1/   # Tests unitaires éventuels
pom.xml            # Projet Maven
README.md          # Ce fichier
```

## Installation

Ce projet utilise **Java** (>= 11) et **Maven**.

1. Cloner le dépôt :
   ```bash
   git clone https://www-apps.univ-lehavre.fr/forge/an253439/tps_reeaux.git
   cd tps_reeaux
   ```
2. Compiler le projet :
   ```bash
   mvn clean install
   ```

## Utilisation
### Lancer l’interface de visualisation (TP1)
```bash
mvn exec:java -Dexec.mainClass="TP1.TracerSignal"
```

### Lancer l’interface CRC (TP2)
```bash
mvn exec:java -Dexec.mainClass="TP2.App"
```

### Lancer l’interface Hamming (TP3)
```bash
mvn exec:java -Dexec.mainClass="TP3.Fenetre"
```

### Lancer un test de routage (TP4)
```bash
mvn exec:java -Dexec.mainClass="TP4.test"
```

## Auteurs

- Nathan ADOHO

Encadrant : Claude Duvallet, Université Le Havre Normandie
