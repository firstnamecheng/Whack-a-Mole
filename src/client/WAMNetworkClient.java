package client;

import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * The network client that communicates with the server
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMNetworkClient extends Thread implements WAMProtocol{

    /** The client socket */
    private Socket clientSocket;

    /** The reader for the input stream */
    private Scanner networkIn;

    /** The writer for the output stream */
    private PrintStream networkOut;

    /** The WAM game */
    private WAMBoard board;

    /** ID of player(depends on when the connection is accepted) */
    private int id;

    /**
     * Constructor for the network client
     *
     * @param host host name
     * @param port port number
     */
    public WAMNetworkClient(String host, int port ) {
        try {
            this.clientSocket = new Socket(host, port);
            this.networkIn = new Scanner(clientSocket.getInputStream());
            this.networkOut = new PrintStream(clientSocket.getOutputStream());

            String[] args = networkIn.nextLine().strip().split( " " );
            if ( args[ 0 ].equals( WELCOME ) ) {
                this.board = new WAMBoard( Integer.parseInt( args[ 1 ] ),
                                           Integer.parseInt( args[ 2 ] ),
                                           Integer.parseInt( args[ 3 ] ) );
                id = Integer.parseInt( args[ 4 ] );
            }
            else {
                System.err.println( "INCORRECT WELCOME MESSAGE!" );
            }

        }
        catch ( IOException e ) {
            System.err.println( "IOException: Error connecting to the server" );
        }
    }

    /**
     * Returns the WAM board
     */
    public WAMBoard getBoard() {
        return board;
    }

    /**
     * Called when the user whacks a mole
     * Sends a message to the server
     *
     * @param mole mole number
     */
    public void whack(int mole) {
        this.networkOut.println(WAMProtocol.WHACK + " " + mole + " " + this.id);
    }

    /**
     * Main loop for the network client.
     * Reads every message from the server,
     * and processes each protocol.
     */
    @Override
    public void run() {
        boolean notOver = true;
        while ( notOver ) {
            String[] args = networkIn.nextLine().strip().split( " " );
            switch ( args[ 0 ] ) {
                case MOLE_UP:
                    board.moleUp( Integer.parseInt( args[ 1 ] ) );
                    break;
                case MOLE_DOWN:
                    board.moleDown( Integer.parseInt( args[ 1 ] ) );
                    break;
                case SCORE:
                    int[] scores = new int [ args.length - 1 ];
                    for ( int i = 1; i < args.length; i++ ) {
                        scores[ i - 1 ] = ( Integer.parseInt( args[ i ] ) );
                    }
                    board.updateScores( scores );
                    break;
                case GAME_WON:
                    board.gameWon();
                    notOver = false;
                    break;
                case GAME_LOST:
                    board.gameLost();
                    notOver = false;
                    break;
                case GAME_TIED:
                    board.gameTied();
                    notOver = false;
                    break;
                case ERROR:
                    board.error( args[ 1 ] );
                    notOver = false;
                    break;
            }

        }
    }

}
