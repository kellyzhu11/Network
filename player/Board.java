package player;

import java.util.Hashtable;
import list.*;

/** The Board class represent the game board
 *
 */
public class Board {
    private Piece[][] pieces;
    private int length;
    private int numMoves = 0;
    private static int nextPlayer;//the next player's color, 1 or 0

    // Constants for color, same as Piece class
    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public static final int EMPTY = -1;

    public static final int[][] DIRECTIONS= new int[][]{{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0},
                                                        {-1, 1}, {0, 1}, {1, 1}};


    /**
     * Create a new 8*8 empty board
     */
    public Board(){
        this(8);
    }

    /**
     * Create a new square empty board
     * @param length
     *          length of the board
     * */
    public Board(int length){
        nextPlayer = WHITE;
        numMoves = 0;
        pieces = new Piece[length][length];
        for (int i = 0; i < length; i++){
            for (int j = 0; j < length; j++){
                pieces[i][j] = new Piece(i, j, EMPTY);
            }
        }

        this.length = length;
        System.out.println("Starting the new game...");

    }

    /**
     * Returns the row/column length of the board.
     */
    protected int getLength(){
        return length;
    }

    /**
     * Returns the color or next player
     */
    protected static int otherPlayer(int player) {
        assert (player == WHITE || player == BLACK):
                "Wrong value of player (should be 0 or 1):" + player;
        if (player == WHITE) {
            return BLACK;
        }
        return WHITE;
    }

    /**
     * Returns the piece at position (x, y).
     * @param x
     *   the x-coordinate of the piece
     * @param y
     *   the y-coordinate of the piece
     *
     * @return the piece at position (x, y)
     */
    protected Piece getPiece(int x, int y){
        if(inBound(x, y)){
            return pieces[x][y];
        }
        return null;
    }

    /* ============================== EVALUATION MODULE ===================================*/
    /**
     * Evaluates intermediate boards by scoring players:
     *
     * Scores are given by...
     *      the number of edges on the board
     *
     * Scores are rescaled from -1 to 1
     * Evaluates a board on a scale from -1 to 1:
     * -1 means black has a winning network
     * 1 means white has a winning network
     *
     * @return this board's evaluation
     */
    public double boardEvaluator() {
        if (isWin(WHITE)) {
            return 1;
        }
        if (isWin(BLACK)) {
            return -1;
        }
        int whiteScore = 0;
        int blackScore = 0;

        int[] conn = findAllConnections();
        whiteScore += conn[0];
        blackScore += conn[1];

        if (whiteScore == 0 && blackScore == 0) {
            return 0;
        }
        return .99 * ((double) (whiteScore - blackScore)) / ((double) (whiteScore + blackScore));
    }

    /**
     * Returns information about the connections (two pieces connected in
     * an orthogonal or diagonal direction not blocked by an opponent's
     * piece) currently on the board.
     *
     * @return [# white conections, # black connections]
     */
    protected int[] findAllConnections() {
        //dictionary respresented by an array of DLists where keys (first entry) are the Piece, and entries are Pieces it's connected to
        DList[] connDict = new DList[java.lang.Math.min(getNumMoves(), 20)];
        int dictInd = 0;
        for (int i = 0; i < getLength(); i++) {
            for (int j = 0; j < getLength(); j++) {
                if (inBound(i, j) && getPiece(i, j).getColor() != EMPTY) {
                    connDict[dictInd] = findPieceConnections(getPiece(i, j));
                    connDict[dictInd].insertFront(getPiece(i,j));
                    dictInd++;
                }
            }
        }
        int whiteTotal = 0, blackTotal = 0;
        for (DList conn:connDict) {
            try {
                if (((Piece) conn.front().item()).getColor() == WHITE) {
                    whiteTotal += conn.length()-1;
                }
                else {
                    blackTotal += conn.length()-1;
                }
            }
            catch (InvalidNodeException e) {
                System.out.println("INException in findAllConnections. Shouldn't happen.");
            }
        }
        int[] counts = {whiteTotal, blackTotal};
        return counts;
    }

