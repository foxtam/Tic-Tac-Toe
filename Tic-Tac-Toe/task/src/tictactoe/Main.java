package tictactoe;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        var game = new TicTacToe(TicTacToe.Sign.X);
        System.out.print(game);

        while (game.status() == TicTacToe.Status.NOT_FINISHED) {
            while (true) {
                System.out.print("Enter the coordinates: ");
                try {
                    int column = in.nextInt();
                    int row = in.nextInt();
                    game.setNextSignTo(column, row);
                    break;
                } catch (CellIsAlreadySignedException e) {
                    System.out.println("This cell is occupied! Choose another one!");
                } catch (InputMismatchException e) {
                    System.out.println("You should enter numbers!");
                    in.nextLine();
                } catch (InvalidCoordinatesException e) {
                    System.out.println("Coordinates should be from 1 to 3!");
                }
            }
            System.out.println(game);
        }
        System.out.println(game.status().asText());
    }
}

class CellIsAlreadySignedException extends Exception {
}

class InvalidCoordinatesException extends Exception {
}

class TicTacToe {
    private final Sign[][] field = asTable("_________");
    private Sign nextSign;

    public TicTacToe(Sign initSign) {
        this.nextSign = initSign;
    }

    public void setNextSignTo(int column, int row) throws CellIsAlreadySignedException, InvalidCoordinatesException {
        int fieldRow = 3 - row;
        int fieldColumn = column - 1;

        if (fieldRow < 0 || fieldRow > 2 || fieldColumn < 0 || fieldColumn > 2) {
            throw new InvalidCoordinatesException();
        }

        if (field[fieldRow][fieldColumn] != Sign.EMPTY) {
            throw new CellIsAlreadySignedException();
        }
        field[fieldRow][fieldColumn] = nextSign;
        if (nextSign == Sign.X) nextSign = Sign.O;
        else if (nextSign == Sign.O) nextSign = Sign.X;
    }

    @Override
    public String toString() {
        return asTableView();
    }

    public Status status() {
        if (existOLine()) {
            return Status.O_WINS;
        } else if (existXLine()) {
            return Status.X_WINS;
        } else if (existsEmptyCells()) {
            return Status.NOT_FINISHED;
        } else return Status.DRAW;
    }

    private boolean existsEmptyCells() {
        return countSign(Sign.EMPTY) > 0;
    }

    private boolean existXLine() {
        return countFullLinesForSign(Sign.X) > 0;
    }

    private boolean existOLine() {
        return countFullLinesForSign(Sign.O) > 0;
    }

    private Sign[][] asTable(String line) {
        if (line.length() != 9) {
            throw new IllegalArgumentException();
        }
        int charIndex = 0;
        var matrix = new Sign[3][3];
        for (int i = 0; i < 9; i++) {
            matrix[i / 3][i % 3] = Sign.getByChar(line.charAt(charIndex++));
        }
        return matrix;
    }

    private int countSign(Sign signToCount) {
        int count = 0;
        for (Sign[] row : field) {
            for (Sign sign : row) {
                if (sign == signToCount) count++;
            }
        }
        return count;
    }

    private int countFullLinesForSign(Sign sign) {
        int linesCount = 0;

        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] != sign) continue outer;
            }
            linesCount++;
        }

        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[j][i] != sign) continue outer;
            }
            linesCount++;
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][i] != sign) break;
            else if (i == 2) linesCount++;
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][2 - i] != sign) break;
            else if (i == 2) linesCount++;
        }
        return linesCount;
    }

    private String asTableView() {
        String horizontalLine = "-".repeat(9) + '\n';
        StringBuilder tableView = new StringBuilder(horizontalLine);
        for (int i = 0; i < 3; i++) {
            tableView.append("| ")
                    .append(field[i][0].signChar())
                    .append(' ')
                    .append(field[i][1].signChar())
                    .append(' ')
                    .append(field[i][2].signChar())
                    .append(" |\n");
        }
        tableView.append(horizontalLine);
        return tableView.toString();
    }

    enum Sign {
        X('X'), O('O'), EMPTY('_');

        private final char signChar;

        Sign(char signChar) {
            this.signChar = signChar;
        }

        static Sign getByChar(char ch) {
            for (Sign s : Sign.values()) {
                if (s.signChar == ch) return s;
            }
            throw new IllegalArgumentException();
        }

        public char signChar() {
            return signChar;
        }
    }

    enum Status {
        X_WINS("X wins"),
        O_WINS("O wins"),
        DRAW("Draw"),
        NOT_FINISHED("Game not finished");

        private final String asText;

        Status(String text) {
            this.asText = text;
        }

        public String asText() {
            return asText;
        }
    }
}
