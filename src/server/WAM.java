package server;

import java.util.ArrayList;

/**
 * Server sided WAM board, contains all information
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAM {

    /** Number of rows */
    private int rows;

    /** Number of columns */
    private int cols;

    /** Array of scores for all players */
    private int[] scores;

    /** True if mole up, false if mole down */
    private boolean[] moleUp;

    /** Not initialized until game ends */
    private int highestScore;

    private WAMGame game;

    public WAM( int rows, int cols, int numPlayers, WAMGame game ) {
        this.rows = rows;
        this.cols = cols;
        scores = new int[ numPlayers ];
        moleUp = new boolean[ rows * cols ];
    }


    /**
     * Returns the number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns every player's scores
     */
    public int[] getScores() {
        return scores;
    }

    /**
     * Updates highest score when game ends
     */
    public void gameEnd() {
        highestScore = 0;

        for ( int score: scores ) {
            if ( score > highestScore ) {
                highestScore = score;
            }
        }
    }

    public ArrayList<Integer> getWinnerIDs() {
        ArrayList<Integer> winnerIDs = new ArrayList<>();

        for ( int ID = 0; ID < scores.length; ID++ ) {
            if ( scores[ID] == highestScore ) {
                winnerIDs.add( ID );
            }
        }

        return winnerIDs;
    }

    public ArrayList<Integer> getLoserIDs() {
        ArrayList<Integer> loserIDs = new ArrayList<>();

        for ( int ID = 0; ID < scores.length; ID++ ) {
            if ( scores[ID] != highestScore ) {
                loserIDs.add( ID );
            }
        }

        return loserIDs;
    }

    public synchronized void whack( int moleID, int playerID ) {
        if ( moleUp[ moleID ] == true ) {
            moleUp[ moleID ] = false;
            scores[ playerID ] += 2;
            game.moleDown( moleID );
        }
        else {
           scores[ playerID ] -= 1;
        }
        game.updateScores( scores );
    }

    /**
     * Called by WAMGame every once a while to
     * add make a mole pop up
     * @param moleID id of mole
     */
    public void moleUp( int moleID ) {
        moleUp[ moleID ] = true;
    }

    /**
     * Called by WAMGame after time limit to whack
     * this particular mole has passed
     * @param moleID id of mole
     */
    public void moleDown( int moleID ) {
        moleUp[ moleID ] = false;
    }
}
