package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class WAMServer implements Runnable {

    private int rows;
    private int cols;
    private int numPlayers;
    private int gameTime;

    private ServerSocket server;
    private ArrayList<WAMPlayer> players;

    public WAMServer( String[] args ) {
        int port = Integer.parseInt( args[ 0 ] );
        rows = Integer.parseInt( args[ 1 ] );
        cols = Integer.parseInt( args[ 2 ] );
        numPlayers = Integer.parseInt( args[ 3 ] );
        gameTime = Integer.parseInt( args[ 4] );

        try {
            server = new ServerSocket( port );
        }
        catch ( IOException e ) {
            System.out.println( "Error while setting up server.");
        }
    }

    @Override
    public void run() {
        try {
            for ( int n = 0; n < numPlayers; n++ ) {
                players.add( new WAMPlayer( server.accept(),
                                            rows,
                                            cols,
                                            numPlayers,
                                            n ) );
            }
        }
        catch ( IOException e ) {
            System.out.println( "Error while setting up connections with players." );
        }

    }

    public static void main( String[] args ) {
        WAMServer server = new WAMServer( args );
        server.run();
    }


}
