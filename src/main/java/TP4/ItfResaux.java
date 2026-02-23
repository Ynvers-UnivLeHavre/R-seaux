package TP4;

import java.awt.*;
import javax.swing.*;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.graph.Path;

public class ItfResaux extends JFrame {
    private ReseauLogique reseau;
    private JComboBox<String> cbSource, cbDest;
    private JRadioButton rbTable, rbShortest;

    public ItfResaux(ReseauLogique reseau) {
        super("Simulateur de Routage - Nathan 2026");
        this.reseau = reseau;
        init();
        setSize(1100, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void init() {
        Container content = getContentPane();
        content.setLayout(new BorderLayout());

        // --- ZONE NORD : CONTRÔLES ---
        JPanel pnlControl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbSource = new JComboBox<>();
        cbDest = new JComboBox<>();
        
        // On remplit avec TOUS les équipements (pour permettre Machine A -> Machine B)
        reseau.getGraph().nodes().forEach(n -> {
            cbSource.addItem(n.getId());
            cbDest.addItem(n.getId());
        });

        rbTable = new JRadioButton("Options Routage (Switchs)", true);
        rbShortest = new JRadioButton("Chemin Logique / PCC");
        ButtonGroup group = new ButtonGroup();
        group.add(rbTable); group.add(rbShortest);

        JButton btnVisualiser = new JButton("Visualiser");
        btnVisualiser.addActionListener(e -> actionCalculer());

        pnlControl.add(new JLabel("Source:")); pnlControl.add(cbSource);
        pnlControl.add(new JLabel("Dest:")); pnlControl.add(cbDest);
        pnlControl.add(rbTable); pnlControl.add(rbShortest);
        pnlControl.add(btnVisualiser);
        content.add(pnlControl, BorderLayout.NORTH);

        // --- ZONE CENTRALE : LE GRAPHE ---
        Viewer viewer = new SwingViewer(reseau.getGraph(), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel view = (ViewPanel) viewer.addDefaultView(false);
        content.add(view, BorderLayout.CENTER);
    }

    private void actionCalculer() {
        String src = (String) cbSource.getSelectedItem();
        String dst = (String) cbDest.getSelectedItem();
        
        if (src.equals(dst)) return;

        if (rbTable.isSelected()) {
            reseau.colorierOptionsRoutage(src, dst);
        } else {
            // Dijkstra gère nativement Machine -> Switch -> Switch -> Machine car lien M-S = poid 0
            Path p = reseau.plusCourtChemin(src, dst);
            reseau.colorierChemin(p);
        }
    }
}