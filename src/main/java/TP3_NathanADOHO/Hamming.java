package TP3_NathanADOHO;

public class Hamming {
    protected static String[] motToCodeAndHamming(String mot) {
        int r;
        int j = 0;
        int k = 0;
        int m = mot.length();
        String[] motFinal;
        String code = "";
        // Calculer le nombre de bits de parité nécessaires
        for (r = 0; ; r++) {
            if (Math.pow(2, r) >= m + r + 1)
                break;
        }
        // Placer les bits de données et les bits de parité
        motFinal = new String[m + r];
        for (int i = 1; i <= motFinal.length; i++) {
            if (Math.pow(2, k) == i) {
                motFinal[i - 1] = "0";
                k++;
            } 
            else {
                motFinal[i - 1] = String.valueOf(mot.charAt(j));
                j++;
            }
        }
        // Calculer les bits de parité
        for (int i = 0; i < r; i++) {
            int posParite = (int) Math.pow(2, i);
            int bitValeur = 0;

            for (int a= 1; a <= motFinal.length; a++) {
                if (((a >> i) & 1) == 1)
                    if (a != posParite)
                        bitValeur ^= Integer.parseInt(motFinal[a - 1]);
            }
            motFinal[posParite - 1] = String.valueOf(bitValeur);
        }
        // Construire le code à adjoindre final
        k = 0;
        for (int i = 0; i < motFinal.length; i++) {
            if (Math.pow(2, k) == i + 1) {
                code += motFinal[i] ;
                k++;
            }
        }

        return new String[]{
            String.join("", motFinal),
            code
        };
    }

    protected static boolean verifHamming(String codeHamming) {
        int syndrome = 0;
        int r;
        // Calculer le nombre de bits de parité
        for (r = 0; ; r++) {
            if (Math.pow(2, r) >= codeHamming.length() + 1)
                break;
        }
        // Calculer le syndrome
        for (int i = 0; i < r; i++) {
            int posParite = (int) Math.pow(2, i);
            int bitValeur = 0;

            for (int a= 1; a <= codeHamming.length(); a++) {
                if (((a >> i) & 1) == 1)
                    if (a != posParite)
                        bitValeur ^= Integer.parseInt(String.valueOf(codeHamming.charAt(a - 1)));
            }
            bitValeur ^= Integer.parseInt(String.valueOf(codeHamming.charAt(posParite - 1)));
            syndrome += bitValeur * posParite;
        }
        // Vérifier si le syndrome est nul
        return syndrome == 0;
    }
}
