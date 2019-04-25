package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMListener implements Runnable {

    private Scanner input;
    private WAM wam;
    private WAMGame game;

    public WAMListener( Socket player, WAM wam, WAMGame game ) throws IOException {
        input = new Scanner( player.getInputStream() );
        this.wam = wam;
        this.game = game;
    }

    @Override
    public void run() {
        while( input.hasNextLine() ) {
            try {
                String[] whack = input.nextLine().split( " " );
                int moleID = Integer.parseInt( whack[1] );
                int playerID = Integer.parseInt( whack[2] );
                wam.whack( moleID, playerID );
            }
            catch ( Exception e ) {
                game.error( e.getMessage() );
            }
        }
    }
}
