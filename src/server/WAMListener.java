package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class WAMListener implements Runnable{

    private Scanner input;
    private WAMGame game;

    public WAMListener( Socket player, WAMGame game ) throws IOException {
        input = new Scanner( player.getInputStream() );
        this.game = game;
    }

    @Override
    public void run() {

        while( input.hasNextLine() ) {

        }

    }
}
