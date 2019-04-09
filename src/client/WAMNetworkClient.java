package client;

import client.gui.WAMGUI;
import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class WAMNetworkClient extends Thread implements WAMProtocol{

    private Socket clientSocket;
    private Scanner networkIn;
    private PrintStream networkOut;
    private WAMBoard board;
    private boolean go;
    private int id;

    private boolean goodToGo(){
        return this.go;
    }
    private void stopGame() {
        this.go = false;
    }

    public WAMNetworkClient(String host, int port ) throws IOException {
        try {
            this.clientSocket = new Socket(host, port);
            this.networkIn = new Scanner(clientSocket.getInputStream());
            this.networkOut = new PrintStream(clientSocket.getOutputStream());
            this.go = true;

            String[] args = networkIn.nextLine().strip().split( " " );
            if ( args[ 0 ].equals( WELCOME ) ) {
                this.board = new WAMBoard( Integer.parseInt( args[ 1 ] ),
                                            Integer.parseInt( args[ 2 ] ),
                                            Integer.parseInt( args[ 3 ] ) );
            }
            else {
                System.err.println( "INCORRECT WELCOME MESSAGE!" );
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WAMBoard getBoard() {
        return board;
    }

    public void whack(int mole) {
        this.networkOut.println(WAMProtocol.WHACK + " " + mole + " " + this.id);
    }

    @Override
    public void run() {
        while ( this.goodToGo() ) {
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
                    go = false;
                    break;
                case GAME_LOST:
                    board.gameLost();
                    go = false;
                    break;
                case GAME_TIED:
                    board.gameTied();
                    go = false;
                    break;
                case ERROR:
                    board.error( args[ 1 ] );
                    go = false;
                    break;
            }

        }
    }

}
