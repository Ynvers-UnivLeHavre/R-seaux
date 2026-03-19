package TP4;


import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.graphstream.graph.Path;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceDGS;

public class ReseauLogique {
	private static final String ATTR_POID = "poid";
	private static final String ATTR_UI_CLASS = "ui.class";
	private static final String ATTR_UI_LABEL = "ui.label";
	private static final String ATTR_UI_STYLE = "ui.style";
	private static final String CLASS_SWITCH = "switch";
	private static final String CLASS_MACHINE = "machine";
	private static final String EDGE_CLASS_DASHED = "dashed";

	private Graph graph;

	public ReseauLogique(String id) {
		System.setProperty("org.graphstream.ui", "swing");
		this.graph = new SingleGraph(id);
		this.appliquerStyle();
	}

	public ReseauLogique(java.io.File fichierDGS) throws IOException {
		this.graph = new SingleGraph("ReseauImporte");
		this.appliquerStyle();

		FileSourceDGS source = new FileSourceDGS();
		source.addSink(this.graph);
		source.readAll(fichierDGS.getAbsolutePath());

		for (Edge e : (Iterable<Edge>) graph.edges()::iterator) {
			Node n1 = e.getNode0();
			Node n2 = e.getNode1();
			
			boolean hasMachine = isMachine(n1) || isMachine(n2);

			Object poid = e.getAttribute(ATTR_POID);
			if (poid != null) {
				double poids = Double.parseDouble(poid.toString());
				e.setAttribute(ATTR_POID, poids);

				if (!hasMachine)
					e.setAttribute(ATTR_UI_LABEL, poid.toString());
				else
					e.setAttribute(ATTR_UI_LABEL, "");
			}

			if (hasMachine) {
				e.setAttribute(ATTR_UI_CLASS, EDGE_CLASS_DASHED);
			}
		}
	}

	private void appliquerStyle() {
		this.graph.setAttribute("ui.stylesheet",
			"node.switch { " +
			"  fill-color: #1976D2; " +
			"  shape: circle; " +
			"  size: 50px; " +
			"  text-color: white; " +
			"  text-size: 13; " +
			"} " +
			"node.machine { " +
			"  fill-color: #FF6F00; " +
			"  shape: box; " +
			"  size: 35px; " +
			"  text-color: white; " +
			"  text-size: 13; " +
			"} " +
			"edge { " +
			"  text-alignment: along; " +
			"  text-size: 14; " +
			"  text-color: #555555; " +
			"  stroke-color: gray; " +
			"  size: 1px; " +
			"} " +
			"edge.dashed { " +
			"  stroke-mode: dashes; " +
			"} " +
			"edge.chemin { " +
			"  stroke-color: green; " +
			"  size: 4px; " +
			"} " +
			"node { " +
			"  text-background-mode: none; " +
			"}"
		);
	}

	public void ajouterEquipement(String id, boolean estSwitch) {
		Node n = graph.addNode(id);
		n.setAttribute(ATTR_UI_LABEL, id);
		n.setAttribute(ATTR_UI_CLASS, estSwitch ? CLASS_SWITCH : CLASS_MACHINE);
	}

	public void connecter(String id1, String id2, int poid, int port1, int port2) {
		Node n1 = graph.getNode(id1);
		Node n2 = graph.getNode(id2);
		if (n1 == null || n2 == null) {
			throw new IllegalArgumentException("Nœud introuvable");
		}
		
		boolean isM1 = isMachine(n1);
		boolean isM2 = isMachine(n2);

		if (isM1 && isM2) {
				System.err.println("Erreur : Impossible de connecter deux machines directement (" + id1 + " <-> " + id2 + ")");
				return;
		}

		int poidEffectif = (isM1 || isM2) ? 0 : poid;

		if (isM1 && n1.getDegree() > 0) {
			System.err.println("Erreur : La machine " + id1 + " est déjà connectée à un switch.");
			return;
		}
		if (isM2 && n2.getDegree() > 0) {
			System.err.println("Erreur : La machine " + id2 + " est déjà connectée à un switch.");
			return;
		}

		Edge e = graph.addEdge(id1 + "-" + id2, id1, id2);
		e.setAttribute(ATTR_POID, poidEffectif);

		if (isM1 || isM2) {
			e.setAttribute(ATTR_UI_LABEL, "");
			e.setAttribute(ATTR_UI_CLASS, EDGE_CLASS_DASHED);
		} else {
			e.setAttribute(ATTR_UI_LABEL, poidEffectif);
		}

		if (isSwitch(this.graph.getNode(id1)))
			e.setAttribute("port." + id1, port1);
		if (isSwitch(this.graph.getNode(id2)))
			e.setAttribute("port." + id2, port2);
	}

