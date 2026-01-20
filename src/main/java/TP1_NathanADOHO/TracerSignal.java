package TP1_NathanADOHO;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;

public class TracerSignal extends JFrame {
	JLabel texte;
	JTextField signal;
	JComboBox<String> types;
	JButton tracer;
	private SignalPanel signalPanel;

	public TracerSignal() {
		super("TP 1 - By Nathan ADOHO :)");
		init();
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private JPanel panneauTitre() {
		JPanel panneau = new JPanel();
		panneau.setLayout(new BoxLayout(panneau, BoxLayout.X_AXIS));
		panneau.setBorder(new EmptyBorder(15, 15, 15, 15));

		texte = new JLabel("Entrez le signal (binaire) : ");
		texte.setFont(new Font("Arial", Font.BOLD, 13));
		texte.setAlignmentX(Component.CENTER_ALIGNMENT);
		panneau.add(texte);

		panneau.add(Box.createHorizontalStrut(8));

		signal = new JTextField(20);
		signal.setMaximumSize(new Dimension(250, 25));
		signal.setHorizontalAlignment(JTextField.CENTER);
		signal.setAlignmentX(Component.CENTER_ALIGNMENT);
		panneau.add(signal);

		panneau.add(Box.createHorizontalStrut(8));

		String[] encodages = { "NRZ", "Manchester", "Manchester Différentiel", "Miller" };
		types = new JComboBox<>(encodages);
		types.setMaximumSize(new Dimension(250, 25));
		types.setAlignmentX(Component.CENTER_ALIGNMENT);
		((JLabel) types.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		panneau.add(types);

		panneau.add(Box.createHorizontalStrut(15));

		tracer = new JButton("Tracer le graphe");
		tracer.setFont(new Font("Arial", Font.PLAIN, 12));
		tracer.setFocusPainted(false);
		tracer.setCursor(new Cursor(Cursor.HAND_CURSOR));
		tracer.setAlignmentX(Component.CENTER_ALIGNMENT);
		panneau.add(tracer);

		return panneau;
	}

	private void init() {
		Container principal = this.getContentPane();
		principal.add(panneauTitre(), BorderLayout.NORTH);

		signalPanel = new SignalPanel();
		principal.add(signalPanel, BorderLayout.CENTER);

		tracer.addActionListener(e -> {
			String code = signal.getText();
			if (code == null || !code.matches("[01]+")) {
				JOptionPane.showMessageDialog(this,
						"Le signal doit être composé uniquement de 0 et de 1 (ex: 010110) et ne doit pas être vide.",
						"Erreur de Format",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			String type = types.getSelectedItem().toString();
			signalPanel.updateSignal(code, type);
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(TracerSignal::new);
	}
}
