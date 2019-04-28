package server;

import common.WAMProtocol;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * WAMListener is the class that listens for
 * client WHACK responses
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMListener implements Runnable {

    /** Scanner that reads the input stream from a client */
    private Scanner input;

    /** WAM object that contains game data and game logic */
    private WAM wam;

    /** The WAMGame which contains the loop that runs the game */
    private WAMGame game;

    /**
     * Constructor for a listener that listens to inputs from
     * one client/player
     * @param player the client socket for the player
     * @param wam WAM object that contains game data and game logic
     * @param game The WAMGame which contains the loop that runs the game
     * @throws IOException if there is an error retrieving the input stream
     */
    public WAMListener( Socket player, WAM wam, WAMGame game ) throws IOException {
        input = new Scanner( player.getInputStream() );
        this.wam = wam;
        this.game = game;
    }

    /**
     * Receives WHACK responses from the client,
     * and tells the WAM object to modify its data values.
     */
    @Override
    public void run() {
        while( input.hasNextLine() ) {
            try {
                String[] whack = input.nextLine().split( " " );
                if ( !whack[0].equals( WAMProtocol.WHACK ) ) {
                    game.error( "Protocol != WHACK" );
                }
                else {
                    int moleID = Integer.parseInt( whack[1] );
                    int playerID = Integer.parseInt( whack[2] );
                    wam.whack( moleID, playerID );
                }
            }
            catch ( Exception e ) {
                game.error( e.getMessage() );
            }
        }
    }
}
