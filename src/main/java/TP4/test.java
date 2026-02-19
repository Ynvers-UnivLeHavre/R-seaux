package TP4;

import java.io.File;
import java.io.IOException;

public class test {
	public static void main(String args[]) throws IOException {
		File f = new File("src/main/ressources/test.dgs");
		ReseauLogique netDGS = new ReseauLogique(f);
		netDGS.display();
		System.out.println("Plus court chemin de M1 à M5 :");
		System.out.println(netDGS.plusCourtChemin("M1", "M5"));
		System.out.println("Table de routage pour S1 :");
		var table = netDGS.tableRoutage("S2");
		for (var entry : table.entrySet()) {
			System.out.println("Vers " + entry.getKey() + " : prochain saut = " + entry.getValue().prochainSaut + ", coût = " + entry.getValue().cout);
		}
	}
}
