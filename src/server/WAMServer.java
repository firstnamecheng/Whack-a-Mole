package server;

import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * The class that is ran to start all server sided operations.
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMServer implements Runnable, WAMProtocol {

    /** Number of rows */
    private int rows;

    /** Number of columns */
    private int cols;

    /** Number of players */
    private int numPlayers;

    /** Amount of game time, in seconds */
    private int gameTime;

    /** The server socket */
    private ServerSocket server;

    /** The list of client sockets */
    private ArrayList<WAMPlayer> players;

    /**
     * Constructor for the WAMServer
     *
     * @param args command line arguments
     */
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

    /**
     * Continuously accepts new connections until
     * the correct number of players are met.
     * Then creates the WAMGame and starts the game.
     */
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

    /**
     * Main method that checks for the correct number of args,
     * and then creates the WAMServer
     *
     * @param args arguments from the command line
     */
    public static void main( String[] args ) {
        if (args.length != 5) {
            System.out.println("Usage: java WAMGServer [port] [rows] [cols] [players] [game time]");
            System.exit(-1);
        }
        WAMServer server = new WAMServer( args );
        server.run();
    }


}
