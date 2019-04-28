package server;

import java.util.ArrayList;

/**
 * Server sided WAM board, contains all information
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAM {

    /** Array of scores for all players */
    private int[] scores;

    /** True if mole up, false if mole down */
    private boolean[] moleUp;

    /** Not initialized until game ends */
    private int highestScore;

    /** The class that contains the game loop */
    private WAMGame game;

    /**
     * Constructor that makes a new WAM object,
     * which stores all data and logic about the game.
     * @param rows number of rows
     * @param cols number of columns
     * @param numPlayers number of players
     * @param game the WAMGame which contains the loop that runs the game
     */
    public WAM( int rows, int cols, int numPlayers, WAMGame game ) {
        this.game = game;
        scores = new int[ numPlayers ];
        moleUp = new boolean[ rows * cols ];
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

    /**
     * Returns a list of players with the highest score
     * @return list of player IDs
     */
    public ArrayList<Integer> getWinnerIDs() {
        ArrayList<Integer> winnerIDs = new ArrayList<>();

        for ( int ID = 0; ID < scores.length; ID++ ) {
            if ( scores[ID] == highestScore ) {
                winnerIDs.add( ID );
            }
        }

        return winnerIDs;
    }

    /**
     * Returns a list of players that did not win
     * @return list of player IDs
     */
    public ArrayList<Integer> getLoserIDs() {
        ArrayList<Integer> loserIDs = new ArrayList<>();

        for ( int ID = 0; ID < scores.length; ID++ ) {
            if ( scores[ID] != highestScore ) {
                loserIDs.add( ID );
            }
        }

        return loserIDs;
    }

    /**
     * Called by the listeners to whack a mole.
     * @param moleID Mole to whack
     * @param playerID player that did the whacking
     */
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


}