    /**
     * Returns a DList whose DListNodes contain the Pieces connected to
     * Piece p.
     *
     * @param p
     * @return DList of Pieces
     */
    protected DList findPieceConnections(Piece p) {
        DList connections = new DList();
        //Checks for connections in all diagonal and orthogonal directions.
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 || j != 0) {
                    Piece p2 = connInDirection(p, i, j);
                    if (p2 != null) {
                        connections.insertBack(p2);
                    }
                }
            }
        }
        return connections;
    }

    /**
     * Returns a connected piece in the direction determined by yDelt and
     * xDelt. For example, if looking for a connected Piece in the up
     * direction, do yDelt 1, xDelt 0.
     *
     * @param p
     * @param yDelt
     * @param xDelt
     * @return connected Piece or null
     */

    protected Piece connInDirection(Piece p, int xDelt, int yDelt) {
        int x = p.getX() + xDelt, y = p.getY() + yDelt;
        while (inBound(x, y)) {
            if (p.getColor() == getPiece(x, y).getColor()) {
                return getPiece(x, y);
            }
            else if (getPiece(x, y).getColor() != EMPTY) {
                return null;
            }
            x += xDelt;
            y += yDelt;
        }
        return null;
    }

    /* ============================== EVALUATION MODULE ===================================*/

    /**
     * Find pieces which can be connected with the given piece currentPiece
     * the pieces shouldn't be in the start goal area
     * the connection shouldn't be break by opponent's chips
     * If no edges can be found or currentPiece is in the end area, return empty list
     * @param currentPiece, the given piece
     * @Return edges
     */
    protected DList findEdges(Piece currentPiece, int player){
        DList edges = new DList();
        if (isInEndGoalArea(currentPiece.getX(), currentPiece.getY(), player)){
            return edges;
        } // reach the end area, no more edges

        for (int[] direction: DIRECTIONS){
            int step = 1;
            int x = direction[0]*step + currentPiece.getX();
            int y = direction[1]*step + currentPiece.getY();
            while (inBound(x, y)){
                Piece nextPiece = pieces[x][y];
                if (nextPiece.getColor() == otherPlayer(player)){break;}// reach opponent's chip, change directions
                else if (nextPiece.getColor() == player){ //run into a piece with the same color
                    if (!isInStartGoalArea(x, y, player)) {
                        edges.insertBack(nextPiece);
                    }
                    break;
                }
                step++; // increase step, continue searching
                x = direction[0]*step + currentPiece.getX();
                y = direction[1]*step + currentPiece.getY();
            }
        }
        return edges;
    }

    /**
     * Return a Dlist of pieces which can be used as the start piece
     * Return an empty DList if no edges can be found in the start area
     * @Return startPieces
     */
    protected DList findStart(int player){
        DList edges = new DList();
        if (player == WHITE){
            for (int y = 1; y < length-1; y++){//exclude corners
                if (pieces[0][y].getColor() == player){edges.insertBack(pieces[0][y]);}
            }
        }

        else if (player == BLACK)
            for (int x = 1; x < length-1; x++){//exclude corners
                if (pieces[x][0].getColor() == player){edges.insertBack(pieces[x][0]);}
            }

        return edges;
    }

    /**
     * Return a Dlist of pieces which can be used as the end piece
     * Return an empty DList if no edges can be found in the end area
     * @Return endPieces
     */
    protected DList findEnd(int player){
        DList edges = new DList();
        if (player == WHITE){
            for (int y = 1; y < length-1; y++){//exclude corners
                if (pieces[length-1][y].getColor() == player){
                    edges.insertBack(pieces[length-1][y]);
                }
            }
        }

        else if (player == BLACK)
            for (int x = 1; x < length-1; x++){//exclude corners
                if (pieces[x][length-1].getColor() == player){
                    edges.insertBack(pieces[x][length-1]);
                }
            }

        return edges;
    }

    /**
     * A recursive function to store all paths from 'from' to 'to'.
     *     visited keeps track of vertices in current path.
     *     path stores actual vertices
     *     Return a Dlist of all paths/connections starting at currentPiece
     * @param from
     *      the starting piece
     * @param to
     *      the destination piece
     * @param visited
     *      hashtable: key: piece, value: true if the piece was visited; otherwise false
     * @param path
     *      the current path
     * @param allPaths
     *      a DList storing all possible paths
     */
    protected void findPathsUtil(Piece from, Piece to, Hashtable <Piece, Boolean> visited,
                                 DList path, DList allPaths, int player){
        // Mark the current node as visited and store in path
        visited.put(from, true);
        path.insertBack(from);

        // If current vertex is same as destination, then add current path to allPaths
        if (from.equals(to)){
            allPaths.insertBack(path.clone());
        }
        else {
            // If current vertex is not destination
            //Recur for all the vertices adjacent to this vertex
            DList edges = findEdges(from, player);
            for (DListNode next:edges) {
                Piece nextPiece = null;
                try {
                    nextPiece = (Piece) next.item();
                } catch (InvalidNodeException e) {
                    System.out.println("Invalid node in findPathsUtil() when finding " +
                            "next piece");
                    e.printStackTrace();
                }
                if (path.hasItem(nextPiece)){continue;} // cannot have one item twice in the path
                if (visited.get(nextPiece) == null || visited.get(nextPiece) == false){
                    this.findPathsUtil(nextPiece, to, visited, path, allPaths, player);}
            }
        }
        // Remove current vertex from path and mark it as unvisited
        try {
            path.back().remove();
        } catch (InvalidNodeException e) {
            System.out.println("Invalid node in findPathsUtil() when removing last item " +
                    "in the path");
            e.printStackTrace();
        }
        visited.put(from, false);
    }

    /**
     * Return a Dlist of all paths/connections from 'from' to 'to'
     * @param from
     *      the starting piece
     * @param to
     *      the destination piece
     * @param visited
     *      hashtable: key: piece, value: true if the piece was visited; otherwise false
     * @param allPaths
     */
    protected void findPaths(Piece from, Piece to, Hashtable <Piece, Boolean> visited,
                              DList allPaths, int player){
        DList path = new DList();
        findPathsUtil(from, to, visited, path, allPaths, player);
    }

    /**
     * Return a Dlist of all paths/connections from 'from' to 'to'
     * Does not consider path's length and directions
     */
    protected DList findPaths(int player){
        DList allPaths = new DList();
        Hashtable <Piece, Boolean> visited;

        for (DListNode start: findStart(player)){
            Piece from = null;
            try {
                from = (Piece) start.item();
            } catch (InvalidNodeException e) {
                System.out.println("Invalid node in findPaths() when finding start piece.");
                e.printStackTrace();
            }
            for (DListNode end: findEnd(player)){
                Piece to = null;
                try {
                    to = (Piece) end.item();
                } catch (InvalidNodeException e) {
                    System.out.println("Invalid node in findPaths() when finding end piece.");
                    e.printStackTrace();
                }
                visited = new Hashtable <Piece, Boolean>();
                findPaths(from, to, visited, allPaths, player);
            }
        }
        return allPaths;
    }

    /**
     * find networks for winning, if there are winning networks, return a list consists of all winning networks;
     * otherwise, return an empty list.
     * @param allPaths
     * @return allPaths
     */
    protected DList findNetworks(DList allPaths){
        for (ListNode pathNode:allPaths){
            DList path = null;
            try {
                path = (DList) pathNode.item();
            } catch (InvalidNodeException e) {
                System.out.println("Invalid node in findPaths() when getting path from allPaths.");
                e.printStackTrace();
            }
            if (path.length() < 6){
                try {
                    pathNode.remove();
                } catch (InvalidNodeException e) {
                    System.out.println("Invalid node in findPaths() when removing paths with length < 6.");
                    e.printStackTrace();
                }
                continue;}//only consider paths with length >= 6
            ListNode prev = path.front();
            ListNode curr = null;
            try {
                curr = prev.next();
            } catch (InvalidNodeException e) {
                e.printStackTrace();
            }
            int prevDirectionX = 0;
            int prevDirectionY = 0;
            int directionX = 0;
            int directionY = 0;

            while(prev.equals(path.back()) == false) {
                try {
                    directionX = (int) Math.signum(((Piece) curr.item()).getX() - ((Piece) prev.item()).getX());
                    directionY = (int) Math.signum(((Piece) curr.item()).getY() - ((Piece) prev.item()).getY());
                } catch (InvalidNodeException e) {
                    System.out.println("Invalid node in findPaths() when getting directions.");
                    e.printStackTrace();
                }

                if (directionX == prevDirectionX && directionY == prevDirectionY){
                    try {
                        pathNode.remove(); // cannot have the same direction
                    } catch (InvalidNodeException e) {
                        System.out.println("Invalid node in findPaths() when removing path with same direction.");
                        e.printStackTrace();
                    }
                    break;
                }
                prev = curr;
                if (prev.equals(path.back())){break;}
                try {
                    curr = curr.next();
                } catch (InvalidNodeException e) {
                    System.out.println("Invalid node in findPaths() when getting next path.");
                }
                prevDirectionX = directionX;
                prevDirectionY = directionY;
            }
        }
        return allPaths;
    }

    /**
     * check if the current player wins the game
     * @return true or false
     */
    protected boolean isWin(int player){
        DList allPaths = findPaths(player);
        allPaths = findNetworks(allPaths);
        if (allPaths.length() < 1){return false;}
        return true;
    }

    /* ============================== END OF NETWORK IDENTIFIER MODULE ===================================*/

    /* ============================== GAME OPERATION MODULE ===================================*/

    /**
     * make Move m if it is valid, and switch to the next player
     *
     * @param m
     * @return true if it makes move, else false
     */
    public boolean makeMove(Move m, int player) {
        if (m.moveKind == Move.QUIT) {
            return true;
        }
        if (isValidMove(m, player)) {
            if (m.moveKind == Move.STEP) {
                removePiece(getPiece(m.x2,m.y2));
            }
            addPiece(new Piece(m.x1, m.y1, player));
            nextPlayer = otherPlayer(player);
            numMoves++;
            return true;
        }
        return false;
    }

    /**
     * Undo the Move m, and switch to the previous player
     *
     * @param m
     */
    protected void undoMove(Move m, int player) {
        if (m.moveKind == Move.STEP) {
            pieces[m.x2][m.y2] = new Piece(m.x2, m.y2, player);
        }
        pieces[m.x1][m.y1] = new Piece(m.x1, m.y1, EMPTY);
        nextPlayer = otherPlayer(player);
        numMoves--;
    }

    /**
     * Returns the current player's color.
     * @return the player's color
     */
    protected int getNextPlayer() {
        return nextPlayer;
    }

    /**
     * Returns the number of moves that have occurred.
     * @return the number of moves
     */
    protected int getNumMoves() {
        return numMoves;
    }
    /* ============================== END OF GAME OPERATION MODULE ===================================*/


    /* ============================== VERIFICATION MODULE ===================================*/

    /**
     * Add the piece in the board.
     * @param p
     *   the piece to be added
     */
    protected void addPiece(Piece p){
        pieces[p.getX()][p.getY()] = p;
    }

    /**
     * Add the piece with certain color at position (x, y) in the board.
     * @param x
     *      the x-coordinate of the piece
     * @param y
     *      the y-coordinate of the piece
     * @param color
     *      the color of the piece
     */
    protected void addPiece(int x, int y, int player){
        Piece p = new Piece(x, y, player);
        this.addPiece(p);
    }

    /**
     * Remove the piece in the board.
     * @param p
     *   the piece to be removed
     */
    protected void removePiece(Piece p){
        pieces[p.getX()][p.getY()].setColor(EMPTY);
    }

    /**
     * Check if it is valid to operate the move
     * @param m
     *   the move to be performed
     */
    private boolean isValidMove(Move m, int player){
        // quit
        if (m.moveKind == m.QUIT){
            return true;
        }
        // can only step after 20 moves and only add <= 20 moves
        if ((m.moveKind == Move.STEP && numMoves < 20) || (m.moveKind == Move.ADD && numMoves >= 20)) {
            return false;
        }

        // add piece
        if (m.moveKind == Move.ADD){
            Piece newPiece = new Piece(m.x1, m.y1, player);
            return (isValidAddPiece(newPiece, player));
        }

        // step piece
        else{
            Piece oldPiece = this.getPiece(m.x2, m.y2);
            if (oldPiece.getColor() != player) {
                return false;
            }
            Piece newPiece = new Piece(m.x1, m.y1, player);
            // have to change the clip
            if (oldPiece.getX() == newPiece.getX() && oldPiece.getY() == newPiece.getY()){
                return false;
            }
            return (isValidRemovePiece(oldPiece, player) && isValidAddPiece(newPiece, player));
        }
    }

    /**
     * Get a Dlist of player's pieces on the board
     * @Return existPieces
     */
    protected DList getExistPieces(int player){
        DList existPieces = new DList();

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                if (pieces[i][j].getColor() == player) {
                    existPieces.insertBack(pieces[i][j]);
                }
            }
        }
        return existPieces;
    }

    /**
     * Return a Dlist of empty pieces ont he board
     * @Return emptyPieces
     */
    protected DList getEmptyPieces(){
        DList emptyPieces = new DList();

        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                if (pieces[i][j].getColor() == Piece.EMPTY) {
                    emptyPieces.insertBack(pieces[i][j]);
                }
            }
        }
        return emptyPieces;
    }

    /**
     * Return a Dlist of valid moves
     * @Return validMoves
     */
    protected DList validMoves(int player){
        //record all existed pieces (of the same color)
        DList validMoves = new DList();

        //if number of moves < 20, add piece
        if (numMoves < 20) {
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces.length; j++) {
                    Move m = new Move(pieces[i][j].getX(), pieces[i][j].getY());
                    if (isValidMove(m, player)){validMoves.insertBack(m);}
                }
            }
        } else {//if number of moves >= 20, step piece
            DList existPiece = getExistPieces(player);
            DList emptyPiece = getEmptyPieces();

            try {
                for (DListNode from: existPiece){
                    Piece fromPiece = ((Piece) (from.item()));
                    for (DListNode to: emptyPiece){
                        Piece toPiece = ((Piece) (to.item()));
                        Move m = new Move(toPiece.getX(), toPiece.getY(), fromPiece.getX(), fromPiece.getY());
                        if (isValidMove(m, player)){
                            validMoves.insertBack(m);
                        }
                    }
                }
            }catch (InvalidNodeException e){
                System.out.println("InvalidNodeException in validMoves.");
            }
        }
        return validMoves;
    }

    /**
     * Check if it is valid to add the piece in the board
     * @param p
     *   the piece to be added
     */
    private boolean isValidAddPiece(Piece p, int player) {
        int x = p.getX();
        int y = p.getY();
        int color = p.getColor();

        // check status: must be among BLACK, WHITE and EMPTY
        if (color != player){
            return false;
        }

        // check position: mustn't be at the corners
        if (isAtCorner(x, y)){
            return false;
        }

        // check position: mustn't be in the opponent's goal area
        if (color == WHITE && isInBlackGoalArea(x, y)){
            return false;
        }
        if (color == BLACK && isInWhiteGoalArea(x, y)){
            return false;
        }

        // check position: mustn's be in an occupied square
        if (pieces[x][y].getColor() != EMPTY){
            return false;
        }

        // check cluster: cannot cause cluster
        if (isCluster(p)){
            return false;
        }

        return true;
    }

    /**
     * Check if it is valid to remove the piece in the board
     * @param p
     *   the piece to be removed
     */
    private boolean isValidRemovePiece(Piece p, int player){
        int x = p.getX();
        int y = p.getY();
        int color = p.getColor();

        // check status: cannot remove an EMPTY piece
        if (color != player){
            return false;
        }

        // check position: mustn't be at the corners
        if (isAtCorner(x, y)){
            return false;
        }

        // check position: mustn't be in the opponent's goal area
        if (color == WHITE && isInBlackGoalArea(x, y)){
            return false;
        }
        if (color == BLACK && isInWhiteGoalArea(x, y)){
            return false;
        }

        return true;
    }

    /**
     * Check if position (x, y) is at the corners
     */
    private boolean isAtCorner(int x, int y){
        if ((x == 0 && y == 0)|| (x == 0 && y == length-1) ||
                (x == length-1 && y == 0) || (x == length-1 && y == length - 1)){
            return true;
        }
        return false;
    }

    /**
     * Check if position (x, y) is in the black goal area
     */
    private boolean isInBlackGoalArea(int x, int y){
        if ((y == 0 || y == length-1) && x > 0 && x < length-1){
            return true;
        }
        return false;
    }

    /**
     * Check if position (x, y) is in the white goal area
     */
    private boolean isInWhiteGoalArea(int x, int y){
        if ((x == 0 || x == length-1) && y > 0 && y < length-1){
            return true;
        }
        return false;
    }

    /**
     * Check if position (x, y) is in the start goal area
     * (top line for black piece, and left line for white piece)
     */
    private boolean isInStartGoalArea(int x, int y, int player){
        if (player == BLACK && y == 0) {return true;}
        else if (player == WHITE && x == 0){return true;}
        return false;
    }

    /**
     * Check if position (x, y) is in the end goal area
     * (bottom line for black piece, and right line for white piece)
     */
    private boolean isInEndGoalArea(int x, int y, int player){
        if (player == BLACK && y == length-1) {return true;}
        else if (player == WHITE && x == length-1){return true;}
        return false;
    }

    /**
     * Check if the position (x, y) is in the board.
     * @param x
     *   the x-coordinate of the piece
     * @param y
     *   the y-coordinate of the piece
     *
     * @return true is (x, y) is in the board
     */
    private boolean inBound(int x, int y){
        if(x < 0 || x >= length || y < 0 || y >= length){
            return false;
        }
        return true;
    }

    /**
     * Get neighbors of piece p, get out-of-boundary piece returns null
     * @return p's neighbors: Piece[8]
     */
    protected Piece[] getNeighbors(Piece p){
        Piece[] neighbors = new Piece[8];
        int index = 0;

        for(int i = p.getX()-1; i <= p.getX()+1; i++){
            for(int j = p.getY()-1; j <= p.getY()+1; j++){
                if (i == p.getX() && j == p.getY()){continue;} // exclude the piece itself
                neighbors[index] = this.getPiece(i, j);
                index++;
            }
        }
        return neighbors;
    }

    /**
     * Check if adding p can make clusters (three adjacent pieces of the same color)
     */
    protected boolean isCluster(Piece p){
        int count = 0;

        for(Piece p1: getNeighbors(p)){
            if (p1 == null){continue;}
            if (p1.getColor() != p.getColor()){continue;}
            else { // have a neighbor with same color
                count++;
            }

            if (count > 1){
                return true; // form a cluster centered at p
            }

            for (Piece p2: getNeighbors(p1)){
                if (p2 != null && (! p2.equals(p)) && p2.getColor() == p1.getColor()){
                    return true;
                } // form a cluster centered at p1
            }
        }

        return false;
    }

    /* ============================== END OF VERIFICATION MODULE ===================================*/

    /**
     * Returns a String representation of the board.
     */
    public String toString() {
        String output = "--------------------------------\n";
       for(int j = 0; j < this.length; j++){
           output += "\n|";
           for(int i = 0; i < this.length; i++){
               int color = pieces[i][j].getColor();
               String status = "";
               if (color == BLACK){
                   status = " B |";
               }else if (color == WHITE){
                   status = " W |";
               }else if (color == EMPTY){
                   status = "   |";
               }
               output += status;
           }
           output += "\n--------------------------------";
       }
       return output;
    }

}
