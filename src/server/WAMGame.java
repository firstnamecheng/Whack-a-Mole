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

    private int gameTime;
    private int numMoles;
    private ArrayList<WAMPlayer> players;
    private WAM wam;
    private boolean running;
    private String errorMessage;

    public WAMGame ( int rows, int cols, int gameTime, ArrayList<WAMPlayer> players ) {
        this.gameTime = gameTime;
        this.numMoles = rows * cols;
        this.players = players;
        wam = new WAM( rows, cols, players.size(), this );
        running = true;
        errorMessage = null;
    }

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

    public void moleUp( int moleID ) {
        wam.moleUp( moleID );
        for ( WAMPlayer player: players ) {
            player.send( WAMProtocol.MOLE_UP + " " + moleID );
        }
    }

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

    private void stopRunning() { running = false; }

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