	public Map<String, Route> tableRoutage(String idSwitch) {
		Map<String, Route> table = new TreeMap<>();
		Node n = this.graph.getNode(idSwitch);

		if (n == null || !isSwitch(n))
				return table;

		for (Node dest : this.graph) {
			if (dest != n && isSwitch(dest)) {
				Route routeDest = new Route();

				for (Edge e : (Iterable<Edge>) n.edges()::iterator) {
					Node voisin = e.getOpposite(n);

					if (!isSwitch(voisin))
						continue;

					double poidsLienVersVoisin = e.getNumber(ATTR_POID);

					List<Double> poidsOriginaux = new ArrayList<>();
					List<Edge> aretesAdjacentes = new ArrayList<>();
					for (Edge adj : (Iterable<Edge>) n.edges()::iterator) {
						aretesAdjacentes.add(adj);
						poidsOriginaux.add(adj.getNumber(ATTR_POID));
						adj.setAttribute(ATTR_POID, 999999.0); // Coût prohibitif
					}

					Path cheminDepuisVoisin = plusCourtChemin(voisin.getId(), dest.getId());

					for (int i = 0; i < aretesAdjacentes.size(); i++)
						aretesAdjacentes.get(i).setAttribute(ATTR_POID, poidsOriginaux.get(i));

					if (cheminDepuisVoisin != null && cheminDepuisVoisin.getPathWeight(ATTR_POID) < 999999.0) {
						double coutVoisinVersDest = cheminDepuisVoisin.getPathWeight(ATTR_POID);
						double coutTotalParCeVoisin = poidsLienVersVoisin + coutVoisinVersDest;
						routeDest.ajouterOption(voisin.getId(), coutTotalParCeVoisin);
					}
				}

				if (!routeDest.optionsVoisins.isEmpty())
					table.put(dest.getId(), routeDest);
			}
		}
		return table;
}

	public Path plusCourtChemin(String idOrigine, String idDestination) {
		Node source = this.graph.getNode(idOrigine);
		Node dest = this.graph.getNode(idDestination);

		if (source == null || dest == null)
			return null;

		Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, ATTR_POID);
		dijkstra.init(this.graph);
		dijkstra.setSource(source);
		dijkstra.compute();

		return dijkstra.getPath(dest);
	}

	public void colorierOptionsRoutage(String idSource, String idDest) {
		Map<String, Route> routes = tableRoutage(idSource);
		Route r = routes.get(idDest);
		reinitialiserStyle();
		if (r == null) return;

		String[] couleurs = {"blue", "red", "green", "orange", "magenta", "cyan"};
		int i = 0;
		for (String voisin : r.optionsVoisins.keySet()) {
			Edge e = graph.getNode(idSource).getEdgeBetween(voisin);
			if (e != null) {
				e.setAttribute(ATTR_UI_STYLE, "fill-color: " + couleurs[i % couleurs.length] + "; size: 5px;");
				i++;
			}
		}
	}

	public void colorierChemin(Path p) {
		// Réinitialiser d'abord
		for (Edge e : (Iterable<Edge>) graph.edges()::iterator)
			e.removeAttribute(ATTR_UI_STYLE);
		
		if (p == null) {
			return;
		}
		
		// Appliquer le style directement
		for (Edge e : p.getEdgePath()) {
			e.setAttribute(ATTR_UI_STYLE, "fill-color: green; size: 4px;");
		}
	}

	private void reinitialiserStyle() {
		for (Edge e : (Iterable<Edge>) graph.edges()::iterator)
			e.removeAttribute(ATTR_UI_STYLE);
	}

	private boolean isSwitch(Node n) {
		return n != null && CLASS_SWITCH.equals(n.getAttribute(ATTR_UI_CLASS));
	}

	private boolean isMachine(Node n) {
		return n != null && CLASS_MACHINE.equals(n.getAttribute(ATTR_UI_CLASS));
	}
	public static class Route {
		public Map<String, Double> optionsVoisins;

		public Route() {
			this.optionsVoisins = new TreeMap<>();
		}

		public void ajouterOption(String voisin, double coutTotal) {
			this.optionsVoisins.put(voisin, coutTotal);
		}

		@Override
		public String toString() {
			return optionsVoisins.toString();
		}
	}

	public void display() {
		this.graph.display();
	}

	public Graph getGraph() {
		return this.graph;
	}
}
