package player;
public class BoardTests {
    public static void main(String[] args) {
        int WHITE = Board.WHITE;
        int BLACK = Board.BLACK;
        int EMPTY = Board.EMPTY;
        Board b = new Board();
        b.addPiece(0, 1, WHITE);
        b.addPiece(2, 1, WHITE);
        b.addPiece(2, 4, WHITE);
        b.addPiece(3, 4, WHITE);
        b.addPiece(5, 2, WHITE);
        b.addPiece(5, 5, WHITE);
        b.addPiece(7, 5, WHITE);
        b.addPiece(4, 2, BLACK);
        b.addPiece(4, 3, BLACK);
        b.addPiece(4, 5, BLACK);
        b.addPiece(4, 7, BLACK);
        b.addPiece(5, 4, BLACK);
        b.addPiece(6, 0, BLACK);
        b.addPiece(3, 1, WHITE);
        b.addPiece(7, 1, WHITE);
        System.out.println("Testing Board.isWin(WHITE), result should be true: " + b.isWin(WHITE));

        Board c = new Board();
        c.addPiece(2, 0, BLACK);
        c.addPiece(2, 2, BLACK);
        c.addPiece(2, 3, BLACK);
        c.addPiece(6, 3, BLACK);
        c.addPiece(2, 6, BLACK);
        c.addPiece(1, 7, BLACK);
        System.out.println("Testing Board.isWin(WHITE), result should be false: " + c.isWin(WHITE));
        System.out.println("Testing Board.isWin(BLACK), result should be false: " + c.isWin(BLACK));

        c.addPiece(6, 2, BLACK);
        System.out.println("Testing Board.isWin(BLACK), result should be true: " + c.isWin(BLACK));
        c.addPiece(4, 2, BLACK);
        c.addPiece(3, 1, WHITE);
        System.out.println(c);
        System.out.println("Testing Board.isWin(WHITE), result should be false: " + c.isWin(WHITE));
        System.out.println("Testing Board.isWin(BLACK), result should be false: " + c.isWin(BLACK));

        c.addPiece(4, 3, BLACK);
        c.addPiece(0, 4, WHITE);
        c.addPiece(3, 4, WHITE);
        c.addPiece(5, 1, WHITE);
        c.addPiece(5, 5, WHITE);
        c.addPiece(7, 3, WHITE);
        System.out.println("Testing Board.isWin(otherPlayer), result should be true: "
                + c.isWin(c.otherPlayer(c.getNextPlayer())));
    }
}