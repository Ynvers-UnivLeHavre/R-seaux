package TP4;

import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;

public class ItfResaux extends JFrame {
    private static final String CLASS_SWITCH = "switch";
    private static final String CLASS_MACHINE = "machine";

    private final ReseauLogique reseau;
    private final ReseauInteractionService actions;
    private JComboBox<String> cbSource;
    private JComboBox<String> cbDestination;
    private JComboBox<String> cbOperationType;
    private JPanel panelDynamique;
    private ViewPanel viewPanel;

    // Couleurs personnalisées
    private static final Color COULEUR_FOND = new Color(240, 240, 240);
    private static final Color COULEUR_BOUTON = new Color(65, 105, 225);
    private static final Color COULEUR_BOUTON_HOVER = new Color(30, 70, 180);

    public ItfResaux() {
        this(new ReseauLogique("ReseauInteractif"));
    }

    public ItfResaux(ReseauLogique reseau) {
        super("Simulateur de Réseau");
        this.reseau = reseau;
        this.actions = new ReseauInteractionService(this, reseau, this::rafraichirInterfaceApresMutation);
        initialiserInterface();
    }

    private void initialiserInterface() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setBackground(COULEUR_FOND);

        Container content = getContentPane();
        content.setLayout(new BorderLayout(10, 10));
        content.setBackground(COULEUR_FOND);

        // Onglets sur la gauche
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(280, 850));
        tabbedPane.setBackground(COULEUR_FOND);
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));

        tabbedPane.addTab("Fichier", creerOngletFichier());
        tabbedPane.addTab("Outils", creerOngletOutils());
        tabbedPane.addTab("Analyse", creerOngletAnalyse());

        // Graphe au centre
        JPanel panneauGraphe = construirePanneauGraphe();

        content.add(tabbedPane, BorderLayout.WEST);
        content.add(panneauGraphe, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel creerOngletFichier() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(COULEUR_FOND);

        JLabel lbl = new JLabel("Gestion des fichiers");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(creerBouton("📂 Charger fichier", e -> actions.chargerFichier(nouveauReseau ->
            SwingUtilities.invokeLater(() -> {
                dispose();
                new ItfResaux(nouveauReseau);
            })
        )));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(creerBouton("🗑️ Vider tout", e -> {
            reseau.getGraph().clear();
            rafraichirInterfaceApresMutation();
        }));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel creerOngletOutils() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(COULEUR_FOND);

        JLabel lbl = new JLabel("Gestion du réseau");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(creerBouton("➕ Ajouter switch", e -> actions.ajouterSwitch()));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(creerBouton("🔗 Lier deux switchs", e -> actions.lierDeuxSwitchs()));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(creerBouton("➕ Ajouter machine", e -> actions.ajouterMachine()));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(creerBouton("❌ Supprimer", e -> actions.supprimerNoeud()));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel creerOngletAnalyse() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(COULEUR_FOND);

        JLabel lbl = new JLabel("Analyse du réseau");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblOp = new JLabel("Type d'analyse:");
        lblOp.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblOp);

        cbOperationType = new JComboBox<>(new String[]{"Table de routage", "Circuit logique"});
        cbOperationType.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cbOperationType.addActionListener(e -> rafraichirPanelDynamique());
        panel.add(cbOperationType);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panelDynamique = new JPanel();
        panelDynamique.setLayout(new BoxLayout(panelDynamique, BoxLayout.Y_AXIS));
        panelDynamique.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDynamique.setBackground(COULEUR_FOND);
        rafraichirPanelDynamique();

        panel.add(panelDynamique);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel construirePanneauGraphe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        Viewer viewer = new SwingViewer(reseau.getGraph(), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        viewPanel = (ViewPanel) viewer.addDefaultView(false);
        viewPanel.setBackground(Color.WHITE);

        panel.add(viewPanel, BorderLayout.CENTER);
        return panel;
    }

    private JButton creerBouton(String texte, java.awt.event.ActionListener action) {
        JButton bouton = new JButton(texte);
        bouton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        bouton.setFont(new Font("Arial", Font.PLAIN, 11));
        bouton.setBackground(COULEUR_BOUTON);
        bouton.setForeground(Color.WHITE);
        bouton.setFocusPainted(false);
        bouton.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        bouton.addActionListener(action);

        // Effet hover
        bouton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bouton.setBackground(COULEUR_BOUTON_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                bouton.setBackground(COULEUR_BOUTON);
            }
        });

        return bouton;
    }

    private void rechargerNoeudsDansCombos() {
        if (cbSource == null || cbDestination == null) {
            return;
        }

        remplirComboAvecNoeuds(cbSource, CLASS_MACHINE);
        remplirComboAvecNoeuds(cbDestination, CLASS_MACHINE);
    }

    private void remplirComboAvecNoeuds(JComboBox<String> combo, String nodeClass) {
        combo.removeAllItems();
        for (String id : actions.getNodeIdsByClass(nodeClass)) {
            combo.addItem(id);
        }
    }

    private void rafraichirInterfaceApresMutation() {
        rechargerNoeudsDansCombos();
        rafraichirPanelDynamique();
    }

    private void rafraichirPanelDynamique() {
        panelDynamique.removeAll();

        if (cbOperationType.getSelectedIndex() == 0) {
            // Table de routage
            JComboBox<String> cbNoeud = new JComboBox<>();
            for (String id : actions.getNodeIdsByClass(CLASS_SWITCH)) {
                cbNoeud.addItem(id);
            }
            cbNoeud.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            panelDynamique.add(new JLabel("Sélectionner un switch:"));
            panelDynamique.add(cbNoeud);
            panelDynamique.add(Box.createRigidArea(new Dimension(0, 8)));
            panelDynamique.add(creerBouton("📋 Afficher table", e -> actions.afficherTableRoutage((String) cbNoeud.getSelectedItem())));
        } else {
            // Circuit logique
            cbSource = new JComboBox<>();
            cbDestination = new JComboBox<>();
            rechargerNoeudsDansCombos();
            cbSource.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            cbDestination.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            panelDynamique.add(new JLabel("Machine source:"));
            panelDynamique.add(cbSource);
            panelDynamique.add(Box.createRigidArea(new Dimension(0, 6)));
            panelDynamique.add(new JLabel("Machine destination:"));
            panelDynamique.add(cbDestination);
            panelDynamique.add(Box.createRigidArea(new Dimension(0, 8)));
            panelDynamique.add(creerBouton("🛣️ Afficher chemin", e -> {
                if (actions.afficherCircuitLogique((String) cbSource.getSelectedItem(), (String) cbDestination.getSelectedItem())) {
                    viewPanel.repaint();
                }
            }));
        }

        panelDynamique.revalidate();
        panelDynamique.repaint();
    }
}
