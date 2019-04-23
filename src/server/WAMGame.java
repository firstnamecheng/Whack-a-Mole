package server;

import common.WAMProtocol;

import java.io.IOException;
import java.util.ArrayList;

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
    private ArrayList<WAMPlayer> players;
    private WAM wam;
    private boolean running;

    public WAMGame ( int rows, int cols, int gameTime, ArrayList<WAMPlayer> players ) {
        this.gameTime = gameTime;
        this.players = players;
        wam = new WAM( rows, cols, players.size(), this );
        running = true;
    }

    public void updateScores( int[] playerScores ) {
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

    private void stopRunning() { running = false; }

    public void startGame() {

        try {
            for ( WAMPlayer player : players ) {
                player.startListener( wam );
            }

            Thread gameTimer = new Thread( () -> {
                try {
                sleep( gameTime * 1000 );
                stopRunning(); }
                catch ( InterruptedException e ) {
                    System.out.println( "gameTimer sleep" );
                }
            } );

            gameTimer.start();

            }
        catch ( IOException e ) {
            System.err.println( "Error setting up input stream for players." );
        }

        while ( running ) {

        }



    }
}
