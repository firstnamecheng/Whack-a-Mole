package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * WAMPlayer is the class representation of a player.
 * Can send messages or stop communication with a player.
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMPlayer {

    /** The client socket for the player */
    private Socket player;

    /** The PrintStream that prints to the output stream */
    private PrintStream output;

    /**
     * Shuts down the input stream for the socket
     */
    public void shutdownInput() {
        try {
            player.shutdownInput();
        }
        catch ( IOException e ) {
            System.out.println( "Error shutting down player input.");
        }
    }

    /**
     * Shuts down the input stream for the socket
     */
    public void shutdownOutput() {
        try {
            player.shutdownOutput();
        }
        catch ( IOException e ) {
            System.out.println( "Error shutting down player output.");
        }
    }

    /**
     * Constructor for the class that represents a player
     *
     * @param player client socket of the player
     * @throws IOException when an error occurs with getting the output stream
     */
    public WAMPlayer( Socket player ) throws IOException {
        this.player = player;
        output = new PrintStream( player.getOutputStream() );
    }

    /**
     * Sends a message to the player
     *
     * @param message message to send
     */
    public void send( String message ) {
        output.println( message );
        output.flush();
    }

    /**
     * Starts the listener for player inputs
     *
     * @param wam WAM object that contains game data and game logic
     * @param game The WAMGame which contains the loop that runs the game
     * @throws IOException when an IOException is thrown within the WAMListener
     */
    public void startListener( WAM wam, WAMGame game ) throws IOException {
        Thread listener = new Thread( new WAMListener( player, wam, game ) );
        listener.start();
    }

}
