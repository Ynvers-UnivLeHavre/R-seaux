package TP4;


import java.io.IOException;
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

		for (Edge e : this.graph.getEdgeSet()) {
			Node n1 = e.getNode0();
			Node n2 = e.getNode1();
			
			boolean hasMachine = "machine".equals(n1.getAttribute("ui.class")) || 
								 "machine".equals(n2.getAttribute("ui.class"));

			Object poid = e.getAttribute("poid");
			if (poid != null) {
				double var = Double.parseDouble(poid.toString());
				e.setAttribute("poid", var);

				if (!hasMachine)
					e.setAttribute("ui.label", poid.toString());
				else
					e.setAttribute("ui.label", "");
			}
		}
	}

	private void appliquerStyle() {
		this.graph.setAttribute("ui.stylesheet",
			"node.machine { fill-color: blue; size: 20px; } " + 
			"node.switch { fill-color: red; shape: box; size: 30px; } " + 
			"edge { text-alignment: along; text-size: 15; } " +
			"node { text-size: 15; text-background-mode: plain; }"
		);
	}

	public void ajouterEquipement(String id, boolean estSwitch) {
		Node n = graph.addNode(id);
		n.setAttribute("ui.label", id);
		n.setAttribute("ui.class", estSwitch ? "switch" : "machine");
	}

	public void connecter(String id1, String id2, int poid, int port1, int port2) {
		Node n1 = graph.getNode(id1);
		Node n2 = graph.getNode(id2);
		
		boolean isM1 = "machine".equals(n1.getAttribute("ui.class"));
		boolean isM2 = "machine".equals(n2.getAttribute("ui.class"));

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
		e.setAttribute("poid", poidEffectif);

		if (isM1 || isM2)
			e.setAttribute("ui.label", "");
		else 
			e.setAttribute("ui.label", poidEffectif);

		if ("switch".equals(this.graph.getNode(id1).getAttribute("ui.class")))
			e.setAttribute("port." + id1, port1);
		if ("switch".equals(this.graph.getNode(id2).getAttribute("ui.class")))
			e.setAttribute("port." + id2, port2);
	}

		public Map<String, Route> tableRoutage(String idSwitch) {
		Map<String, Route> table = new TreeMap<>();
		Node n = this.graph.getNode(idSwitch);

		if (n == null || !"switch".equals(n.getAttribute("ui.class")))
			return table;

		for (Node dest : this.graph) {
			if (dest != n && "switch".equals(dest.getAttribute("ui.class"))) {
				Path p = plusCourtChemin(n.getId(), dest.getId());

				if (p != null && p.size() > 0) {
					String nextHop = p.getNodePath().get(1).getId();
					double coutTotal = p.getPathWeight("poid");
					table.put(dest.getId(), new Route(nextHop, coutTotal));
				}
			}
		}
		return table;
	}

	public Path plusCourtChemin(String idOrigine, String idDestination) {
		Node source = this.graph.getNode(idOrigine);
		Node dest = this.graph.getNode(idDestination);

		if (source == null || dest == null)
			return null;

		Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "poid");
		dijkstra.init(this.graph);
		dijkstra.setSource(source);
		dijkstra.compute();

		return dijkstra.getPath(dest);
	}

	public class Route {
		public String prochainSaut;
		public double cout;

		public Route(String prochainSaut, double cout) {
			this.prochainSaut = prochainSaut;
			this.cout = cout;
		}
	}

	public void display() {
		this.graph.display();
	}
}
