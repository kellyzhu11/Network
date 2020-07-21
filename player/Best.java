package player;
/**
 * A package-private class that stores the best move and its score
 * @
 **/
class Best {
    protected Move move;
    protected double score;

    /**
     * Create a new Best object
     */
    protected Best(){
        move = null;
        score = 0;
    };

}