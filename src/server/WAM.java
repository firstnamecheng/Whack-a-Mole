package server;

/**
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAM {

    private int rows;
    private int cols;
    private int[] scores;

    public WAM( int rows, int cols, int numPlayers ) {
        this.rows = rows;
        this.cols = cols;
        scores = new int[ numPlayers ];
        for ( int i = 0; i < numPlayers; i++ ) {
            scores[i] = 0;
        }
    }



}
