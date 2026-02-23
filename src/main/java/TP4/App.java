package TP4;

import javax.swing.SwingUtilities;
import java.io.File;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Charge ton fichier DGS (vérifie bien le chemin)
                ReseauLogique reseau = new ReseauLogique(new File("src/main/ressources/test.dgs"));
                new ItfResaux(reseau);
            } catch (Exception e) {
                e.printStackTrace();
                // Alternative : création manuelle si le fichier n'est pas trouvé
                ReseauLogique reseau = new ReseauLogique("ReseauTest");
                // Ajoutez vos équipements et connexions ici pour tester
                new ItfResaux(reseau);
            }
        });
    }
}