package TP4;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ReseauInteractionService {
    private static final String CLASS_SWITCH = "switch";
    private static final String CLASS_MACHINE = "machine";

    private final JFrame parent;
    private final ReseauLogique reseau;
    private final Runnable refreshUi;

    public ReseauInteractionService(JFrame parent, ReseauLogique reseau, Runnable refreshUi) {
        this.parent = parent;
        this.reseau = reseau;
        this.refreshUi = refreshUi;
    }

    public List<String> getNodeIdsByClass(String nodeClass) {
        List<String> ids = new ArrayList<>();
        reseau.getGraph().nodes().forEach(n -> {
            if (nodeClass.equals(n.getAttribute("ui.class"))) {
                ids.add(n.getId());
            }
        });
        return ids;
    }

    public void ajouterSwitch() {
        String id = genererIdSwitch();
        reseau.ajouterEquipement(id, true);
        refreshUi.run();

        if (reseau.getGraph().getNodeCount() > 1) {
            definirLiaisonsNouveauNoeud(id);
        }
    }

    public void ajouterMachine() {
        String id = genererIdMachine();
        reseau.ajouterEquipement(id, false);
        refreshUi.run();

        if (reseau.getGraph().getNodeCount() > 1) {
            definirLiaisonsNouveauNoeud(id);
        }
    }

    public void lierDeuxSwitchs() {
        List<String> switches = getNodeIdsByClass(CLASS_SWITCH);
        if (switches.size() < 2) {
            JOptionPane.showMessageDialog(parent, "Il faut au moins 2 switches pour créer une liaison.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JComboBox<String> cbNoeud1 = new JComboBox<>();
        JComboBox<String> cbNoeud2 = new JComboBox<>();
        for (String id : switches) {
            cbNoeud1.addItem(id);
            cbNoeud2.addItem(id);
        }

        JSpinner spPoids = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        panel.add(new JLabel("Nœud 1:"));
        panel.add(cbNoeud1);
        panel.add(new JLabel("Nœud 2:"));
        panel.add(cbNoeud2);
        panel.add(new JLabel("Poids:"));
        panel.add(spPoids);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Lier deux nœuds", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String id1 = (String) cbNoeud1.getSelectedItem();
        String id2 = (String) cbNoeud2.getSelectedItem();

        if (id1 == null || id2 == null || id1.equals(id2)) {
            JOptionPane.showMessageDialog(parent, "Les deux nœuds doivent être différents.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int poids = (Integer) spPoids.getValue();
        int port1 = obtenirProchainPort(id1);
        int port2 = obtenirProchainPort(id2);

        try {
            reseau.connecter(id1, id2, poids, port1, port2);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Erreur lors de la connexion: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void supprimerNoeud() {
        if (reseau.getGraph().getNodeCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Aucun nœud à supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> cbNoeud = new JComboBox<>();
        reseau.getGraph().nodes().forEach(n -> cbNoeud.addItem(n.getId()));

        int result = JOptionPane.showConfirmDialog(parent, cbNoeud, "Supprimer un nœud", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = (String) cbNoeud.getSelectedItem();
            if (id != null) {
                reseau.getGraph().removeNode(id);
                refreshUi.run();
            }
        }
    }

    public void afficherTableRoutage(String idNoeud) {
        reseau.colorierChemin(null);
        Node node = reseau.getGraph().getNode(idNoeud);

        if (node == null || !CLASS_SWITCH.equals(node.getAttribute("ui.class"))) {
            JOptionPane.showMessageDialog(parent, "Sélectionnez un switch.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, ReseauLogique.Route> table = reseau.tableRoutage(idNoeud);

        if (table.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Aucune route disponible.", "Table de routage - " + idNoeud, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Object[]> donnees = new ArrayList<>();
        for (Map.Entry<String, ReseauLogique.Route> entry : table.entrySet()) {
            String destination = entry.getKey();
            for (Map.Entry<String, Double> option : entry.getValue().optionsVoisins.entrySet()) {
                donnees.add(new Object[]{destination, option.getKey(), String.format("%.2f", option.getValue())});
            }
        }

        String[] colonnes = {"Destination", "Via (prochain nœud)", "Coût"};
        Object[][] donneesCellules = donnees.toArray(new Object[0][]);

        JTable tableAffiche = new JTable(donneesCellules, colonnes) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableAffiche.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tableAffiche.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableAffiche.setRowHeight(25);
        tableAffiche.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableAffiche.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableAffiche.getColumnModel().getColumn(2).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(tableAffiche);
        JOptionPane.showMessageDialog(parent, scrollPane, "Table de routage - " + idNoeud, JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean afficherCircuitLogique(String source, String destination) {
        if (source == null || destination == null || source.equals(destination)) {
            JOptionPane.showMessageDialog(parent, "Sélectionnez deux nœuds différents.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Node src = reseau.getGraph().getNode(source);
        Node dst = reseau.getGraph().getNode(destination);
        if (src == null || dst == null || !CLASS_MACHINE.equals(src.getAttribute("ui.class")) || !CLASS_MACHINE.equals(dst.getAttribute("ui.class"))) {
            JOptionPane.showMessageDialog(parent, "Le circuit logique doit partir d'une machine et arriver sur une machine.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Path chemin = reseau.plusCourtChemin(source, destination);
        if (chemin == null) {
            JOptionPane.showMessageDialog(parent, "Aucun chemin trouvé entre ces deux nœuds.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        reseau.colorierChemin(chemin);
        return true;
    }

    public void chargerFichier(Consumer<ReseauLogique> onLoaded) {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().endsWith(".dgs");
            }

            @Override
            public String getDescription() {
                return "Fichiers DGS (*.dgs)";
            }
        });

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File fichier = fileChooser.getSelectedFile();
            try {
                ReseauLogique nouveauReseau = new ReseauLogique(fichier);
                onLoaded.accept(nouveauReseau);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Erreur lors du chargement: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String genererIdSwitch() {
        int index = 1;
        while (reseau.getGraph().getNode("Sw" + index) != null) {
            index++;
        }
        return "Sw" + index;
    }

    private String genererIdMachine() {
        int index = 1;
        while (reseau.getGraph().getNode("M" + index) != null) {
            index++;
        }
        return "M" + index;
    }

    private int obtenirProchainPort(String idNoeud) {
        Node node = reseau.getGraph().getNode(idNoeud);
        if (node == null) {
            return 1;
        }

        int maxPort = 0;
        for (Edge edge : (Iterable<Edge>) node.edges()::iterator) {
            Object portObj = edge.getAttribute("port." + idNoeud);
            if (portObj != null) {
                maxPort = Math.max(maxPort, Integer.parseInt(portObj.toString()));
            }
        }
        return maxPort + 1;
    }

    private void definirLiaisonsNouveauNoeud(String idNouveauNoeud) {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("Liaisons pour " + idNouveauNoeud + " (définir les poids):");
        container.add(lblInfo, BorderLayout.NORTH);

        JPanel pnlLiaisons = new JPanel();
        pnlLiaisons.setLayout(new BoxLayout(pnlLiaisons, BoxLayout.Y_AXIS));

        List<String> liaisons = new ArrayList<>();
        Map<String, Integer> poids = new HashMap<>();

        reseau.getGraph().nodes().forEach(n -> {
            if (!n.getId().equals(idNouveauNoeud)) {
                JPanel pnlLiaison = new JPanel();
                pnlLiaison.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

                JCheckBox cbLiaison = new JCheckBox(n.getId());
                JSpinner spPoids = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
                JLabel lblPoids = new JLabel("Poids:");

                pnlLiaison.add(cbLiaison);
                pnlLiaison.add(lblPoids);
                pnlLiaison.add(spPoids);
                pnlLiaisons.add(pnlLiaison);

                cbLiaison.addItemListener(e -> {
                    if (cbLiaison.isSelected()) {
                        liaisons.add(n.getId());
                        poids.put(n.getId(), (Integer) spPoids.getValue());
                    } else {
                        liaisons.remove(n.getId());
                        poids.remove(n.getId());
                    }
                });

                spPoids.addChangeListener(e -> {
                    if (cbLiaison.isSelected()) {
                        poids.put(n.getId(), (Integer) spPoids.getValue());
                    }
                });
            }
        });

        JScrollPane scrollPane = new JScrollPane(pnlLiaisons);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        container.add(scrollPane, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(parent, container, "Définir les liaisons", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            liaisons.forEach(id -> {
                int port1 = obtenirProchainPort(idNouveauNoeud);
                int port2 = obtenirProchainPort(id);
                int pdsLiaison = poids.getOrDefault(id, 1);
                try {
                    reseau.connecter(idNouveauNoeud, id, pdsLiaison, port1, port2);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, "Erreur lors de la liaison: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
}
