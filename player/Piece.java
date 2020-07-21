package player;

/** The Piece class represent a piece in the game board.
 *
 */
class Piece {
    private int x;
    private int y;
    private int color;

    // Constants for color
    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public static final int EMPTY = -1;

    /** Create a new Piece with specific color and position
    * @param x
    *   the x-coordinate of the piece
    * @param y
    *   the y-coordinate of the piece
    * @param color
    *   the color of the piece
    * */

    public Piece(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    protected int getX(){
        return this.x;
    }

    protected void setX(int x){
        this.x = x;
    }

    protected int getY(){
        return this.y;
    }

    protected void setY(int y){
        this.y = y;
    }

    protected int getColor(){
        return this.color;
    }

    protected void setColor(int color){
        this.color = color;
    }

    protected boolean equals(Piece p){
        if(this.x == p.getX() && this.y == p.getY() && this.color == p.getColor()){
            return true;
        }
        return false;
    }

    public String toString(){
        String status = "";
        if (color == BLACK){
            status = "Black";
        }else if (color == WHITE){
            status = "White";
        }else if (color == EMPTY){
            status = "Empty";
        }

        return status+"piece at ("+ x + ", " + y + ")";
    }




}
