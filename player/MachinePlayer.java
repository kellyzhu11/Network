/* MachinePlayer.java */

package player;

import java.io.*;
import java.util.*;
import list.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {


  protected String myName = "machine";
  private Board board;
  private int playerColor;
  private int opponentColor;
  public int maxDepth;


  /**
   * Creates a machine player with the given color and search depth = 4.
   * Color is either 0 (black) or 1 (white).
   * @param color
   */
  public MachinePlayer(int color) {
    this(color, 3);
  }

  /**
   * Creates a machine player with the given color and search depth.  Color is
   * either 0 (black) or 1 (white).
   * @param color
   * @param searchDepth
   */
  public MachinePlayer(int color, int searchDepth) {
    assert (color == 0 || color == 1):
            "Wrong color: " + color;
    if (color == 0){
      this.playerColor = Board.BLACK;
      this.opponentColor = Board.WHITE;
    }else{
      this.playerColor = Board.WHITE;
      this.opponentColor = Board.BLACK;
    }
    //reinitialize searchDepth
    this.maxDepth = searchDepth;
    this.board = new Board();
  }

  /**
   * Internally records the move (updates the game board) as a move by "this" player.
   * @return a chosen move by "this" player
   */
  public Move chooseMove() {

    Move chosenMove;
    if (board.getNumMoves() > 20) { // to save playing time
      chosenMove = chooseMove(this.playerColor, -1, 1, maxDepth - 2).move;
    }else{
      chosenMove = chooseMove(this.playerColor, -1, 1, maxDepth).move;
    }

    board.makeMove(chosenMove, this.playerColor);
    return chosenMove;
  }

  /**
   * Performs the minimax search algorithm with alpha-beta pruning to find the
   * best move by "this" player within the specified search depth
   * @param side
   *    the player to make a move
   * @param alpha
   * @param beta
   * @param depth
   * @return a Best object that stores the best Move and its score
   */
  public Best chooseMove(int side, double alpha, double beta, int depth) {
    Best myMove = new Best(); // My best move
    Best reply; // Opponent's best reply
    DList validMoves = board.validMoves(side);
//    System.out.println(board.getNextPlayer()+"'s valid moves are : "+validMoves);
    double boardScore = board.boardEvaluator();
    if (board.getNextPlayer() == Board.BLACK){
      boardScore = -1*board.boardEvaluator();
    }

    if (depth == 0 || boardScore == 1 || boardScore == -1 ) { // has a winner or stop searching
      myMove.score = boardScore;
      return myMove;
    }

    if (validMoves.length() == 0){
      myMove.move = new Move();
      myMove.score = 0;
      return myMove;  //Returns a QUIT move if there are no valid moves.
    }

    if (side == this.playerColor) {
      myMove.score = alpha;
    } else {
      myMove.score = beta;
    }

    try {
      for (DListNode nodeM: validMoves) {
        Move m = (Move)nodeM.item();
        board.makeMove(m, side); // Modifies "this" Grid
        reply = chooseMove(this.opponentColor, alpha, beta, depth-1);
        reply.score *= 0.99; // encourage fewer moves
        board.undoMove(m, side); // Restores "this" Grid
        if (side == this.playerColor && reply.score > myMove.score) {
          myMove.move = m;
          myMove.score = reply.score;
          alpha = reply.score;
        } else if (side == this.opponentColor && reply.score < myMove.score) {
          myMove.move = m;
          myMove.score = reply.score;
          beta = reply.score;
        }
        if (alpha >= beta) { return myMove; }
      }
    }catch (InvalidNodeException e){
      System.out.println("Invalid node when updating myMove");
      e.printStackTrace();
    }

    return myMove;
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


