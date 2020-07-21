/* MachinePlayer.java */

package player;

import java.io.*;
import java.util.*;
import list.*;

/**
 *  An implementation of an automatic Network random player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class RandomPlayer extends Player {


    protected String myName = "random";
    private Board board;
    private int playerColor;
    private int opponentColor;

    /**
     * Creates a random player with the given color.  Color is 0 (black) or 1 (white).
     * @param color
     */
    public RandomPlayer(int color) {
        assert (color == 0 || color == 1):
                "Wrong color: " + color;
        if (color == 0){
            this.playerColor = Board.BLACK;
            this.opponentColor = Board.WHITE;
        }else{
            this.playerColor = Board.WHITE;
            this.opponentColor = Board.BLACK;
        }
        this.board = new Board();
    }

    /**
     * Internally records the move (updates the game board) as a move by "this" player.
     * @return a chosen move by "this" player
     */
    public Move chooseMove() {
        DList validMoves = board.validMoves(playerColor);
        Move chosenMove = new Move();

        if (validMoves.length() == 0){
            return chosenMove;  //Returns a QUIT move if there are no valid moves.
        }

        try {
            chosenMove = (Move) validMoves.nth((int)Math.random()*validMoves.length()).item();
        } catch (InvalidNodeException e) {
            System.out.println("Invalid node when the random player gets a random move");
            e.printStackTrace();
        }
        board.makeMove(chosenMove, playerColor);
        System.out.println(board.getNextPlayer()+"makes move: "+chosenMove);

        return chosenMove;
    }

    // If the Move m is legal, records the move as a move by the opponent
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.  This method allows your opponents to inform you of their moves.
    public boolean opponentMove(Move m) {
        return board.makeMove(m, this.opponentColor);
    }

    // If the Move m is legal, records the move as a move by "this" player
    // (updates the internal game board) and returns true.  If the move is
    // illegal, returns false without modifying the internal state of "this"
    // player.
    public boolean forceMove(Move m) {
        return board.makeMove(m, this.playerColor);
    }
}