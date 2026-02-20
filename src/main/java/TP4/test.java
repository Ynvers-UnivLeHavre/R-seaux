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
		System.out.println("Table de routage pour S2 :");
		var table = netDGS.tableRoutage("S2");
		System.out.print(table);
	}
}
