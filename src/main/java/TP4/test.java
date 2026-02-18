package TP4;

import java.io.File;
import java.io.IOException;

public class test {
	public static void main(String args[]) throws IOException {
		ReseauLogique net = new ReseauLogique("Test_Complexe");
		net.ajouterEquipement("Source", false);
		net.ajouterEquipement("C1", true);
		net.ajouterEquipement("C2", true);
		net.ajouterEquipement("C3", true);
		net.ajouterEquipement("C4", true);
		net.ajouterEquipement("Dest", false);

		// net.connecter("Source", "C1", 1);
		// net.connecter("C1", "C2", 10);
		// net.connecter("C2", "Dest", 1);

		// net.connecter("C1", "C3", 1);
		// net.connecter("C3", "C4", 1);
		// net.connecter("C4", "C2", 1);

		net.display();

		net.plusCourtChemin("Source", "Dest");

		// File f = new File("test.dgs");
		// ReseauLogique test2 = new ReseauLogique(f);
		// test2.plusCourtChemin(null, null);
		// test2.display();
	}
}
