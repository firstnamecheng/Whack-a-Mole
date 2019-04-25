package server;

import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMServer implements Runnable, WAMProtocol {

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
            System.err.println( "Error while setting up server.");
        }
        catch ( Exception e ) {
            System.err.println( "Potential error with args" );
        }
        players = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            // Waits for all the players to connect
            for ( int n = 0; n < numPlayers; n++ ) {
                WAMPlayer player = new WAMPlayer( server.accept() );
                players.add( player );
                player.send( WELCOME + " " + rows + " " + cols
                        + " " + numPlayers + " " + n );
                System.out.println( "Player " + n + " has connected." );
            }

            // Starts the game
            WAMGame game = new WAMGame( rows, cols, gameTime, players );
            game.startGame();
            server.close();

        }
        catch ( IOException e ) {
            System.err.println( "Error while setting up connections with players." );
        }

    }

    public static void main( String[] args ) {
        if (args.length != 5) {
            System.out.println("Usage: java WAMGServer [port] [rows] [cols] [players] [game time]");
            System.exit(-1);
        }
        WAMServer server = new WAMServer( args );
        server.run();
    }


}
