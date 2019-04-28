package server;

import common.WAMProtocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Class that represents the game running.
 * Creates WAM( board ), starts listeners.
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMGame {

    /** Amount of time, in seconds, that the game runs for */
    private int gameTime;

    /** Number of moles */
    private int numMoles;

    /** Number of players */
    private ArrayList<WAMPlayer> players;

    /** WAM object that contains game data and game logic */
    private WAM wam;

    /** True if game is running (time is not up) */
    private boolean running;

    /** Null if there is no error message */
    private String errorMessage;

    /**
     * Constructor for the WAMGame object
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param gameTime game time, in seconds
     * @param players number of players
     */
    public WAMGame ( int rows, int cols, int gameTime, ArrayList<WAMPlayer> players ) {
        this.gameTime = gameTime;
        this.numMoles = rows * cols;
        this.players = players;
        wam = new WAM( rows, cols, players.size(), this );
        running = true;
        errorMessage = null;
    }

    /**
     * Sends scores to all players
     */
    public void updateScores() {
        int[] playerScores = wam.getScores();

        String scores = "";
        for ( int score: playerScores ) {
            scores += " " + score;
        }
        for ( WAMPlayer player: players ) {
            player.send( WAMProtocol.SCORE + scores );
        }
    }

    /**
     * Tells a specific mole to pop up
     * @param moleID the ID of the mole to pop up
     */
    public void moleUp( int moleID ) {
        wam.moleUp( moleID );
        for ( WAMPlayer player: players ) {
            player.send( WAMProtocol.MOLE_UP + " " + moleID );
        }
    }

    /**
     * Tells a specific mole to hide
     * @param moleID the ID of the mole to go back down
     */
    public void moleDown( int moleID ) {
        wam.moleDown( moleID );
        for ( WAMPlayer player: players ) {
            player.send( WAMProtocol.MOLE_DOWN + " " + moleID );
        }
    }

    /**
     * Called by listeners to set an error message
     * @param errorMessage the error message
     */
    public void error( String errorMessage ) {
        this.errorMessage = errorMessage;
    }

    /**
     * Called when game time is up
     */
    private void stopRunning() { running = false; }

    /**
     * Tells a random mole to pop up,
     * and after a random amount of time,
     * tell it to go down if it has not been hit.
     */
    private void activateMole() {
        Random r = new Random();

        int moleID = r.nextInt( numMoles );
        if ( !wam.isMoleUp( moleID ) ) {
            moleUp( moleID );
        }

        int moleUptime = r.nextInt( 2 ) + 1;
        try {
            sleep( moleUptime * 1000 );
            if ( wam.isMoleUp( moleID ) ) {
                moleDown( moleID );
            }
        }
        catch ( InterruptedException e ) {
            System.err.println( "Error in sleep() in activeMole()." );
        }
    }

    /**
     * The main loop for this class.
     * Starts game timer, starts random mole activity,
     * and sends game end messages.
     */
    public void startGame() {

        try {
            for ( WAMPlayer player : players ) {
                player.startListener( wam, this );
            }
        }
        catch ( IOException e ) {
            System.err.println( "Error setting up input stream for players." );
        }

        Thread gameTimer = new Thread( () -> {
            try {
                sleep( gameTime * 1000 );
                stopRunning(); }
            catch ( InterruptedException e ) {
                System.out.println( "gameTimer sleep error." );
            }
        } );

        System.out.println( "All players have connected. GAME BEGINS!" );
        gameTimer.start();

        while ( running ) {

            if ( errorMessage != null ) {
                stopRunning();
                for ( WAMPlayer player: players ) {
                    player.send( WAMProtocol.ERROR + " " + errorMessage );
                    player.shutdownInput();
                    player.shutdownOutput();
                }
                return;
            }

            Thread moleThread = new Thread( () -> activateMole() );
            moleThread.start();

            // Delay until next mole can pop up
            Random r = new Random();
            int delay = r.nextInt( 12 ) + 3;
            try {
                sleep( delay * 100 );
            }
            catch ( InterruptedException e ) {
                System.err.println( "Error: sleep() in game loop." );
            }
        }

        // To stop buttons from being pressed
        for ( WAMPlayer player: players ) {
            player.shutdownInput();
        }

        stopRunning();
        wam.gameEnd();
        ArrayList<Integer> winnerIDs = wam.getWinnerIDs();
        ArrayList<Integer> loserIDs = wam.getLoserIDs();

        if ( winnerIDs.size() != 1 ) {
            for ( int winnerID: winnerIDs ) {
                players.get( winnerID ).send( WAMProtocol.GAME_TIED );
            }
        }
        else {
            players.get( winnerIDs.get( 0 ) ).send( WAMProtocol.GAME_WON );
        }

        for ( int loserID: loserIDs ) {
            players.get( loserID ).send( WAMProtocol.GAME_LOST );
        }

        for ( WAMPlayer player: players ) {
            player.shutdownOutput();
        }

        System.out.println( "\nGame is over." );
    }
}
