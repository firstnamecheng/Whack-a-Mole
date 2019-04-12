package client;

import client.gui.WAMGUI;

/**
 * WAMBoard is the class representation of whack-a-mole's game logic.
 * This is the model part of the MVC architecture.
 *
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMBoard {


    /** Different possible statuses of the game */
    private enum Status {
        I_WON, I_LOST, TIE, NOT_OVER, ERROR;

        private String message = null;

        public void setMessage( String message ) {
            this.message = message;
        }

        @Override
        public String toString() {
            return super.toString() +
                    message == null ? "" : ( "(" + this.message + ")" );
        }
    }

    /** Current status of the game */
    private Status status;

    /** Number of rows */
    private int rows;

    /** Number of columns */
    private int cols;

    /** The latest updated mole, either by moleUp or moleDown */
    private int lastMoleId;

    /** true for mole is up, false otherwise */
    private boolean[] moleStatus;

    /** Array of scores for all players */
    private int[] scores;

    /** The observer that is to be notified and updated */
    private WAMGUI view;


    /**
     * Constructor for the WAMBoard object
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param numPlayers number of players
     */
    public WAMBoard( int rows, int cols, int numPlayers ) {
        this.rows = rows;
        this.cols = cols;
        moleStatus = new boolean[ rows * cols ];
        scores = new int[ numPlayers ];
        for ( int i = 0; i < numPlayers; i++ ) {
            scores[i] = 0;
        }
        status = Status.NOT_OVER;
    }

    /**
     * Called by the view to add itself as an observer
     *
     * @param view the GUI that that player interacts with
     */
    public void addObserver( WAMGUI view ) {
        this.view = view;
    }

    /**
     * Called by the network client to indicate
     * that a new mole popped up
     *
     * @param moleNum Which mole popped up
     */
    public void moleUp( int moleNum ) {
        moleStatus[ moleNum ] = true;
        lastMoleId = moleNum;
        view.update();

    }

    /**
     * Called by the network client to indicate
     * that a new mole disappears
     *
     * @param moleNum Which mole disappeared
     */
    public void moleDown( int moleNum ) {
        moleStatus[ moleNum ] = false;
        lastMoleId = moleNum;
        view.update();
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
     * When the score changes, update the view
     *
     * @param scores array that contains every player's score
     */
    public void updateScores( int[] scores ) {
        this.scores = scores;
        view.update();
    }

    /**
     * Return the ID of the last mole activity
     */
    public int getLastestMole() {
        return this.lastMoleId;
    }

    /**
     * Return the state of the mole( up or down )
     * at the specified position
     *
     * @param moleId specified location of the mole
     * @return true if mole is up
     */
    public boolean isMoleUp( int moleId ) {
        return moleStatus[ moleId ];
    }

    /**
     * Returns every player's scores
     */
    public int[] getScores() {
        return scores;
    }

    /**
     * Called when the player wins
     */
    public void gameWon() {
        status = Status.I_WON;
        view.update();
    }

    /**
     * Called when the player loses
     */
    public void gameLost() {
        status = Status.I_LOST;
        view.update();
    }

    /**
     * Called when the player ties
     */
    public void gameTied() {
        status = Status.TIE;
        view.update();
    }

    /**
     * Called when an error has occurred
     *
     * @param msg error message
     */
    public void error( String msg ) {
        status = Status.ERROR;
        status.setMessage( msg );
        view.update();
    }
}
