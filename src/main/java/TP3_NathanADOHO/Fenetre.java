package TP3_NathanADOHO;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Fenetre extends JFrame {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> modeComboBox;

    public Fenetre() {
        super("TP Hamming - Calcul et Vérification de Codes");
        init();
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void init() {
        Container content = getContentPane();
        content.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        content.add(mainPanel, BorderLayout.CENTER);

        setSize(600, 500);
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        inputArea = new JTextArea(8, 40);
        inputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        centerPanel.add(createBorderedPanel("Entrée:", inputArea));

        outputArea = new JTextArea(8, 40);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        centerPanel.add(createBorderedPanel("Résultat:", outputArea));

        return centerPanel;
    }

    private JPanel createBorderedPanel(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.add(new JLabel("Opération:"));
        modeComboBox = new JComboBox<>(new String[]{
            "Calculer Code Hamming",
            "Vérifier Code Hamming"
        });
        modePanel.add(modeComboBox);
        bottomPanel.add(modePanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(createButton("Exécuter", e -> executerOperation()));
        buttonPanel.add(createButton("Effacer", e -> effacer()));
        bottomPanel.add(buttonPanel);

        return bottomPanel;
    }

    private JButton createButton(String label, java.awt.event.ActionListener listener) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(120, 35));
        button.addActionListener(listener);
        return button;
    }

    private void executerOperation() {
        int mode = modeComboBox.getSelectedIndex();
        if (mode == 0) {
            calculerHamming();
        } else {
            verifierHamming();
        }
    }

    private void calculerHamming() {
        String input = inputArea.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une suite de bits", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!input.matches("[01]+")) {
            JOptionPane.showMessageDialog(this, "Utilisez uniquement 0 et 1", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String[] result = Hamming.motToCodeAndHamming(input);
            String motFinal = result[0];
            String code = result[1];

            StringBuilder output = new StringBuilder();
            output.append("Message: ").append(input).append("\n\n");
            output.append("Code Hamming ").append(motFinal.length()).append("-").append(input.length()).append(":\n");
            output.append(motFinal).append("\n\n");
            output.append("Bits de parité: ").append(code);

            outputArea.setText(output.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifierHamming() {
        String input = inputArea.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une suite de bits", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!input.matches("[01]+")) {
            JOptionPane.showMessageDialog(this, "Utilisez uniquement 0 et 1", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int r = calculerR(input.length());
        if (Math.pow(2, r) - 1 != input.length()) {
            JOptionPane.showMessageDialog(this, "Longueur invalide. Doit être 2^i - 1 (7, 15, 31, ...)", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean estValide = Hamming.verifHamming(input);
            StringBuilder output = new StringBuilder();
            
            if (estValide) {
                output.append("Code reçu: ").append(input).append("\n\n");
                output.append("✓ Le code est VALIDE\n");
                output.append("Aucune erreur détectée");
            } else {
                output.append("Code reçu: ").append(input).append("\n\n");
                output.append("✗ Le code est INVALIDE\n");
                output.append("Erreur(s) détectée(s)");
            }

            outputArea.setText(output.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void effacer() {
        inputArea.setText("");
        outputArea.setText("");
    }

    private int calculerR(int length) {
        for (int r = 0; ; r++) {
            if (Math.pow(2, r) - 1 >= length) {
                return r;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Fenetre::new);
    }
}
