package client;

import common.WAMProtocol;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class WAMNetworkClient {

    private Socket clientSocket;
    private Scanner networkIn;
    private PrintStream networkOut;
    private WAMBoard board;
    private boolean go;
    private int id;

    private synchronized boolean goodToGo(){
        return this.go;
    }
    private synchronized void stop() {
        this.go = false;
    }

    public WAMNetworkClient(String host, int port, WAMBoard board) throws Exception{
        try {
            this.clientSocket = new Socket(host, port);
            this.networkIn = new Scanner(clientSocket.getInputStream());
            this.networkOut = new PrintStream(clientSocket.getOutputStream());
            this.board = board;
            this.go = true;

            String request = this.networkIn.next();
            if(!request.equals((WAMProtocol.WELCOME))) {
                throw new Exception("Expected WELCOME from server");
            }
            System.out.println("Connected to the server");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void whack(int mole) {
        this.networkOut.println(WAMProtocol.WHACK + " " + mole + " " + this.id);
    }
}
