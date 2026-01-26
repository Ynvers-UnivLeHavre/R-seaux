package TP2_NathanADOHO;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class App extends JFrame {

	private JTextField messageBin;
	private JTextField polyGenerateur;
	private JTextArea resultArea;

	public App() {
		super("TP2 - Nathan ADOHO (CRC)");
		init();
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void init() {
		Container content = getContentPane();
		content.setLayout(new BorderLayout(10, 10));
		
		// Panneau de calcul
		JPanel calcPanel = createCalcPanel();
		content.add(calcPanel, BorderLayout.NORTH);
		
		// Panneau de résultats
		resultArea = new JTextArea(12, 40);
		resultArea.setEditable(false);
		resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(resultArea);
		scrollPane.setBorder(new TitledBorder("Résultats"));
		content.add(scrollPane, BorderLayout.CENTER);
	}

	private JPanel createCalcPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new TitledBorder("Paramètres"));
		
		// Grille des inputs
		JPanel inputGrid = new JPanel(new GridLayout(2, 2, 10, 10));
		
		inputGrid.add(new JLabel("Message binaire:"));
		messageBin = new JTextField(15);
		messageBin.setDocument(new BinaryDocument());
		inputGrid.add(messageBin);
		
		inputGrid.add(new JLabel("Polynôme générateur:"));
		polyGenerateur = new JTextField(15);
		polyGenerateur.setDocument(new BinaryDocument());
		inputGrid.add(polyGenerateur);
		
		panel.add(inputGrid, BorderLayout.CENTER);
		
		// Boutons
		JPanel buttonPanel = new JPanel();
		
		JButton calcButton = new JButton("Calculer CRC");
		calcButton.addActionListener(e -> calculerCRC());
		buttonPanel.add(calcButton);
		
		JButton verifyButton = new JButton("Vérifier Message");
		verifyButton.addActionListener(e -> verifierMessage());
		buttonPanel.add(verifyButton);
		
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		return panel;
	}

	private void calculerCRC() {
		try {
			String message = messageBin.getText().trim();
			String generateur = polyGenerateur.getText().trim();
			
			if (message.isEmpty() || generateur.isEmpty()) {
				resultArea.append("[ERREUR] Veuillez remplir tous les champs\n");
				return;
			}
			
			String resultat = CRC.calculerCRC(message, generateur);
			resultArea.append("Message initial: " + message + "\n");
			resultArea.append("Polynôme: " + generateur + "\n");
			resultArea.append("Message + CRC: " + resultat + "\n");
			resultArea.append("---\n");
		} catch (Exception ex) {
			resultArea.append("[ERREUR] " + ex.getMessage() + "\n");
		}
	}

	private void verifierMessage() {
		try {
			String message = messageBin.getText().trim();
			String generateur = polyGenerateur.getText().trim();
			
			if (message.isEmpty() || generateur.isEmpty()) {
				resultArea.append("[ERREUR] Veuillez remplir tous les champs\n");
				return;
			}
			
			boolean valide = CRC.verifierMEssage(message, generateur);
			resultArea.append("Message: " + message + "\n");
			resultArea.append("Polynôme: " + generateur + "\n");
			resultArea.append("Résultat: " + (valide ? "✓ VALIDE" : "✗ CORROMPU") + "\n");
			resultArea.append("---\n");
		} catch (Exception ex) {
			resultArea.append("[ERREUR] " + ex.getMessage() + "\n");
		}
	}

	// Classe interne pour filtrer les caractères et n'accepter que 0 et 1
	static class BinaryDocument extends PlainDocument {
		@Override
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null) {
				return;
			}
			
			// Vérifie que chaque caractère est 0 ou 1
			for (char c : str.toCharArray()) {
				if (c != '0' && c != '1') {
					return; // Rejette silencieusement les caractères invalides
				}
			}
			
			super.insertString(offset, str, attr);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(App::new);
	}
}