package server;

import common.WAMProtocol;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMGame {

    private int gameTime;
    private ArrayList<WAMPlayer> players;
    private WAM wam;

    public WAMGame ( int rows, int cols, int gameTime, ArrayList<WAMPlayer> players ) {
        this.gameTime = gameTime;
        this.players = players;
        wam = new WAM( rows, cols, players.size(), this );
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

    }

    public void startGame() {
        try {
            for ( WAMPlayer player : players ) {
                player.startListener( wam );
            }
        }
        catch ( IOException e ) {
            System.err.println( "Error setting up input stream for players." );
        }


    }
}
