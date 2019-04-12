package server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Cheng Ye
 * @author Albert Htun
 */
public class WAMPlayer {

    private Socket player;
    private PrintStream output;

    public WAMPlayer( Socket player ) throws IOException {
        this.player = player;
        output = new PrintStream( player.getOutputStream() );
    }

    public void send( String message ) {
        output.println( message );
        output.flush();
    }

    public void startListener( WAMGame game ) throws IOException {
        Thread listener = new Thread( new WAMListener( player, game ) );
        listener.start();
    }

}
