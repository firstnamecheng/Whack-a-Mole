package client;

import client.gui.WAMGUI;

public class WAMBoard {


    private enum Status {
        I_WON, I_LOST, TIE, ERROR;

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

    private Status status;
    private int rows;
    private int cols;

    /**
     * 0 for mole down, 1 for mole up
     */
    private int[] moles;

    /**
     * Array of scores for all players
     */
    private int[] scores;


    WAMGUI view;

    public WAMBoard( int rows, int cols, int numPlayers, WAMGUI view ) {
        this.view = view;
        this.rows = rows;
        this.cols = cols;
        moles = new int[ rows * cols ];
        scores = new int[ numPlayers ];
        for ( int i = 0; i < numPlayers; i++ ) {
            scores[i] = 0;
        }
    }

    public void moleUp( int moleNum ) {
        moles[ moleNum ] = 1;
        view.update();

    }

    public void moleDown( int moleNum ) {
        moles[ moleNum ] = 0;
        view.update();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void updateScores( int[] scores ) {
        this.scores = scores;
        view.update();
    }

    public int[] getScores() {
        return scores;
    }

    public void gameWon() {
        status = Status.I_WON;
        view.update();
    }

    public void gameLost() {
        status = Status.I_LOST;
        view.update();
    }

    public void gameTied() {
        status = Status.TIE;
        view.update();
    }

    public void error( String msg ) {
        status = Status.ERROR;
        status.setMessage( msg );
        view.update();
    }
}
