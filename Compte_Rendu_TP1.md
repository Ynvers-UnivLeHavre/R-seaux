# Compte-Rendu : TP1 Réseaux - Visualisation de Signaux

**Auteur :** Nathan ADOHO
**Matière :** Réseaux
**Date :** 19 Janvier 2026

---

## 1. Rappel du sujet

L'objectif de ce Travail Pratique (TP) était de concevoir et réaliser une application graphique permettant de visualiser différents types de codages en ligne utilisés dans la transmission de données numériques.

L'application devait être capable de :
*   Prendre en entrée une séquence binaire (composée de 0 et de 1).
*   Proposer différents types d'encodage :
    *   **NRZ (Non-Return-to-Zero)**
    *   **Manchester**
    *   **Manchester Différentiel**
    *   **Miller**
*   Tracer le graphe correspondant au signal électrique généré par l'encodage choisi.

## 2. Analyse du sujet

Pour répondre à ce besoin, nous avons identifié la nécessité de créer une Interface Graphique Utilisateur (GUI) conviviale.

**Les besoins fonctionnels identifiés sont :**
*   **Zone de saisie** : Un champ texte pour entrer la chaîne binaire.
*   **Sélection du codage** : Un menu déroulant (`ComboBox`) pour choisir l'algorithme de tracé.
*   **Validation** : Un système pour s'assurer que l'utilisateur n'entre que des '0' et des '1'.
*   **Zone d'affichage** : Un panneau graphique dynamique capable de dessiner des lignes representant les niveaux de tension en fonction du temps.

**Analyse des signaux à implémenter :**
*   **NRZ** : Le signal reste à un niveau bas pour '0' et haut pour '1' (ou inversement) durant toute la durée du bit.
*   **Manchester** : Il y a toujours une transition au milieu du bit. '0' provoque une transition montante, '1' une transition descendante.
*   **Manchester Différentiel** : Transition systématique au milieu du bit. La présence d'une transition ou non au *début* du bit dépend de la valeur du bit (codage par transition).
*   **Miller** : Transition au milieu du bit pour '1'. Pas de transition pour '0', sauf si deux '0' se suivent (transition entre les deux).

## 3. Choix techniques effectués

### 3.1 Langage et Environnement
Le choix s'est porté sur le langage **Java**, en utilisant la bibliothèque standard **Swing** pour l'interface graphique. Ce choix se justifie par la portabilité de Java (l'application fonctionne sous Windows, Linux et macOS sans modification) et la robustesse de Swing pour créer des applications de bureau légères.

### 3.2 Architecture du Code
Le projet a été structuré en deux classes principales pour séparer la logique d'affichage et l'interface de contrôle :

1.  **`TracerSignal.java` (La Fenêtre Principale)** :
    *   Hérite de `JFrame`.
    *   Gère la disposition des composants (boutons, champs de texte) via des `LayoutManagers` (`BorderLayout`, `BoxLayout`).
    *   Implémente la gestion des événements : lorsque l'utilisateur clique sur "Tracer", c'est cette classe qui récupère les données et demande la mise à jour du graphique.
    *   Intègre une validation via Regex (`[01]+`) pour empêcher les entrées invalides et afficher une `JOptionPane` d'erreur si nécessaire.

2.  **`SignalPanel.java` (Le Composant de Dessin)** :
    *   Hérite de `JPanel`.
    *   Surcharge la méthode `paintComponent(Graphics g)` pour effectuer le dessin personnalisé.
    *   Utilise l'API **Graphics2D** pour des fonctionnalités avancées :
        *   **Anti-aliasing** (`RenderingHints`) pour des traits lisses.
        *   **Stroke** (`BasicStroke`) pour gérer l'épaisseur des traits.
    *   Calcule dynamiquement les positions (X, Y) en fonction de la taille de la fenêtre, rendant l'application redimensionnable ("Responsive").

### 3.3 Implémentation des Algorithmes de Tracé
Chaque codage possède sa propre méthode dédiée (`tracerNRZ`, `tracerManchester`, etc.) dans la classe `SignalPanel`.
Le dessin se fait segment par segment. Une boucle parcourt la chaîne binaire et détermine les coordonnées de départ et d'arrivée de chaque ligne.
Exemple de logique pour **NRZ** :
```java
int targetY = (bit == '0') ? yLow : yHigh;
g2d.drawLine(currentX, currentY, currentX, targetY); // Front vertical
g2d.drawLine(currentX, targetY, currentX + stepX, targetY); // Palier horizontal
```

## 4. Résultats et tests

L'application finale présente une interface propre avec un panneau de configuration en haut et la zone de tracé au centre.

### Scénarios de Test
Nous avons testé l'application avec la chaîne : `010110`

1.  **Test NRZ** :
    *   On observe bien les niveaux stables correspondant aux bits.
    *   Le tracé correspond à la théorie : 0 (bas), 1 (haut).

2.  **Test Manchester** :
    *   Chaque intervalle de bit possède une transition en son centre.
    *   Les transitions correspondent : 0 (montant), 1 (descendant).

3.  **Test Gestion d'erreur** :
    *   Entrée : "01234" -> Une boîte de dialogue d'erreur s'affiche correctement ("Le signal doit être composé uniquement de 0 et de 1").
    *   Entrée vide -> Erreur signalée.

L'affichage est fluide et le graphique s'adapte correctement lorsque l'on redimensionne la fenêtre. Une grille en arrière-plan facilite la lecture des bits.

## 5. Conclusion

Ce TP a permis de mettre en pratique les connaissances théoriques sur le codage en ligne tout en développant une application graphique concrète.

**Difficultés rencontrées :**
*   La gestion des coordonnées dans `Graphics2D` demande de la rigueur (inversion de l'axe Y : le point (0,0) est en haut à gauche).
*   L'implémentation de la logique du codage Miller et Manchester Différentiel a nécessité une attention particulière pour gérer l'état précédent (mémoire du dernier niveau de tension).

**Limites du programme :**
*   Si la chaîne binaire est très longue (ex: > 100 bits), le graphe devient illisible car tout est compressé dans la fenêtre. Il n'y a pas de barre de défilement horizontale.
*   L'échelle de temps est relative (pas d'unités précises en microsecondes).

**Perspectives d'évolution :**
*   Ajouter une barre de défilement (Scrollbar) pour supporter des signaux longs.
*   Implémenter d'autres codages comme AMI ou HDB3.
*   Ajouter une fonctionnalité d'export du graphique en image (PNG/JPG).
