package TP5.Java_version.Exercice_2;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeGame {
    private final char[] board = new char[9];

    public TicTacToeGame() {
        for (int i = 0; i < board.length; i++) {
            board[i] = ' ';
        }
    }

    public boolean playMove(int index, char symbol) {
        if (index < 0 || index > 8) {
            return false;
        }
        if (board[index] != ' ') {
            return false;
        }
        board[index] = symbol;
        return true;
    }

    public boolean hasWinner(char symbol) {
        int[][] lines = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6}
        };
        for (int[] line : lines) {
            if (board[line[0]] == symbol && board[line[1]] == symbol && board[line[2]] == symbol) {
                return true;
            }
        }
        return false;
    }

    public boolean isDraw() {
        if (hasWinner('X') || hasWinner('O')) {
            return false;
        }
        for (char c : board) {
            if (c == ' ') {
                return false;
            }
        }
        return true;
    }

    public List<Integer> availableMoves() {
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == ' ') {
                moves.add(i);
            }
        }
        return moves;
    }

    public String boardAsProtocolString() {
        StringBuilder sb = new StringBuilder(9);
        for (char c : board) {
            sb.append(c == ' ' ? '-' : c);
        }
        return sb.toString();
    }
}
