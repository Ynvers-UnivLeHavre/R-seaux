package TP5.Java_version.Exercice_2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TicTacToeClient {
    private static final int DEFAULT_PORT = 6000;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connecte au serveur " + host + ":" + port);

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("BOARD ")) {
                    String board = line.substring("BOARD ".length());
                    printBoard(board);
                } else if (line.equals("YOUR_TURN")) {
                    System.out.print("Votre coup (1-9): ");
                    String input = scanner.nextLine().trim();
                    out.println("MOVE " + input);
                } else if (line.startsWith("WELCOME ")) {
                    System.out.println(line);
                } else if (line.startsWith("INFO ")) {
                    System.out.println(line.substring("INFO ".length()));
                } else if (line.startsWith("ERROR ")) {
                    System.out.println("Erreur: " + line.substring("ERROR ".length()));
                    System.out.print("Votre coup (1-9): ");
                    String input = scanner.nextLine().trim();
                    out.println("MOVE " + input);
                } else if (line.startsWith("RESULT ")) {
                    String result = line.substring("RESULT ".length());
                    System.out.println("Resultat: " + result);
                    break;
                } else {
                    System.out.println(line);
                }
            }

            System.out.println("Connexion fermee.");
        } catch (Exception e) {
            System.err.println("Erreur client: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printBoard(String board) {
        if (board.length() != 9) {
            System.out.println("Plateau: " + board);
            return;
        }

        char[] cells = board.toCharArray();
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] == '-') {
                cells[i] = Character.forDigit(i + 1, 10);
            }
        }

        System.out.println();
        System.out.println(" " + cells[0] + " | " + cells[1] + " | " + cells[2]);
        System.out.println("---+---+---");
        System.out.println(" " + cells[3] + " | " + cells[4] + " | " + cells[5]);
        System.out.println("---+---+---");
        System.out.println(" " + cells[6] + " | " + cells[7] + " | " + cells[8]);
        System.out.println();
    }
}
