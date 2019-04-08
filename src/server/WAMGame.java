package server;

import java.io.IOException;
import java.util.ArrayList;

public class WAMGame extends Thread {

    private int gameTime;
    private ArrayList<WAMPlayer> players;
    private WAM wam;

    public WAMGame ( int rows, int cols, int gameTime, ArrayList<WAMPlayer> players ) {
        this.gameTime = gameTime;
        this.players = players;
        wam = new WAM( rows, cols, players.size() );
    }

    @Override
    public void run() {
        try {
            for ( WAMPlayer player : players ) {
                player.startListener( this );
            }
            sleep( gameTime * 1000 );
        }
        catch ( IOException e ) {
            System.err.println( "Error setting up input stream for players." );
        }
        catch ( InterruptedException e ) {
            System.err.println( "Error with sleep over duration of game time." );
        }

    }
}
