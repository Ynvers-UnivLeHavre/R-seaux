package TP1_NathanADOHO;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class SignalPanel extends JPanel {
	private String signal = "";
	private String type = "NRZ";
	private static final int PADDING = 50;

	public SignalPanel() {
		setPreferredSize(new Dimension(400, 300));
		setBackground(Color.WHITE);
	}

	public void updateSignal(String signal, String type) {
		this.signal = signal;
		this.type = type;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (signal.isEmpty() || type.isEmpty()) {
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();
		int graphWidth = width - 2 * PADDING;
		int graphHeight = height - 2 * PADDING;
		int stepX = graphWidth / signal.length();
		int yLow = height - PADDING - graphHeight / 4;
		int yHigh = PADDING + graphHeight / 4;

		drawGrid(g2d, width, height, stepX, yLow, yHigh);
		drawAxes(g2d, width, height, yLow, yHigh);

		g2d.setStroke(new BasicStroke(3));

		switch (type) {
			case "NRZ":
				tracerNRZ(g2d, stepX, yLow, yHigh);
				break;
			case "Manchester":
				tracerManchester(g2d, stepX, yLow, yHigh);
				break;
			case "Manchester Différentiel":
				tracerManchesterDifferentiel(g2d, stepX, yLow, yHigh);
				break;
			case "Miller":
				tracerMiller(g2d, stepX, yLow, yHigh);
				break;
		}
	}

	private void drawGrid(Graphics2D g2d, int width, int height, int stepX, int yLow, int yHigh) {
		g2d.setColor(new Color(230, 230, 230));
		g2d.setStroke(
				new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 5.0f }, 0.0f));

		// Lignes verticales (séparation des bits)
		for (int i = 0; i <= signal.length(); i++) {
			int x = PADDING + i * stepX;
			g2d.drawLine(x, PADDING, x, height - PADDING);
		}
		// Lignes horizontales (niveaux)
		g2d.drawLine(PADDING, yLow, width - PADDING, yLow);
		g2d.drawLine(PADDING, yHigh, width - PADDING, yHigh);
	}

	private void drawAxes(Graphics2D g2d, int width, int height, int yLow, int yHigh) {
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		g2d.setFont(new Font("Arial", Font.BOLD, 12));

		// Axe Y
		g2d.drawLine(PADDING, PADDING, PADDING, height - PADDING);
		g2d.drawString("+V", PADDING - 25, yHigh + 5);
		g2d.drawString("-V", PADDING - 25, yLow + 5);
		g2d.drawString("Voltage", PADDING - 40, PADDING - 10);

		// Axe X
		g2d.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING);
		g2d.drawString("Temps", width - PADDING, height - PADDING + 20);

		// Labels des bits
		for (int i = 0; i < signal.length(); i++) {
			int x = PADDING + i * (width - 2 * PADDING) / signal.length();
			g2d.drawString(String.valueOf(signal.charAt(i)), x + (width - 2 * PADDING) / signal.length() / 2,
					height - PADDING + 20);
		}
	}

	private void tracerNRZ(Graphics2D g2d, int stepX, int yLow, int yHigh) {
		g2d.setColor(Color.BLUE);
		int currentX = PADDING;
		int currentY = PADDING; // Initialisation arbitraire

		for (int i = 0; i < signal.length(); i++) {
			char bit = signal.charAt(i);
			int targetY = (bit == '0') ? yLow : yHigh;

			if (i == 0) {
				currentY = targetY; // Position initiale correcte
			}

			g2d.drawLine(currentX, currentY, currentX, targetY); // Transition verticale
			g2d.drawLine(currentX, targetY, currentX + stepX, targetY); // Palier horizontal

			currentX += stepX;
			currentY = targetY;
		}
	}

	private void tracerManchester(Graphics2D g2d, int stepX, int yLow, int yHigh) {
		g2d.setColor(Color.RED);
		int currentX = PADDING;
		int halfStep = stepX / 2;

		int prevY = -1;

		for (int i = 0; i < signal.length(); i++) {
			char bit = signal.charAt(i);
			int startY, midY;

			if (bit == '0') {
				startY = yLow;
				midY = yHigh;
			} else {
				startY = yHigh;
				midY = yLow;
			}

			if (i > 0 && prevY != -1) {
				g2d.drawLine(currentX, prevY, currentX, startY);
			}

			g2d.drawLine(currentX, startY, currentX + halfStep, startY);
			g2d.drawLine(currentX + halfStep, startY, currentX + halfStep, midY);
			g2d.drawLine(currentX + halfStep, midY, currentX + stepX, midY);

			prevY = midY;
			currentX += stepX;
		}
	}

	private void tracerManchesterDifferentiel(Graphics2D g2d, int stepX, int yLow, int yHigh) {
		g2d.setColor(Color.GREEN);
		int currentX = PADDING;
		int halfStep = stepX / 2;
		boolean inverse = false; // false = montant, true = descendant au milieu

		for (int i = 0; i < signal.length(); i++) {
			char bit = signal.charAt(i);

			if (bit == '1') {
				// Transition au début si 1
				inverse = !inverse;
			}
		}

		int currentLevel = yLow;

		for (int i = 0; i < signal.length(); i++) {
			char bit = signal.charAt(i);
			boolean isZero = (bit == '0');

			if (isZero) {
				currentLevel = (currentLevel == yLow) ? yHigh : yLow;
				g2d.drawLine(currentX, (currentLevel == yLow) ? yHigh : yLow, currentX, currentLevel);
			}

			g2d.drawLine(currentX, currentLevel, currentX + halfStep, currentLevel);

			int nextLevel = (currentLevel == yLow) ? yHigh : yLow;
			g2d.drawLine(currentX + halfStep, currentLevel, currentX + halfStep, nextLevel);
			g2d.drawLine(currentX + halfStep, nextLevel, currentX + stepX, nextLevel);

			currentLevel = nextLevel;
			currentX += stepX;
		}
	}

	private void tracerMiller(Graphics2D g2d, int stepX, int yLow, int yHigh) {
		g2d.setColor(Color.MAGENTA);
		int currentX = PADDING;
		int halfStep = stepX / 2;

		int currentLevel = yLow;

		for (int i = 0; i < signal.length(); i++) {
			char bit = signal.charAt(i);
			boolean isOne = (bit == '1');
			boolean prevIsOne = (i > 0 && signal.charAt(i - 1) == '1');

			if (!isOne && !prevIsOne && i > 0) {
				int nextLevel = (currentLevel == yLow) ? yHigh : yLow;
				g2d.drawLine(currentX, currentLevel, currentX, nextLevel);
				currentLevel = nextLevel;
			}

			if (isOne) {
				g2d.drawLine(currentX, currentLevel, currentX + halfStep, currentLevel);

				int nextLevel = (currentLevel == yLow) ? yHigh : yLow;
				g2d.drawLine(currentX + halfStep, currentLevel, currentX + halfStep, nextLevel);

				g2d.drawLine(currentX + halfStep, nextLevel, currentX + stepX, nextLevel);
				currentLevel = nextLevel;
			} else {
				g2d.drawLine(currentX, currentLevel, currentX + stepX, currentLevel);
			}

			currentX += stepX;
		}
	}
}
