package TP5.Java_version.Exercice_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TicTacToeServer {
    private enum Mode { RANDOM, PVP }

    private static final int DEFAULT_PORT = 6000;
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    private final Mode mode;
    private final int port;
    private final Object lock = new Object();
    private PlayerConnection waitingPlayer;

    public TicTacToeServer(Mode mode, int port) {
        this.mode = mode;
        this.port = port;
    }

    public static void main(String[] args) {
        String modeArg = args.length > 0 ? args[0].trim().toLowerCase() : "random";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        Mode mode;
        if ("pvp".equals(modeArg)) {
            mode = Mode.PVP;
        } else if ("random".equals(modeArg)) {
            mode = Mode.RANDOM;
        } else {
            System.err.println("Usage: java TicTacToeServer [random|pvp] [port]");
            return;
        }

        new TicTacToeServer(mode, port).start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur Morpion lance en mode " + mode + " sur le port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                PlayerConnection player = PlayerConnection.fromSocket(socket, "Joueur-" + NEXT_ID.getAndIncrement());

                if (mode == Mode.RANDOM) {
                    new Thread(() -> runRandomSession(player), "random-session-" + player.name).start();
                } else {
                    handlePvpQueue(player);
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur: " + e.getMessage());
        }
    }

    private void handlePvpQueue(PlayerConnection player) {
        synchronized (lock) {
            if (waitingPlayer == null) {
                waitingPlayer = player;
                player.send("INFO En attente d'un second joueur...");
                player.send("INFO Vous serez X au prochain match.");
            } else {
                PlayerConnection first = waitingPlayer;
                waitingPlayer = null;
                new Thread(() -> runPvpSession(first, player), "pvp-session-" + first.name + "-" + player.name).start();
            }
        }
    }

    private void runRandomSession(PlayerConnection player) {
        Random random = new Random();
        TicTacToeGame game = new TicTacToeGame();
        char human = 'X';
        char bot = 'O';

        try {
            player.send("WELCOME " + human + " MODE RANDOM");
            player.send("INFO Jouez en envoyant: MOVE <case>, avec case de 1 a 9.");

            while (true) {
                player.send("BOARD " + game.boardAsProtocolString());
                player.send("YOUR_TURN");

                Integer move = readMove(player, game);
                if (move == null) {
                    return;
                }
                game.playMove(move, human);

                if (game.hasWinner(human)) {
                    player.send("BOARD " + game.boardAsProtocolString());
                    player.send("RESULT WIN");
                    return;
                }
                if (game.isDraw()) {
                    player.send("BOARD " + game.boardAsProtocolString());
                    player.send("RESULT DRAW");
                    return;
                }

                List<Integer> available = game.availableMoves();
                int botMove = available.get(random.nextInt(available.size()));
                game.playMove(botMove, bot);
                player.send("INFO Le serveur joue en case " + (botMove + 1));

                if (game.hasWinner(bot)) {
                    player.send("BOARD " + game.boardAsProtocolString());
                    player.send("RESULT LOSE");
                    return;
                }
                if (game.isDraw()) {
                    player.send("BOARD " + game.boardAsProtocolString());
                    player.send("RESULT DRAW");
                    return;
                }
            }
        } finally {
            player.close();
        }
    }

    private void runPvpSession(PlayerConnection p1, PlayerConnection p2) {
        TicTacToeGame game = new TicTacToeGame();
        PlayerConnection current = p1;
        PlayerConnection other = p2;
        char currentSymbol = 'X';
        char otherSymbol = 'O';

        try {
            p1.send("WELCOME X MODE PVP");
            p2.send("WELCOME O MODE PVP");
            p1.send("INFO Match trouve contre " + p2.name);
            p2.send("INFO Match trouve contre " + p1.name);

            while (true) {
                String board = game.boardAsProtocolString();
                current.send("BOARD " + board);
                other.send("BOARD " + board);

                current.send("YOUR_TURN");
                other.send("INFO Tour adverse...");

                Integer move = readMove(current, game);
                if (move == null) {
                    other.send("RESULT WIN_FORFEIT");
                    return;
                }
                game.playMove(move, currentSymbol);

                if (game.hasWinner(currentSymbol)) {
                    String endBoard = game.boardAsProtocolString();
                    current.send("BOARD " + endBoard);
                    other.send("BOARD " + endBoard);
                    current.send("RESULT WIN");
                    other.send("RESULT LOSE");
                    return;
                }
                if (game.isDraw()) {
                    String endBoard = game.boardAsProtocolString();
                    current.send("BOARD " + endBoard);
                    other.send("BOARD " + endBoard);
                    current.send("RESULT DRAW");
                    other.send("RESULT DRAW");
                    return;
                }

                PlayerConnection tmpPlayer = current;
                current = other;
                other = tmpPlayer;

                char tmpSymbol = currentSymbol;
                currentSymbol = otherSymbol;
                otherSymbol = tmpSymbol;
            }
        } finally {
            p1.close();
            p2.close();
        }
    }

    private Integer readMove(PlayerConnection player, TicTacToeGame game) {
        while (true) {
            String line = player.readLine();
            if (line == null) {
                return null;
            }

            String trimmed = line.trim();
            if (!trimmed.toUpperCase().startsWith("MOVE ")) {
                player.send("ERROR Commande invalide. Format attendu: MOVE <1-9>");
                player.send("YOUR_TURN");
                continue;
            }

            String[] parts = trimmed.split("\\s+");
            if (parts.length != 2) {
                player.send("ERROR Format attendu: MOVE <1-9>");
                player.send("YOUR_TURN");
                continue;
            }

            int caseNumber;
            try {
                caseNumber = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                player.send("ERROR Case invalide.");
                player.send("YOUR_TURN");
                continue;
            }

            int index = caseNumber - 1;
            if (index < 0 || index > 8) {
                player.send("ERROR La case doit etre entre 1 et 9.");
                player.send("YOUR_TURN");
                continue;
            }
            if (!game.availableMoves().contains(index)) {
                player.send("ERROR Case deja occupee.");
                player.send("YOUR_TURN");
                continue;
            }
            return index;
        }
    }

    private static final class PlayerConnection {
        private final Socket socket;
        private final BufferedReader in;
        private final PrintWriter out;
        private final String name;

        private PlayerConnection(Socket socket, BufferedReader in, PrintWriter out, String name) {
            this.socket = socket;
            this.in = in;
            this.out = out;
            this.name = name;
        }

        static PlayerConnection fromSocket(Socket socket, String defaultName) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            return new PlayerConnection(socket, in, out, defaultName);
        }

        void send(String line) {
            out.println(line);
        }

        String readLine() {
            try {
                return in.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        void close() {
            try {
                socket.close();
            } catch (IOException ignored) {
                // no-op
            }
        }
    }
}
