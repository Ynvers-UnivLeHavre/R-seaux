package TP4;

import java.io.File;
import java.io.IOException;

public class test {
	public static void main(String args[]) throws IOException {
		// Test manuel existant
		// ReseauLogique net = new ReseauLogique("Test_Complexe");
		// net.ajouterEquipement("Source", false);
		// net.ajouterEquipement("C1", true);
		// net.ajouterEquipement("C2", true);
		// net.ajouterEquipement("C3", true);
		// net.ajouterEquipement("C4", true);
		// net.ajouterEquipement("Dest", false);

		// net.connecter("Source", "C1", 1);
		// net.connecter("C1", "C2", 10);
		// net.connecter("C2", "Dest", 1);

		// net.connecter("C1", "C3", 1);
		// net.connecter("C3", "C4", 1);
		// net.connecter("C4", "C2", 1);

		// net.display();

		// net.plusCourtChemin("Source", "Dest");

		// Nouveau test avec le fichier test.dgs
		File f = new File("src/main/ressources/test.dgs");
		ReseauLogique netDGS = new ReseauLogique(f);
		netDGS.display();
		System.out.println("Plus court chemin de M1 à M5 :");
		System.out.println(netDGS.plusCourtChemin("M1", "M5"));
		System.out.println("Table de routage pour S1 :");
		var table = netDGS.tableRoutage("S1");
		for (var entry : table.entrySet()) {
			System.out.println("Vers " + entry.getKey() + " : prochain saut = " + entry.getValue().prochainSaut + ", coût = " + entry.getValue().cout);
		}
	}
}
