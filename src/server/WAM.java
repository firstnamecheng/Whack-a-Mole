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

    /** The class that contains the game loop */
    private WAMGame game;

    /** Error message */
    private String errorMessage;

    public WAM( int rows, int cols, int numPlayers, WAMGame game ) {
        this.rows = rows;
        this.cols = cols;
        this.game = game;
        errorMessage = null;
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
        highestScore = -99999;

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
        game.updateScores();
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

    /**
     * Used to check if mole is up or not
     * @param moleID ID of mole
     * @return true if mole is up, false if mole is down
     */
    public boolean isMoleUp( int moleID ) { return moleUp[ moleID ]; }

    /**
     * Called by listeners to set an error message
     * @param errorMessage the error message
     */
    public void error( String errorMessage ) {
        this.errorMessage = errorMessage;
    }

    /**
     * Called by WAMGame to check if there is an error
     * @return true if there is an error message
     */
    public boolean hasError() {
        return errorMessage != null;
    }

    /**
     * Called by WAMGame to retrieve error message to send to players
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
