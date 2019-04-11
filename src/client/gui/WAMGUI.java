package client.gui;

import client.WAMBoard;
import client.WAMNetworkClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class WAMGUI extends Application {
    private WAMBoard board;
    private WAMNetworkClient client;
    private ArrayList<Button> buttons;

    @Override
    public void init() {
        try {
            List<String> args = getParameters().getRaw();

            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));

            try {
                this.client = new WAMNetworkClient(host, port);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            this.board = client.getBoard();
            this.board.addObserver( this );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start (Stage stage) throws Exception {
        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        int position = 0;
        this.buttons = new ArrayList();
        for (int row=0; row<this.board.getRows(); ++row) {
            Button[] buttrow = new Button[this.board.getCols()];
            for (int col=0; col<this.board.getCols(); ++col) {
                Button button = new Button();
                button.setId(String.valueOf(position++));
                buttons.add(button);
                buttrow[col] = button;
                // TODO add event handler and graphics for each button
            }
            gridPane.addRow(row, buttrow);
        }
        borderPane.setCenter(gridPane);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }

    public void refresh() {
        int moleId = this.board.getLastestMole();
        if (board.getMoleStatus(moleId) == 1) {
            // this.buttons.get(moleId).setGraphic();
        }
        else {
            //this.buttons.get(moleId).setGraphic();
        }
    }

    public void update() {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh();
        }
        else {
            Platform.runLater( () -> this.refresh() );
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WAMGUI host port");
            System.exit(-1);
        }
        else {
            Application.launch(args);
        }
    }
}
